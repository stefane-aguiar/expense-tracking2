package com.aguiar.expense_tracking2.service

import com.aguiar.expense_tracking2.dto.ExpenseCreateDTO
import com.aguiar.expense_tracking2.dto.ExpenseUpdateDTO
import com.aguiar.expense_tracking2.exception.ResourceNotFoundException
import com.aguiar.expense_tracking2.model.Expense
import com.aguiar.expense_tracking2.repository.ExpenseRepository
import com.aguiar.expense_tracking2.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ExpenseService (
    private val expenseRepository: ExpenseRepository,
    private val userRepository: UserRepository
){

    private val logger = LoggerFactory.getLogger(ExpenseService::class.java)


    // Create
    fun createExpense(dto: ExpenseCreateDTO): Expense {
        logger.info("Creating expense for userId=${dto.userId}")

        // 1. Search real user on DataBase
        val user = userRepository.findById(dto.userId)
            .orElseThrow {
                logger.warn("User not found with id=${dto.userId}")
                ResourceNotFoundException("User not found with id: ${dto.userId}")
            }

        // 2. Converts DTO -> Entity
        val expense = dto.toEntity(user)

        // 3. Save and return
        val saved = expenseRepository.save(expense)
        logger.info("Expense created: id=${saved.id}, amount=${dto.amount}")
        return saved

    }


    // Get
    fun getAllExpenses(): List<Expense> {
        logger.info("Fetching all expenses")
        return expenseRepository.findAll()
    }

    fun getExpense(expenseId: Long): Expense {
        logger.info("Fetching expense id=$expenseId")
        return expenseRepository.findById(expenseId)
            .orElseThrow {
                logger.warn("Expense not found with id=$expenseId")
                ResourceNotFoundException("Expense not found with id: $expenseId")
            }
    }

    fun getExpenseByUser(userId: Long): List<Expense> {
        logger.info("Fetching expenses for userId=$userId")

        // 1. Validates if user exists
        if (!userRepository.existsById(userId)) {
            logger.warn("User not found with id=$userId")
            throw ResourceNotFoundException("User not found with id: $userId")
        }
        // 2. Return
        return expenseRepository.findByUserId(userId)
    }


    // Update
    fun updateExpense(expenseId: Long, dto: ExpenseUpdateDTO): Expense {
        logger.info("Updating expense id=$expenseId")

        // 1. Validates if expense exists
        val existingExpense = expenseRepository.findById(expenseId)
            .orElseThrow {
                logger.warn("Expense not found with id=$expenseId")
                ResourceNotFoundException("Expense not found with id: $expenseId")
            }

        // 2. Updates only the fields which came in the request (PATCH)
        dto.category?.takeIf { it.isNotBlank() }?.let { existingExpense.category = it }
        dto.subCategory?.takeIf { it.isNotBlank() }?.let { existingExpense.subCategory = it }
        dto.description?.let { existingExpense.description = it }
        dto.amount?.let { existingExpense.amount = it }
        dto.date?.let { existingExpense.date = it }

        // 3. Save and return
        val saved = expenseRepository.save(existingExpense)
        logger.info("Expense updated: id=${saved.id}")
        return saved

    }


    // Delete
    fun deleteExpense(expenseId: Long) {
        logger.info("Deleting expense id=$expenseId")

        // 1. Validates if expense exists
        if (!expenseRepository.existsById(expenseId)) {
            logger.warn("Expense not found with id=$expenseId")
            throw ResourceNotFoundException("Expense not found with id: $expenseId")
        }
        // 2. Delete
        expenseRepository.deleteById(expenseId)
        logger.info("Expense deleted: id=$expenseId")
    }

}