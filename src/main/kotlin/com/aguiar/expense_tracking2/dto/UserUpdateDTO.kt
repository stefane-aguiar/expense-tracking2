package com.aguiar.expense_tracking2.dto

import jakarta.validation.constraints.Email

class UserUpdateDTO(

    var name: String? = null,

    @field:Email(message = "Email is invalid")
    var email: String? = null

)