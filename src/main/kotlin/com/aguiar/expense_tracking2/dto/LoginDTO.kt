package com.aguiar.expense_tracking2.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

class LoginDTO (
    @field:Email(message = "Email is invalid")
    val email: String,

    @field:NotBlank(message = "Password is required")
    val password: String
)