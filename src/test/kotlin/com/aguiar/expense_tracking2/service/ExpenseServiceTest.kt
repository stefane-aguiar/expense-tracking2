package com.aguiar.expense_tracking2.service

import com.aguiar.expense_tracking2.dto.ExpenseCreateDTO
import com.aguiar.expense_tracking2.dto.ExpenseUpdateDTO
import com.aguiar.expense_tracking2.exception.ResourceNotFoundException
import com.aguiar.expense_tracking2.model.Expense
import com.aguiar.expense_tracking2.model.User
import com.aguiar.expense_tracking2.repository.ExpenseRepository
import com.aguiar.expense_tracking2.repository.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate
import java.util.Optional


@ExtendWith(MockitoExtension::class)
class ExpenseServiceTest {

    // 1. Create mocks (fakes) of the dependencies
    @Mock
    private lateinit var expenseRepository: ExpenseRepository
    @Mock
    private lateinit var userRepository: UserRepository

    // 2. Inject mocks on the service
    @InjectMocks
    private lateinit var expenseService: ExpenseService

    // 3. Reusable Test objects
    private lateinit var testUser: User
    private lateinit var testExpense: Expense

    // 4. Runs BEFORE each test
    @BeforeEach
    fun setup() {
        // Prepare test data
        testUser = User(
            id = 1L,
            name = "Joao",
            email = "joao@email.com",
            password = "123456"
        )
        // Expense that "already exists in the database" (for tests READ/UPDATE/DELETE, but not CREATE)
        testExpense = Expense(
            id = 1L,
            category = "Comida",
            subCategory = "Mercado",
            description = "description test",
            amount = 13.50.toBigDecimal(),
            date = LocalDate.now(),
            user = testUser
        )
    }



    // 5. Real tests - With @Test



