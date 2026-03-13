package com.aguiar.expense_tracking2.repository

import com.aguiar.expense_tracking2.model.Expense
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ExpenseRepository : JpaRepository<Expense, Long> {
    @Query("SELECT exp FROM Expense exp JOIN FETCH exp.user usr WHERE usr.id = :userId")
    fun findByUserIdWithUser(userId: Long): List<Expense>
}