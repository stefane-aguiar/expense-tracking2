package com.aguiar.expense_tracking2.controller

import com.aguiar.expense_tracking2.dto.UserCreateDTO
import com.aguiar.expense_tracking2.dto.UserUpdateDTO
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


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var userRepository: UserRepository

    @BeforeEach
    fun setup() {
        userRepository.deleteAll()
    }


    // ----- Tests ----- //


    // ============ CREATE TESTS ============
    @Test
    @DisplayName("POST /users - Should create user and return 201")
    fun shouldCreateUser() {
        // 1. Arrange
        val dto = UserCreateDTO(name = "Steh", email = "steh@email.com", password = "123456")

        // 2. Act & Assert
        mockMvc.perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value("Steh"))
            .andExpect(jsonPath("$.email").value("steh@email.com"))
    }


    @Test
    @DisplayName("POST /users - Should return 400 when name is blank")
    fun shouldReturn400WhenNameIsBlank() {
        // 1. Arrange
        val dto = UserCreateDTO(name = "", email = "steh@email.com", password = "123456")

        // 2. Act & Assert
        mockMvc.perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.errors.name").value("Name is required"))
    }


    @Test
    @DisplayName("POST /users - Should return 400 when email is invalid")
    fun shouldReturn400WhenEmailIsInvalid() {
        // 1. Arrange
        val dto = UserCreateDTO(name = "Steh", email = "invalid_email", password = "123456")

        // 2. Act & Assert
        mockMvc.perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.errors.email").value("Email is invalid"))

    }




    // ============ READ TESTS ============
    @Test
    @DisplayName("GET /users - Should get all users and return 200")
    fun shouldGetAllUsers() {
        // 1. Arrange
        val dtoUser1 = UserCreateDTO(name = "user1", email = "user1@email.com", password = "123456")
        val dtoUser2 = UserCreateDTO(name = "user2", email = "user2@email.com", password = "123456")

        mockMvc.perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoUser1))
        )
        mockMvc.perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoUser2))
        )

        // 2. Act & Assert
        mockMvc.perform(get("/users"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].name").value("user1"))
            .andExpect(jsonPath("$[1].name").value("user2"))
    }


    @Test
    @DisplayName("GET /users/{id} - Should get user by id and return 200")
    fun shouldGetUserById() {
        // 1. Arrange
        val dto = UserCreateDTO(name = "Steh", email = "steh@email.com", password = "123456")

        val result = mockMvc.perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andReturn()

        val responseJson = result.response.contentAsString
        val userId = objectMapper.readTree(responseJson).get("id").asLong()

        // 2. Act & Assert
        mockMvc.perform(get("/users/$userId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Steh"))
            .andExpect(jsonPath("$.email").value("steh@email.com"))
    }


    @Test
    @DisplayName("GET /users/{id} - Should return 404 when user not found")
    fun shouldReturn404WhenUserNotFound() {
        // 1. Act & Assert
        mockMvc.perform(get("/users/999"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("User not found with id: 999"))
    }




    // ============ UPDATE TESTS ============
    @Test
    @DisplayName("PATCH /users/{id} - Should update user and return 200")
    fun shouldUpdateUser() {
        // 1. Arrange
        val dtoUserCreate = UserCreateDTO(name = "Steh", email = "steh@email.com", password = "123456")

        val result = mockMvc.perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoUserCreate))
        ).andReturn()

        val responseJson = result.response.contentAsString
        val userId = objectMapper.readTree(responseJson).get("id").asLong()

        val dtoUserUpdate = UserUpdateDTO(name = "Nina")

        // 2. Act & assert
        mockMvc.perform(
            patch("/users/$userId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoUserUpdate))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Nina"))
            .andExpect(jsonPath("$.email").value("steh@email.com"))
    }


    @Test
    @DisplayName("PATCH /users/{id} - Should return 404 when updating non existent user")
    fun shouldReturn404WhenUpdatingNonExistentUser() {
        // 1. Arrange
        val dto = UserUpdateDTO(name = "Steh")

        // 2. Act & Assert
        mockMvc.perform(
            patch("/users/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("User not found with id: 999"))
    }


    @Test
    @DisplayName("PATCH /users/{id} - Should return 400 when updating with invalid email")
    fun shouldReturn400WhenUpdatingWithInvalidEmail() {
        // 1. Arrange
        val dtoUserCreate = UserCreateDTO(name = "Steh", email = "steh@email.com", password = "123456")

        val result = mockMvc.perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoUserCreate))
        ).andReturn()

        val responseJson = result.response.contentAsString
        val userId = objectMapper.readTree(responseJson).get("id").asLong()

        val dtoUserUpdate = UserUpdateDTO(email = "invalid_email")

        // 2. Act & assert
        mockMvc.perform(
            patch("/users/$userId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoUserUpdate))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.errors.email").value("Email is invalid"))
    }


    @Test
    @DisplayName("PATCH /users/{id} - Should return 400 when updating name as blank")
    fun shouldReturn400WhenUpdatingNameAsBlank() {
        // 1. Arrange
        val dtoUserCreate = UserCreateDTO(name = "Steh", email = "steh@email.com", password = "123456")

        val result = mockMvc.perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoUserCreate))
        ).andReturn()

        val responseJson = result.response.contentAsString
        val userId = objectMapper.readTree(responseJson).get("id").asLong()

        val dtoUserUpdate = UserUpdateDTO(name = "")

        // 2. Act & assert
        mockMvc.perform(
            patch("/users/$userId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoUserUpdate))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.errors.name").value("Name is required"))
    }




    // ============ DELETE TESTS ============
    @Test
    @DisplayName("DELETE /users/{id} - Should delete user and return 200")
    fun shouldDeleteUser() {
        // 1. Arrange
        val dto = UserCreateDTO(name = "Steh", email = "steh@email.com", password = "123456")

        val result = mockMvc.perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andReturn()

        val responseJson = result.response.contentAsString
        val userId = objectMapper.readTree(responseJson).get("id").asLong()

        // 2. Act & Assert
        mockMvc.perform(
            delete("/users/$userId")
        )
            .andExpect(status().isOk)

        mockMvc.perform(
            get("/users/$userId")
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("User not found with id: $userId"))
    }



    @Test
    @DisplayName("DELETE /users/{id} - Should return 404 when deleting non existent user")
    fun shouldReturn404WhenDeletingNonExistentUser() {
        // 1. Act & Assert
        mockMvc.perform(
            delete("/users/999")
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("User not found with id: 999"))
    }






}