package com.aguiar.expense_tracking2.dto

import com.aguiar.expense_tracking2.model.Expense
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDate

class ExpenseResponseDTO (

    val id: Long,
    val category: String,
    val subCategory: String,
    val description: String?,
    val paymentMethod: String,
    val amount: BigDecimal,
    val date: LocalDate,
    val user: UserSummaryDTO

) : Serializable {
    companion object {
        fun fromEntity(expense: Expense): ExpenseResponseDTO {
            return ExpenseResponseDTO(
                id = expense.id!!,
                category = expense.category,
                subCategory = expense.subCategory,
                description = expense.description,
                paymentMethod = expense.paymentMethod,
                amount = expense.amount,
                date = expense.date,
                user = UserSummaryDTO.fromEntity(expense.user)
            )
        }
    }
}