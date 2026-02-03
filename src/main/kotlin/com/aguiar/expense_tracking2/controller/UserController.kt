package com.aguiar.expense_tracking2.controller

import com.aguiar.expense_tracking2.dto.UserCreateDTO
import com.aguiar.expense_tracking2.dto.UserResponseDTO
import com.aguiar.expense_tracking2.dto.UserUpdateDTO
import com.aguiar.expense_tracking2.model.User
import com.aguiar.expense_tracking2.service.UserService
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/users")
@Validated
class UserController (
    private val userService: UserService
){
    // POST
    @PostMapping
    fun createUser(@Valid @RequestBody dto: UserCreateDTO): ResponseEntity<UserResponseDTO> {
        val created = userService.createUser(dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponseDTO.fromEntity(created))
    }

    // GET
    @GetMapping
    fun getAllUsers(): List<UserResponseDTO> {
        return userService.getAllUsers().map(UserResponseDTO::fromEntity)
    }

    @GetMapping("/{userId}")
    fun getUser(@PathVariable @Min(value = 1, message = "User ID must be a positive number") userId: Long): ResponseEntity<UserResponseDTO> {
        // 1. Service returns Entity
        val user = userService.getUser(userId)
        // 2. Convert → DTO
        val response = UserResponseDTO.fromEntity(user)
        // 3. Return
        return ResponseEntity.ok(response)
    }


    // PATCH
    @PatchMapping("/{userId}")
    fun updateUser(@PathVariable @Min(value = 1, message = "User ID must be a positive number") userId: Long, @Valid @RequestBody dto: UserUpdateDTO): UserResponseDTO {
        val updated = userService.updateUser(userId, dto)
        return UserResponseDTO.fromEntity(updated)
    }


    // DELETE
    @DeleteMapping("/{userId}")
    fun deleteUser(@PathVariable @Min(value = 1, message = "User ID must be a positive number") userId: Long) {
        userService.deleteUser(userId)
    }


}