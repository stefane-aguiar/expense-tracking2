package com.aguiar.expense_tracking2.service

import com.aguiar.expense_tracking2.dto.ExpenseCreateDTO
import com.aguiar.expense_tracking2.dto.ExpenseUpdateDTO
import com.aguiar.expense_tracking2.exception.ResourceNotFoundException
import com.aguiar.expense_tracking2.model.Expense
import com.aguiar.expense_tracking2.model.User
import com.aguiar.expense_tracking2.repository.ExpenseRepository
import com.aguiar.expense_tracking2.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class ExpenseService (
    private val expenseRepository: ExpenseRepository,
    private val userRepository: UserRepository
){
    // Create
    fun createExpense(dto: ExpenseCreateDTO): Expense {
        // 1. Search real user on DataBase
        val user = userRepository.findById(dto.userId)
            .orElseThrow { ResourceNotFoundException("User not found with id: ${dto.userId}") }

        // 2. Converts DTO -> Entity
        val expense = dto.toEntity(user)

        // 3. Save and return
        return expenseRepository.save(expense)

    }


    // Get
    fun getAllExpenses(): List<Expense> {
        return expenseRepository.findAll()
    }

    fun getExpense(expenseId: Long): Expense {
        return expenseRepository.findById(expenseId)
            .orElseThrow { ResourceNotFoundException("Expense not found with id: $expenseId") }
    }

    fun getExpenseByUser(userId: Long): List<Expense> {
        // 1. Validates if user exists
        if (!userRepository.existsById(userId)) {
            throw ResourceNotFoundException("User not found with id: $userId")
        }
        // 2. Return
        return expenseRepository.findByUserId(userId)
    }


    // Update
    fun updateExpense(expenseId: Long, dto: ExpenseUpdateDTO): Expense {
        // 1. Validates if expense exists
        val existingExpense = expenseRepository.findById(expenseId)
            .orElseThrow { ResourceNotFoundException("Expense not found with id: $expenseId") }

        // 2. Updates only the fields which came in the request (PATCH)
        dto.category?.takeIf { it.isNotBlank() }?.let { existingExpense.category = it }
        dto.subCategory?.takeIf { it.isNotBlank() }?.let { existingExpense.subCategory = it }
        dto.description?.let { existingExpense.description = it }
        dto.amount?.let { existingExpense.amount = it }
        dto.date?.let { existingExpense.date = it }

        // 3. Save and return
        return expenseRepository.save(existingExpense)

    }


    // Delete
    fun deleteExpense(expenseId: Long) {
        // 1. Validates if expense exists
        if (!expenseRepository.existsById(expenseId)) {
            throw ResourceNotFoundException("Expense not found with id: $expenseId")
        }
        // 2. Delete
        expenseRepository.deleteById(expenseId)
    }

}