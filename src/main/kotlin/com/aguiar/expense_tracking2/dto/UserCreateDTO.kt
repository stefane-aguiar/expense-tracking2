package com.aguiar.expense_tracking2.dto

import com.aguiar.expense_tracking2.model.User
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

class UserCreateDTO(
    @field:NotBlank(message = "Name is required")
    var name: String,

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email is invalid")
    var email: String
){
    fun toEntity(): User {
        return User(name = name, email = email)
    }
}