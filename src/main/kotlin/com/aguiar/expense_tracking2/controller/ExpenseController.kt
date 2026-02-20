package com.aguiar.expense_tracking2.controller

import com.aguiar.expense_tracking2.dto.ExpenseCreateDTO
import com.aguiar.expense_tracking2.dto.ExpenseResponseDTO
import com.aguiar.expense_tracking2.dto.ExpenseUpdateDTO
import com.aguiar.expense_tracking2.service.ExpenseService
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
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
    fun createExpense(@Valid @RequestBody dto: ExpenseCreateDTO, authentication: Authentication): ResponseEntity<ExpenseResponseDTO> {
        val userId = authentication.principal as Long
        val created = expenseService.createExpense(dto, userId)
        return ResponseEntity.status(HttpStatus.CREATED).body(ExpenseResponseDTO.fromEntity(created))
    }


    // GET
    @GetMapping
    fun getAllExpenses(authentication: Authentication): ResponseEntity<List<ExpenseResponseDTO>> {
        val userId = authentication.principal as Long
        val expenses = expenseService.getAllExpenses(userId)
        return ResponseEntity.ok(expenses.map(ExpenseResponseDTO::fromEntity))
    }

    @GetMapping("/{expenseId}")
    fun getExpense(@PathVariable @Min(value = 1, message = "Expense ID must be a positive number") expenseId: Long,
                   authentication: Authentication): ResponseEntity<ExpenseResponseDTO> {
        val userId = authentication.principal as Long
        val expense = expenseService.getExpense(expenseId, userId)
        return ResponseEntity.ok(ExpenseResponseDTO.fromEntity(expense))
    }



    // PATCH
    @PatchMapping("/{expenseId}")
    fun updateExpense(@PathVariable @Min(value = 1, message = "Expense ID must be a positive number") expenseId: Long,
                      @Valid @RequestBody dto: ExpenseUpdateDTO, authentication: Authentication): ResponseEntity<ExpenseResponseDTO> {
        val userId = authentication.principal as Long
        val updated = expenseService.updateExpense(expenseId, dto, userId)
        return ResponseEntity.ok(ExpenseResponseDTO.fromEntity(updated))
    }


    // DELETE
    @DeleteMapping("/{expenseId}")
    fun deleteExpense(@PathVariable @Min(value = 1, message = "Expense ID must be a positive number") expenseId: Long,
                      authentication: Authentication): ResponseEntity<Void> {
        val userId = authentication.principal as Long
        expenseService.deleteExpense(expenseId, userId)
        return ResponseEntity.ok().build()
    }


}