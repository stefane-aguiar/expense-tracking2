package com.aguiar.expense_tracking2.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

class UserUpdateDTO(

    @field:NotBlank(message = "Name is required")
    var name: String? = null,

    @field:Email(message = "Email is invalid")
    var email: String? = null

)