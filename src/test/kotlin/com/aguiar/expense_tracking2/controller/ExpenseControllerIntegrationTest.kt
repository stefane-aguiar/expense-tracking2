package com.aguiar.expense_tracking2.controller

import com.aguiar.expense_tracking2.dto.ExpenseCreateDTO
import com.aguiar.expense_tracking2.dto.ExpenseUpdateDTO
import com.aguiar.expense_tracking2.dto.UserCreateDTO
import com.aguiar.expense_tracking2.repository.ExpenseRepository
import com.aguiar.expense_tracking2.repository.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tools.jackson.databind.ObjectMapper
import java.time.LocalDate

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ExpenseControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var expenseRepository: ExpenseRepository

    @BeforeEach
    fun setup() {
        expenseRepository.deleteAll()
        userRepository.deleteAll()
    }



    // ----- Tests ----- //


    // ============ CREATE TESTS ============

    @Test
    @DisplayName("POST /expenses - Should create expense and return 201")
    fun shouldCreateExpense() {
        // 1. Arrange
        val userId = createTestUser()

        val dtoExpense = ExpenseCreateDTO(
            category = "Comida",
            subCategory = "Mercado",
            description = "mercado compra do mes",
            amount = 604.87.toBigDecimal(),
            date = LocalDate.now(),
            userId = userId
        )

        // 2. Act & Assert
        mockMvc.perform(
            post("/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoExpense))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.category").value("Comida"))
            .andExpect(jsonPath("$.subCategory").value("Mercado"))
            .andExpect(jsonPath("$.description").value("mercado compra do mes"))
            .andExpect(jsonPath("$.amount").value(604.87))
            .andExpect(jsonPath("$.user.id").value(userId))
    }


    @Test
    @DisplayName("POST /expenses - Should return 400 when category is blank")
    fun shouldReturn400WhenCategoryIsBlank() {
        // 1. Arrange
        val userId = createTestUser()

        val dtoExpense = ExpenseCreateDTO(
            category = "",
            subCategory = "Mercado",
            description = "mercado compra do mes",
            amount = 604.87.toBigDecimal(),
            date = LocalDate.now(),
            userId = userId
        )

        // 2. Act & Assert
        mockMvc.perform(
            post("/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoExpense))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.errors.category").value("Category is required"))
    }


    @Test
    @DisplayName("POST /expenses - Should return 400 when subCategory is blank")
    fun shouldReturn400WhenSubCategoryIsBlank() {
        // 1. Arrange
        val userId = createTestUser()

        val dtoExpense = ExpenseCreateDTO(
            category = "Comida",
            subCategory = "",
            description = "mercado compra do mes",
            amount = 604.87.toBigDecimal(),
            date = LocalDate.now(),
            userId = userId
        )

        // 2. Act & Assert
        mockMvc.perform(
            post("/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoExpense))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.errors.subCategory").value("SubCategory is required"))
    }



    @Test
    @DisplayName("POST /expenses - Should create expense with no description")
    fun shouldCreateExpenseWithNoDescription() {
        // 1. Arrange
        val userId = createTestUser()

        val dtoExpense = ExpenseCreateDTO(
            category = "Comida",
            subCategory = "Mercado",
            amount = 604.87.toBigDecimal(),
            date = LocalDate.now(),
            userId = userId
        )

        // 2. Act & Assert
        mockMvc.perform(
            post("/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoExpense))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.category").value("Comida"))
            .andExpect(jsonPath("$.subCategory").value("Mercado"))
            .andExpect(jsonPath("$.description").isEmpty)
            .andExpect(jsonPath("$.amount").value(604.87))
            .andExpect(jsonPath("$.user.id").value(userId))
    }


    @Test
    @DisplayName("POST /expenses - Should return 404 when creating expense for non existent user")
    fun shouldReturn404WhenCreatingExpenseForNonExistentUser() {
        // 1. Arrange
        val dtoExpense = ExpenseCreateDTO(
            category = "Comida",
            subCategory = "Mercado",
            amount = 604.87.toBigDecimal(),
            date = LocalDate.now(),
            userId = 999L
        )

        // 2. Act & Assert
        mockMvc.perform(
            post("/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoExpense))
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("User not found with id: 999"))
    }





    // ============ READ TESTS ============

    @Test
    @DisplayName("GET /expenses - Should get all expenses and return 200")
    fun shouldGetAllExpenses() {
        // 1. Arrange
        val userId = createTestUser()
        val dtoExpense1 = ExpenseCreateDTO(
            category = "Comida",
            subCategory = "Mercado",
            amount = 604.87.toBigDecimal(),
            date = LocalDate.now(),
            userId = userId
        )
        val dtoExpense2 = ExpenseCreateDTO(
            category = "Transporte",
            subCategory = "Uber",
            amount = 34.13.toBigDecimal(),
            date = LocalDate.now(),
            userId = userId
        )

        mockMvc.perform(
            post("/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoExpense1))
        )
        mockMvc.perform(
            post("/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoExpense2))
        )

        // 2. Act & Assert
        mockMvc.perform(
            get("/expenses")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].category").value("Comida"))
            .andExpect(jsonPath("$[1].category").value("Transporte"))
            .andExpect(jsonPath("$[0].user.id").value(userId))
            .andExpect(jsonPath("$[1].user.id").value(userId))
    }


    @Test
    @DisplayName("GET /expenses/{id} - Should get expense by id and return 200")
    fun shouldGetExpenseById() {
        // 1. Arrange
        val userId = createTestUser()
        val dtoExpense = ExpenseCreateDTO(
            category = "Comida",
            subCategory = "Mercado",
            amount = 604.87.toBigDecimal(),
            date = LocalDate.now(),
            userId = userId
        )

        val result = mockMvc.perform(
            post("/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoExpense))
        ).andReturn()

        val responseJson = result.response.contentAsString
        val expenseId = objectMapper.readTree(responseJson).get("id").asLong()

        // 2. Act & Assert
        mockMvc.perform(
            get("/expenses/$expenseId")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(expenseId))
            .andExpect(jsonPath("$.category").value("Comida"))
            .andExpect(jsonPath("$.user.id").value(userId))
    }


    @Test
    @DisplayName("GET /expenses/user/{userId} - Should get expense by user id and return 200")
    fun shouldGetExpensesByUserId() {
        // 1. Arrange
        val userId = createTestUser()
        val dtoExpense1 = ExpenseCreateDTO(
            category = "Comida",
            subCategory = "Mercado",
            amount = 604.87.toBigDecimal(),
            date = LocalDate.now(),
            userId = userId
        )
        val dtoExpense2 = ExpenseCreateDTO(
            category = "Transporte",
            subCategory = "Uber",
            amount = 34.13.toBigDecimal(),
            date = LocalDate.now(),
            userId = userId
        )

        mockMvc.perform(
            post("/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoExpense1))
        )
        mockMvc.perform(
            post("/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoExpense2))
        )

        // 2. Act & Assert
        mockMvc.perform(
            get("/expenses/user/$userId")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].category").value("Comida"))
            .andExpect(jsonPath("$[1].category").value("Transporte"))
            .andExpect(jsonPath("$[0].user.id").value(userId))
            .andExpect(jsonPath("$[1].user.id").value(userId))
    }


    @Test
    @DisplayName("GET /expenses/{id} - Should return 404 when expense not found")
    fun shouldReturn404WhenExpenseNotFound() {
        // 1. Act & Assert
        mockMvc.perform(
            get("/expenses/999")
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Expense not found with id: 999"))
    }


    @Test
    @DisplayName("GET /expenses/user/{UserId} - Should return 404 when getting expenses by non existent user")
    fun shouldReturn404WhenGettingExpensesByNonExistentUser() {
        // 1. Act & Assert
        mockMvc.perform(
            get("/expenses/user/999")
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("User not found with id: 999"))
    }


    @Test
    @DisplayName("GET /expenses - Should return empty list when no expenses")
    fun shouldReturnEmptyListWhenNoExpenses() {
        // 1. Act & Assert
        mockMvc.perform(
            get("/expenses")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isEmpty)
    }


    @Test
    @DisplayName("GET /expenses/user/{userId} - Should return empty list when user has no expenses")
    fun shouldReturnEmptyListWhenUserHasNoExpenses() {
        // 1. Arrange
        val userId = createTestUser()

        // 2. Act & Assert
        mockMvc.perform(
            get("/expenses/user/$userId")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isEmpty)
    }




    // ============ UPDATE TESTS ============
    @Test
    @DisplayName("PATCH /expenses/{id} - Should update expense and return 200")
    fun shouldUpdateExpense() {
        // 1. Arrange
        val userId = createTestUser()
        val dto = ExpenseCreateDTO(
            category = "Comida",
            subCategory = "Mercado",
            amount = 604.87.toBigDecimal(),
            date = LocalDate.now(),
            userId = userId
        )

        val result = mockMvc.perform(
            post("/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andReturn()

        val responseJson = result.response.contentAsString
        val expenseId = objectMapper.readTree(responseJson).get("id").asLong()

        val dtoUpdated = ExpenseUpdateDTO(category = "Transporte", subCategory = "Uber")

        // 2. Act & Assert
        mockMvc.perform(
            patch("/expenses/$expenseId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoUpdated))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.category").value("Transporte"))
            .andExpect(jsonPath("$.subCategory").value("Uber"))
            .andExpect(jsonPath("$.amount").value(604.87))
    }


    @Test
    @DisplayName("PATCH /expenses/{id} - Should return 404 when updating non existent expense")
    fun shouldReturn404WhenUpdatingNonExistentExpense() {
        // 1. Arrange
        val dtoUpdated = ExpenseUpdateDTO(category = "Transporte", subCategory = "Uber")

        // 2. Act & Assert
        mockMvc.perform(
            patch("/expenses/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoUpdated))
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Expense not found with id: 999"))
    }





    // ============ DELETE TESTS ============

    @Test
    @DisplayName("DELETE /expenses/{id} - Should delete expense and return 200")
    fun shouldDeleteExpense() {
        // 1. Arrange
        val userId = createTestUser()
        val dto = ExpenseCreateDTO(
            category = "Comida",
            subCategory = "Mercado",
            amount = 604.87.toBigDecimal(),
            date = LocalDate.now(),
            userId = userId
        )

        val result = mockMvc.perform(
            post("/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andReturn()

        val responseJson = result.response.contentAsString
        val expenseId = objectMapper.readTree(responseJson).get("id").asLong()

        // 2. Act & Assert
        mockMvc.perform(
            delete("/expenses/$expenseId")
        )
            .andExpect(status().isOk)
        mockMvc.perform(
            get("/expenses/$expenseId")
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Expense not found with id: $expenseId"))
    }



    @Test
    @DisplayName("DELETE /expenses/{id} - Should return 404 when deleting non existent expense")
    fun shouldReturn404WhenDeletingNonExistentExpense() {
        // 1. Act & Assert
        mockMvc.perform(
            delete("/expenses/999")
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Expense not found with id: 999"))
    }








    // === createTestUser

    fun createTestUser(): Long {
        val dto = UserCreateDTO(name = "Steh", email = "steh@email.com", password = "123456")
        val resultUser = mockMvc.perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andReturn()
        val responseJson = resultUser.response.contentAsString
        val userId = objectMapper.readTree(responseJson).get("id").asLong()
        return userId
    }



}