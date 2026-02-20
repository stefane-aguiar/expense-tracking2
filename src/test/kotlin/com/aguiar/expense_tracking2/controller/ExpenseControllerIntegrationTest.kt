package com.aguiar.expense_tracking2.controller

import com.aguiar.expense_tracking2.dto.ExpenseCreateDTO
import com.aguiar.expense_tracking2.dto.ExpenseUpdateDTO
import com.aguiar.expense_tracking2.dto.LoginDTO
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

    private var authToken: String = ""
    private var userId: Long = 0L

    @BeforeEach
    fun setup() {
        expenseRepository.deleteAll()
        userRepository.deleteAll()

        // Create user and get token for authenticated requests
        val result = createTestUserAndLogin()
        userId = result.first
        authToken = result.second
    }



    // ----- Tests ----- //


    // ============ CREATE TESTS ============

    @Test
    @DisplayName("POST /expenses - Should create expense and return 201")
    fun shouldCreateExpense() {
        // 1. Arrange
        val dtoExpense = ExpenseCreateDTO(
            category = "Comida",
            subCategory = "Mercado",
            description = "mercado compra do mes",
            amount = 604.87.toBigDecimal(),
            date = LocalDate.now()
        )

        // 2. Act & Assert
        mockMvc.perform(
            post("/expenses")
                .header("Authorization", "Bearer $authToken")
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
        val dtoExpense = ExpenseCreateDTO(
            category = "",
            subCategory = "Mercado",
            description = "mercado compra do mes",
            amount = 604.87.toBigDecimal(),
            date = LocalDate.now()
        )

        // 2. Act & Assert
        mockMvc.perform(
            post("/expenses")
                .header("Authorization", "Bearer $authToken")
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
        val dtoExpense = ExpenseCreateDTO(
            category = "Comida",
            subCategory = "",
            description = "mercado compra do mes",
            amount = 604.87.toBigDecimal(),
            date = LocalDate.now()
        )

        // 2. Act & Assert
        mockMvc.perform(
            post("/expenses")
                .header("Authorization", "Bearer $authToken")
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
        val dtoExpense = ExpenseCreateDTO(
            category = "Comida",
            subCategory = "Mercado",
            amount = 604.87.toBigDecimal(),
            date = LocalDate.now()
        )

        // 2. Act & Assert
        mockMvc.perform(
            post("/expenses")
                .header("Authorization", "Bearer $authToken")
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
    @DisplayName("POST /expenses - Should return 401 when not authenticated")
    fun shouldReturn401WhenNotAuthenticated() {
        // 1. Arrange
        val dtoExpense = ExpenseCreateDTO(
            category = "Comida",
            subCategory = "Mercado",
            amount = 604.87.toBigDecimal(),
            date = LocalDate.now()
        )

        // 2. Act & Assert (no Authorization header)
        mockMvc.perform(
            post("/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoExpense))
        )
            .andExpect(status().isForbidden)
    }






    // ============ READ TESTS ============

    @Test
    @DisplayName("GET /expenses - Should get all expenses and return 200")
    fun shouldGetAllExpenses() {
        // 1. Arrange
        createExpense("Comida", "Mercado", 604.87)
        createExpense("Transporte", "Uber", 34.13)

        // 2. Act & Assert
        mockMvc.perform(
            get("/expenses")
                .header("Authorization", "Bearer $authToken")
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
        val expenseId = createExpense("Comida", "Mercado", 604.87)

        // 2. Act & Assert
        mockMvc.perform(
            get("/expenses/$expenseId")
                .header("Authorization", "Bearer $authToken")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(expenseId))
            .andExpect(jsonPath("$.category").value("Comida"))
            .andExpect(jsonPath("$.user.id").value(userId))
    }



    @Test
    @DisplayName("GET /expenses/{id} - Should return 404 when expense not found")
    fun shouldReturn404WhenExpenseNotFound() {
        // 1. Act & Assert
        mockMvc.perform(
            get("/expenses/999")
                .header("Authorization", "Bearer $authToken")
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Expense not found with id: 999"))
    }

    @Test
    @DisplayName("GET /expenses/{id} - Should return 404 when trying to access another user's expense")
    fun shouldReturn404WhenAccessingAnotherUsersExpense() {
        // 1. Arrange - create expense with current user
        val expenseId = createExpense("Comida", "Mercado", 604.87)

        // Create another user and get their token
        val otherUserResult = createAnotherUserAndLogin("other@email.com")
        val otherToken = otherUserResult.second

        // 2. Act & Assert - try to access with other user
        mockMvc.perform(
            get("/expenses/$expenseId")
                .header("Authorization", "Bearer $otherToken")
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Expense not found with id: $expenseId"))
    }


    @Test
    @DisplayName("GET /expenses - Should return empty list when no expenses")
    fun shouldReturnEmptyListWhenNoExpenses() {
        // 1. Act & Assert
        mockMvc.perform(
            get("/expenses")
                .header("Authorization", "Bearer $authToken")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isEmpty)
    }



    // ============ UPDATE TESTS ============
    @Test
    @DisplayName("PATCH /expenses/{id} - Should update expense and return 200")
    fun shouldUpdateExpense() {
        // 1. Arrange
        val expenseId = createExpense("Comida", "Mercado", 604.87)
        val dtoUpdated = ExpenseUpdateDTO(category = "Transporte", subCategory = "Uber")

        // 2. Act & Assert
        mockMvc.perform(
            patch("/expenses/$expenseId")
                .header("Authorization", "Bearer $authToken")
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
                .header("Authorization", "Bearer $authToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoUpdated))
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Expense not found with id: 999"))
    }


    @Test
    @DisplayName("PATCH /expenses/{id} - Should return 404 when updating another user's expense")
    fun shouldReturn404WhenUpdatingAnotherUsersExpense() {
        // 1. Arrange
        val expenseId = createExpense("Comida", "Mercado", 604.87)
        val dtoUpdated = ExpenseUpdateDTO(category = "Hacked")

        val otherUserResult = createAnotherUserAndLogin("hacker@email.com")
        val otherToken = otherUserResult.second

        // 2. Act & Assert
        mockMvc.perform(
            patch("/expenses/$expenseId")
                .header("Authorization", "Bearer $otherToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoUpdated))
        )
            .andExpect(status().isNotFound)
    }





    // ============ DELETE TESTS ============

    @Test
    @DisplayName("DELETE /expenses/{id} - Should delete expense and return 200")
    fun shouldDeleteExpense() {
        // 1. Arrange
        val expenseId = createExpense("Comida", "Mercado", 604.87)

        // 2. Act & Assert
        mockMvc.perform(
            delete("/expenses/$expenseId")
                .header("Authorization", "Bearer $authToken")
        )
            .andExpect(status().isOk)

        mockMvc.perform(
            get("/expenses/$expenseId")
                .header("Authorization", "Bearer $authToken")
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
                .header("Authorization", "Bearer $authToken")
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Expense not found with id: 999"))
    }


    @Test
    @DisplayName("DELETE /expenses/{id} - Should return 404 when deleting another user's expense")
    fun shouldReturn404WhenDeletingAnotherUsersExpense() {
        // 1. Arrange
        val expenseId = createExpense("Comida", "Mercado", 604.87)

        val otherUserResult = createAnotherUserAndLogin("deleter@email.com")
        val otherToken = otherUserResult.second

        // 2. Act & Assert
        mockMvc.perform(
            delete("/expenses/$expenseId")
                .header("Authorization", "Bearer $otherToken")
        )
            .andExpect(status().isNotFound)
    }








// ============ HELPER METHODS ============

    private fun createTestUserAndLogin(): Pair<Long, String> {
        return createUserAndLogin("steh@email.com", "Steh", "123456")
    }

    private fun createAnotherUserAndLogin(email: String): Pair<Long, String> {
        return createUserAndLogin(email, "Other User", "123456")
    }

    private fun createUserAndLogin(email: String, name: String, password: String): Pair<Long, String> {
        // Register
        val registerDto = UserCreateDTO(name = name, email = email, password = password)
        val registerResult = mockMvc.perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto))
        ).andReturn()

        val userId = objectMapper.readTree(registerResult.response.contentAsString).get("id").asLong()

        // Login
        val loginDto = LoginDTO(email = email, password = password)
        val loginResult = mockMvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto))
        ).andReturn()

        val token = objectMapper.readTree(loginResult.response.contentAsString).get("token").asText()

        return Pair(userId, token)
    }

    private fun createExpense(category: String, subCategory: String, amount: Double): Long {
        val dto = ExpenseCreateDTO(
            category = category,
            subCategory = subCategory,
            amount = amount.toBigDecimal(),
            date = LocalDate.now()
        )

        val result = mockMvc.perform(
            post("/expenses")
                .header("Authorization", "Bearer $authToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andReturn()

        return objectMapper.readTree(result.response.contentAsString).get("id").asLong()
    }


}