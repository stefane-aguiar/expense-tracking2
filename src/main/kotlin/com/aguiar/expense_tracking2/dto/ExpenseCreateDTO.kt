package com.aguiar.expense_tracking2.dto

import com.aguiar.expense_tracking2.model.Expense
import com.aguiar.expense_tracking2.model.User
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDate

class ExpenseCreateDTO (

    @field:NotBlank(message = "Category is required")
    var category: String,

    @field:NotBlank(message = "SubCategory is required")
    var subCategory: String,

    var description: String,

    @field:NotNull(message = "Amount is required")
    @field:Positive(message = "Amount must be a positive number")
    var amount: BigDecimal,

    @field:NotNull(message = "Date is required")
    var date: LocalDate,

    @field:NotNull(message = "User ID is required")
    val userId: Long


){
    fun toEntity(user: User): Expense {
        return Expense(
            category = category,
            subCategory = subCategory,
            description = description,
            amount = amount,
            date = date,
            user = user
        )
    }
}