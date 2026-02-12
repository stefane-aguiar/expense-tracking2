package com.aguiar.expense_tracking2.controller

import com.aguiar.expense_tracking2.dto.ExpenseCreateDTO
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
        userRepository.deleteAll()
        expenseRepository.deleteAll()
    }



    // ----- Tests ----- //


    // ============ CREATE TESTS ============
    @Test
    @DisplayName("POST /expenses - Should create expense and return 201")
    fun shouldCreateExpense() {
        // 1. Arrange
        val dto = UserCreateDTO(name = "Steh", email = "steh@email.com")
        val resultUser = mockMvc.perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andReturn()
        val responseJson = resultUser.response.contentAsString
        val userId = objectMapper.readTree(responseJson).get("id").asLong()


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



    // ============ READ TESTS ============


    // ============ UPDATE TESTS ============


    // ============ DELETE TESTS ============



}