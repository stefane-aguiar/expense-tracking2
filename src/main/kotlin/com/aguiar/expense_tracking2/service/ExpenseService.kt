package com.aguiar.expense_tracking2.service

import com.aguiar.expense_tracking2.dto.ExpenseCreateDTO
import com.aguiar.expense_tracking2.dto.ExpenseResponseDTO
import com.aguiar.expense_tracking2.dto.ExpenseUpdateDTO
import com.aguiar.expense_tracking2.exception.ResourceNotFoundException
import com.aguiar.expense_tracking2.model.Expense
import com.aguiar.expense_tracking2.repository.ExpenseRepository
import com.aguiar.expense_tracking2.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class ExpenseService (
    private val expenseRepository: ExpenseRepository,
    private val userRepository: UserRepository
){

    private val logger = LoggerFactory.getLogger(ExpenseService::class.java)

    // Private helper - sem anotação de cache, pode ser chamado internamente
    private fun findExpenseForUser(expenseId: Long, userId: Long): Expense {
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


    // Create
    @CacheEvict(cacheNames = ["expenses"], key = "#userId")
    fun createExpense(dto: ExpenseCreateDTO, userId: Long): ExpenseResponseDTO {
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
            paymentMethod = dto.paymentMethod,
            amount = dto.amount,
            date = dto.date,
            user = user
        )

        // 3. Save and return
        val saved = expenseRepository.save(expense)
        logger.info("Expense created: id=${saved.id}, amount=${dto.amount}")
        return ExpenseResponseDTO.fromEntity(saved)

    }


    // Get
    @Cacheable("expenses", key = "#userId")
    fun getAllExpenses(userId: Long): List<ExpenseResponseDTO> {
        logger.info("Fetching expenses for userId=$userId")
        return expenseRepository.findByUserIdWithUser(userId)
            .map { ExpenseResponseDTO.fromEntity(it) }
    }

    fun getExpense(expenseId: Long, userId: Long): ExpenseResponseDTO {
        logger.info("Fetching expense id=$expenseId for userId=$userId")
        return ExpenseResponseDTO.fromEntity(findExpenseForUser(expenseId, userId))
    }




    // Update
    @CacheEvict(cacheNames = ["expenses"], key = "#userId")
    fun updateExpense(expenseId: Long, dto: ExpenseUpdateDTO, userId: Long): ExpenseResponseDTO {
        logger.info("Updating expense id=$expenseId for userId=$userId")

        val existingExpense = findExpenseForUser(expenseId, userId)

        // 2. Updates only the fields which came in the request (PATCH)
        dto.category?.takeIf { it.isNotBlank() }?.let { existingExpense.category = it }
        dto.subCategory?.takeIf { it.isNotBlank() }?.let { existingExpense.subCategory = it }
        dto.description?.let { existingExpense.description = it }
        dto.paymentMethod?.takeIf { it.isNotBlank() }?.let {existingExpense.paymentMethod = it }
        dto.amount?.let { existingExpense.amount = it }
        dto.date?.let { existingExpense.date = it }

        // 3. Save and return
        val saved = expenseRepository.save(existingExpense)
        logger.info("Expense updated: id=${saved.id}")
        return ExpenseResponseDTO.fromEntity(saved)
    }


    // Delete
    @CacheEvict(cacheNames = ["expenses"], key = "#userId")
    fun deleteExpense(expenseId: Long, userId: Long) {
        logger.info("Deleting expense id=$expenseId for userId=$userId")

        val expense = findExpenseForUser(expenseId, userId)
        expenseRepository.delete(expense)
        logger.info("Expense deleted: id=$expenseId")
    }

}