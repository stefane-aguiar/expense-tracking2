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
    fun createExpense(dto: ExpenseCreateDTO, userId: Long): Expense {
        logger.info("Creating expense for userId=${userId}")

        // 1. Search real user on DataBase
        val user = userRepository.findById(userId)
            .orElseThrow {
                logger.warn("User not found with id=$userId")
                ResourceNotFoundException("User not found with id: $userId")
            }

        // 2.
        val expense = Expense(
            category = dto.category,
            subCategory = dto.subCategory,
            description = dto.description,
            amount = dto.amount,
            date = dto.date,
            user = user
        )

        // 3. Save and return
        val saved = expenseRepository.save(expense)
        logger.info("Expense created: id=${saved.id}, amount=${dto.amount}")
        return saved

    }


    // Get
    fun getAllExpenses(userId: Long): List<Expense> {
        logger.info("Fetching expenses for userId=$userId")
        return expenseRepository.findByUserId(userId)
    }

    fun getExpense(expenseId: Long, userId: Long): Expense {
        logger.info("Fetching expense id=$expenseId for userId=$userId")

        val expense = expenseRepository.findById(expenseId)
            .orElseThrow {
                logger.warn("Expense not found with id=$expenseId")
                ResourceNotFoundException("Expense not found with id: $expenseId")
            }

        if (expense.user.id != userId) {
            logger.warn("User $userId tried to access expense $expenseId owned by ${expense.user.id}")
            throw ResourceNotFoundException("Expense not found with id: $expenseId")
        }

        return expense
    }




    // Update
    fun updateExpense(expenseId: Long, dto: ExpenseUpdateDTO, userId: Long): Expense {
        logger.info("Updating expense id=$expenseId for userId=$userId")

        val existingExpense = getExpense(expenseId, userId)

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
    fun deleteExpense(expenseId: Long, userId: Long) {
        logger.info("Deleting expense id=$expenseId for userId=$userId")

        val expense = getExpense(expenseId, userId)
        expenseRepository.delete(expense)
        logger.info("Expense deleted: id=$expenseId")
    }

}