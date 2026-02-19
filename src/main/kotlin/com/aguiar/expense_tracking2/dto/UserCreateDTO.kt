package com.aguiar.expense_tracking2.dto

import com.aguiar.expense_tracking2.model.User
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

class UserCreateDTO(
    @field:NotBlank(message = "Name is required")
    val name: String,

    @field:Email(message = "Email is invalid")
    val email: String,

    @field:NotBlank(message = "Password is required")
    val password: String
){
    fun toEntity(): User {
        return User(name = name, email = email, password = password)
    }
}