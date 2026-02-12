package com.aguiar.expense_tracking2.controller

import com.aguiar.expense_tracking2.dto.ExpenseCreateDTO
import com.aguiar.expense_tracking2.dto.ExpenseResponseDTO
import com.aguiar.expense_tracking2.dto.ExpenseUpdateDTO
import com.aguiar.expense_tracking2.service.ExpenseService
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/expenses")
@Validated
class ExpenseController (
    private val expenseService: ExpenseService
){
    // POST
    @PostMapping
    fun createExpense(@Valid @RequestBody dto: ExpenseCreateDTO): ResponseEntity<ExpenseResponseDTO> {
        val created = expenseService.createExpense(dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(ExpenseResponseDTO.fromEntity(created))
    }


    // GET
    @GetMapping
    fun getAllExpenses(): List<ExpenseResponseDTO> {
        return expenseService.getAllExpenses().map(ExpenseResponseDTO::fromEntity)
    }

    @GetMapping("/{expenseId}")
    fun getExpense(@PathVariable @Min(value = 1, message = "Expense ID must be a positive number") expenseId: Long): ResponseEntity<ExpenseResponseDTO> {
        // 1. Service returns Entity
        val expense = expenseService.getExpense(expenseId)
        // 2. Convert → DTO
        val response = ExpenseResponseDTO.fromEntity(expense)
        // 3. Return
        return ResponseEntity.ok(response)
    }

    @GetMapping("/user/{userId}")
    fun getExpenseByUser(@PathVariable @Min(value = 1, message = "User ID must be a positive number") userId: Long): List<ExpenseResponseDTO> {
        return expenseService.getExpenseByUser(userId).map(ExpenseResponseDTO::fromEntity)
    }


    // PATCH
    @PatchMapping("/{expenseId}")
    fun updateExpense(@PathVariable @Min(value = 1, message = "Expense ID must be a positive number") expenseId: Long,
                      @Valid @RequestBody dto: ExpenseUpdateDTO): ExpenseResponseDTO {
        val updated = expenseService.updateExpense(expenseId, dto)
        return ExpenseResponseDTO.fromEntity(updated)
    }


    // DELETE
    @DeleteMapping("/{expenseId}")
    fun deleteExpense(@PathVariable @Min(value = 1, message = "Expense ID must be a positive number") expenseId: Long) {
        expenseService.deleteExpense(expenseId)
    }


}