    // ============ CREATE TESTS ============
    @Test
    @DisplayName("Should create expense when data is valid")
    fun shouldCreateExpense() {
        // 1. Arrange
        val dto = ExpenseCreateDTO(
            category = "Saúde",
            subCategory = "Remédio",
            description = "gripe Ana",
            amount = 40.18.toBigDecimal(),
            date = LocalDate.now()
        )
        val userId = 1L

            // MOCK 1: Programs userRepository
        `when`(userRepository.findById(userId)).thenReturn(Optional.of(testUser))
            // MOCK 2: Programs expenseRepository
        `when`(expenseRepository.save(any(Expense::class.java))).thenAnswer { invocation ->
            val exp = invocation.getArgument<Expense>(0)
            Expense(
                id = 1L,
                category = exp.category,
                subCategory = exp.subCategory,
                description = exp.description,
                amount = exp.amount,
                date = exp.date,
                user = exp.user
            )
        }

        // 2. Act
        val result = expenseService.createExpense(dto, userId)

        // 3. Assert
        assertNotNull(result)
        assertEquals(1L, result.id)
        assertEquals("Saúde", result.category)
        assertEquals("Remédio", result.subCategory)
        assertEquals("gripe Ana", result.description)
        assertEquals(40.18.toBigDecimal(), result.amount)
        verify(userRepository, times(1)).findById(userId)
        verify(expenseRepository, times(1)).save(any(Expense::class.java))
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    fun shouldThrowExceptionWhenUserNotFound() {
        // 1. Arrange
        val dto = ExpenseCreateDTO(
            category = "Comida",
            subCategory = "Comer fora",
            description = "cafe da manha padaria",
            amount = 50.00.toBigDecimal(),
            date = LocalDate.now(),
        )
        val userId = 999L

        `when`(userRepository.findById(userId)).thenReturn(Optional.empty())

        // 2. Act & Assert
        assertThrows(ResourceNotFoundException::class.java) {
            expenseService.createExpense(dto, userId)
        }
        verify(expenseRepository, never()).save(any(Expense::class.java))
    }





    // ============ READ TESTS ============

    @Test
    @DisplayName("Should return all expenses")
    fun shouldReturnAllExpensesForUser() {
        // 1. Arrange
        val userId = 1L
        val expense2 = Expense(
            category = "Contas da casa",
            subCategory = "Condominio",
            description = "bla",
            amount = 1153.33.toBigDecimal(),
            date = LocalDate.now(),
            user = testUser
        )

        val expenses = listOf(testExpense, expense2)
        `when`(expenseRepository.findByUserId(userId)).thenReturn(expenses)

        // 2. Act
        val result = expenseService.getAllExpenses(userId)

        // 3. Assert
        assertEquals(2, result.size)
        assertEquals("Comida", result[0].category)
        assertEquals("Contas da casa", result[1].category)
        assertEquals("Mercado", result[0].subCategory)
        assertEquals("Condominio", result[1].subCategory)
        assertEquals(13.50.toBigDecimal(), result[0].amount)
        assertEquals(1153.33.toBigDecimal(), result[1].amount)
        verify(expenseRepository, times(1)).findByUserId(userId)
    }


    @Test
    @DisplayName("Should return empty list when no expenses")
    fun shouldReturnEmptyListWhenNoExpenses() {
        // 1. Arrange
        val userId = 1L
        `when`(expenseRepository.findByUserId(userId)).thenReturn(listOf())

        // 2. Act
        val result = expenseService.getAllExpenses(userId)

        // 3. Assert
        assertNotNull(result)
        assertTrue(result.isEmpty())
    }


    @Test
    @DisplayName("Should return expense by Id")
    fun shouldReturnExpenseById() {
        // 1. Arrange
        val userId = 1L
        `when`(expenseRepository.findById(1L)).thenReturn(Optional.of(testExpense))

        // 2. Act
        val result = expenseService.getExpense(1L, userId)

        // 3. Assert
        assertNotNull(result)
        assertEquals(1L, result.id)
        assertEquals("Comida", result.category)
    }


    @Test
    @DisplayName("Should throw exception when expense not found")
    fun shouldThrowExceptionWhenExpenseNotFound() {
        // 1. Arrange
        val userId = 1L
        `when`(expenseRepository.findById(999L)).thenReturn(Optional.empty())

        // 2. Act & Assert
        val exception = assertThrows(ResourceNotFoundException::class.java) {
            expenseService.getExpense(999L, userId)
        }
        assertEquals("Expense not found with id: 999", exception.message)
    }



    @Test
    @DisplayName("Should throw exception when user is not owner of expense")
    fun shouldThrowExceptionWhenUserIsNotOwner() {
        // 1. Arrange
        val otherUserId = 999L
        `when`(expenseRepository.findById(1L)).thenReturn(Optional.of(testExpense))

        // 2. Act & Assert
        val exception = assertThrows(ResourceNotFoundException::class.java) {
            expenseService.getExpense(1L, otherUserId)
        }
        assertEquals("Expense not found with id: 1", exception.message)
    }







    // ============ UPDATE TESTS ============

    @Test
    @DisplayName("Should update expense")
    fun shouldUpdateExpense() {
        // 1. Arrange
        val userId = 1L
        val updateData = ExpenseUpdateDTO(
            category = "Carro",
            amount = 195.91.toBigDecimal()
        )

        `when`(expenseRepository.findById(1L)).thenReturn(Optional.of(testExpense))
        `when`(expenseRepository.save(any(Expense::class.java))).thenReturn(testExpense)

        // 2. Act
        val result = expenseService.updateExpense(1L, updateData, userId)

        // 3. Assert
        assertEquals("Carro", result.category)
        assertEquals(195.91.toBigDecimal(), result.amount)
        verify(expenseRepository, times(1)).save(any(Expense::class.java))
    }


    @Test
    @DisplayName("Should update only category (PATCH partial)")
    fun shouldUpdateOnlyCategory() {
        // 1. Arrange
        val userId = 1L
        val updateCategory = ExpenseUpdateDTO(category = "Contas da casa")

        `when`(expenseRepository.findById(1L)).thenReturn(Optional.of(testExpense))
        `when`(expenseRepository.save(any(Expense::class.java))).thenReturn(testExpense)

        // 2. Act
        val result = expenseService.updateExpense(1L, updateCategory, userId)

        // 3. Assert
        assertEquals("Contas da casa", result.category)
        assertEquals("Mercado", result.subCategory)
    }


    @Test
    @DisplayName("Should update only amount (PATCH partial)")
    fun shouldUpdateOnlyAmount() {
        // 1. Arrange
        val userId = 1L
        val updateAmount = ExpenseUpdateDTO(amount = 120.35.toBigDecimal())

        `when`(expenseRepository.findById(1L)).thenReturn(Optional.of(testExpense))
        `when`(expenseRepository.save(any(Expense::class.java))).thenReturn(testExpense)

        // 2. Act
        val result = expenseService.updateExpense(1L, updateAmount, userId)

        // 3. Assert
        assertEquals(120.35.toBigDecimal(), result.amount)
        assertEquals("Comida", result.category)
    }


    @Test
    @DisplayName("Should throw exception when updating non existent expense")
    fun shouldThrowExceptionWhenUpdatingNonExistentExpense() {
        // 1. Arrange
        val userId = 1L
        val updateData = ExpenseUpdateDTO()
        `when`(expenseRepository.findById(999L)).thenReturn(Optional.empty())

        // 2. Act & Assert
        assertThrows(ResourceNotFoundException::class.java) {
            expenseService.updateExpense(999L, updateData, userId)
        }

    }


    @Test
    @DisplayName("Should throw exception when updating expense of another user")
    fun shouldThrowExceptionWhenUpdatingExpenseOfAnotherUser() {
        // 1. Arrange
        val otherUserId = 999L
        val updateData = ExpenseUpdateDTO(category = "Hacked")
        `when`(expenseRepository.findById(1L)).thenReturn(Optional.of(testExpense))

        // 2. Act & Assert
        assertThrows(ResourceNotFoundException::class.java) {
            expenseService.updateExpense(1L, updateData, otherUserId)
        }
        verify(expenseRepository, never()).save(any(Expense::class.java))
    }




    // ============ DELETE TESTS ============

    @Test
    @DisplayName("Should delete expense")
    fun shouldDeleteExpense() {
        // 1. Arrange
        val userId = 1L
        `when`(expenseRepository.findById(1L)).thenReturn(Optional.of(testExpense))

        // 2. Act
        expenseService.deleteExpense(1L, userId)

        // 3. Assert
        verify(expenseRepository, times(1)).delete(testExpense)

    }


    @Test
    @DisplayName("Should throw exception when deleting non existent expense")
    fun shouldThrowExceptionWhenDeletingNonExistentExpense() {
        // 1. Arrange
        val userId = 1L
        `when`(expenseRepository.findById(999L)).thenReturn(Optional.empty())

        // 2. Act & Assert
        val exception = assertThrows(ResourceNotFoundException::class.java) {
            expenseService.deleteExpense(999L, userId)
        }
        assertEquals("Expense not found with id: 999", exception.message)
        verify(expenseRepository, never()).delete(any(Expense::class.java))
    }



    @Test
    @DisplayName("Should throw exception when deleting expense of another user")
    fun shouldThrowExceptionWhenDeletingExpenseOfAnotherUser() {
        // 1. Arrange
        val otherUserId = 999L
        `when`(expenseRepository.findById(1L)).thenReturn(Optional.of(testExpense))

        // 2. Act & Assert
        assertThrows(ResourceNotFoundException::class.java) {
            expenseService.deleteExpense(1L, otherUserId)
        }
        verify(expenseRepository, never()).delete(any(Expense::class.java))
    }



}