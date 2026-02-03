package com.aguiar.expense_tracking2.repository

import com.aguiar.expense_tracking2.model.Expense
import org.springframework.data.jpa.repository.JpaRepository

interface ExpenseRepository : JpaRepository<Expense, Long> {
    fun findByUserId(userId: Long): List<Expense>
}