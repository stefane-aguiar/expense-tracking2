package com.aguiar.expense_tracking2.dto

import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.time.LocalDate

class ExpenseUpdateDTO (

    var category: String? = null,
    var subCategory: String? = null,
    var description: String? = null,
    var date: LocalDate? = null,

    @field:Positive(message = "Amount must be a positive number")
    var amount: BigDecimal? = null

)