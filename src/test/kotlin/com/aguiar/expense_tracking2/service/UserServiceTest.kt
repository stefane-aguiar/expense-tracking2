package com.aguiar.expense_tracking2.service

import com.aguiar.expense_tracking2.dto.UserCreateDTO
import com.aguiar.expense_tracking2.dto.UserUpdateDTO
import com.aguiar.expense_tracking2.exception.ResourceNotFoundException
import com.aguiar.expense_tracking2.model.User
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
import java.util.Optional


@ExtendWith(MockitoExtension::class)
class UserServiceTest {

    // 1. Create mocks (fakes) of the dependencies
    @Mock
    private lateinit var userRepository: UserRepository

    // 2. Inject mocks on the service
    @InjectMocks
    private lateinit var userService: UserService

    // 3. Reusable Test objects
    private lateinit var testUser: User

    // 4. Runs BEFORE each test
    @BeforeEach
    fun setup() {
        testUser = User(
            id = 1L,
            name = "Joao",
            email = "joao@email.com"
        )
    }



    // 5. Real tests - With @Test



    // ============ CREATE TESTS ============

    @Test
    @DisplayName("Should create user when data is valid")
    fun shouldCreateUser() {
        // 1. Arrange
        val dto = UserCreateDTO(name = "Steh", email = "steh@email.com")

            // MOCK: Programs userRepository
        `when`(userRepository.save(any(User::class.java))).thenAnswer { invocation ->
            val user = invocation.getArgument<User>(0)
            User(
                id = 1L,
                name = user.name,
                email = user.email
            )
        }

        // 2. Act
        val result = userService.createUser(dto)

        // 3. Assert
        assertNotNull(result)
        assertEquals(1L, result.id)
        assertEquals("Steh", result.name)
        assertEquals("steh@email.com", result.email)
        verify(userRepository, times(1)).save(any(User::class.java))
    }




    // ============ READ TESTS ============

    @Test
    @DisplayName("Should return all users")
    fun shouldReturnAllUsers() {
        // 1. Arrange
        val user2 = User(
            name = "Steh",
            email = "steh@email.com"
        )

        val users = listOf(testUser, user2)
        `when`(userRepository.findAll()).thenReturn(users)

        // 2. Act
        val result = userService.getAllUsers()

        // 3. Assert
        assertEquals(2, result.size)
        assertEquals("Joao", result[0].name)
        assertEquals("Steh", result[1].name)
        assertEquals("joao@email.com", result[0].email)
        assertEquals("steh@email.com", result[1].email)
        verify(userRepository, times(1)).findAll()
    }


    @Test
    @DisplayName("Should return empty list when no users")
    fun shouldReturnEmptyListWhenNoUsers() {
        // 1. Arrange
        `when`(userRepository.findAll()).thenReturn(listOf())

        // 2. Act
        val result = userService.getAllUsers()

        // 3. Assert
        assertNotNull(result)
        assertTrue(result.isEmpty())
    }




    @Test
    @DisplayName("Should return user by id")
    fun shouldReturnUserById() {
        // 1. Arrange
        `when`(userRepository.findById(1L)).thenReturn(Optional.of(testUser))

        // 2. Act
        val result = userService.getUser(1L)

        // 3. Assert
        assertNotNull(result)
        assertEquals(1L, result.id)
        assertEquals("Joao", result.name)
        assertEquals("joao@email.com", result.email)
    }



    @Test
    @DisplayName("Should throw exception when user not found")
    fun shouldThrowExceptionWhenUserNotFound() {
        // 1. Arrange
        `when`(userRepository.findById(999L)).thenReturn(Optional.empty())

        // 2. Act & Assert
        val exception = assertThrows(ResourceNotFoundException::class.java) {
            userService.getUser(999L)
        }
        assertEquals("User not found with id: 999", exception.message)
    }





    // ============ UPDATE TESTS ============

    @Test
    @DisplayName("Should update user")
    fun shouldUpdateUser() {
        // 1. Arrange
        val updateDTO = UserUpdateDTO(name = "Steh", email = "steh@email.com")

        `when`(userRepository.findById(1L)).thenReturn(Optional.of(testUser))
        `when`(userRepository.save(any(User::class.java))).thenReturn(testUser)

        // 2. Act
        val result = userService.updateUser(1L, updateDTO)

        // 3. Assert
        assertEquals("Steh", result.name)
        assertEquals("steh@email.com", result.email)
        verify(userRepository, times(1)).save(any(User::class.java))
    }


    @Test
    @DisplayName("Should update only name of user")
    fun shouldUpdateOnlyName() {
        // 1. Arrange
        val updateDTO = UserUpdateDTO(name = "Steh")

        `when`(userRepository.findById(1L)).thenReturn(Optional.of(testUser))
        `when`(userRepository.save(any(User::class.java))).thenReturn(testUser)

        // 2. Act
        val result = userService.updateUser(1L, updateDTO)

        // 3. Assert
        assertEquals("Steh", result.name)
        assertEquals("joao@email.com", result.email)
        verify(userRepository, times(1)).save(any(User::class.java))
    }



    @Test
    @DisplayName("Should update only email of user")
    fun shouldUpdateOnlyEmail() {
        // 1. Arrange
        val updateDTO = UserUpdateDTO(email = "steh@email.com")

        `when`(userRepository.findById(1L)).thenReturn(Optional.of(testUser))
        `when`(userRepository.save(any(User::class.java))).thenReturn(testUser)

        // 2. Act
        val result = userService.updateUser(1L, updateDTO)

        // 3. Assert
        assertEquals("Joao", result.name)
        assertEquals("steh@email.com", result.email)
        verify(userRepository, times(1)).save(any(User::class.java))
    }



    @Test
    @DisplayName("Should throw exception when updating non existent user")
    fun shouldThrowExceptionWhenUpdatingNonExistentUser() {
        // 1. Arrange
        val updateDTO = UserUpdateDTO(name = "Steh")

        `when`(userRepository.findById(999L)).thenReturn(Optional.empty())

        // 2. Act & Assert
        val exception = assertThrows(ResourceNotFoundException::class.java) {
            userService.updateUser(999L, updateDTO)
        }
        assertEquals("User not found with id: 999", exception.message)
    }







    // ============ DELETE TESTS ============

    @Test
    @DisplayName("Should delete user")
    fun shouldDeleteUser() {
        // 1. Arrange
        `when`(userRepository.existsById(1L)).thenReturn(true)

        // 2. Act
        userService.deleteUser(1L)

        // 3. Assert
        verify(userRepository, times(1)).deleteById(1L)
    }


    @Test
    @DisplayName("Should throw exception when deleting non existent user")
    fun shouldThrowExceptionWhenDeletingNonExistentUser() {
        // 1. Arrange
        `when`(userRepository.existsById(999L)).thenReturn(false)

        // 2. Act
        val exception = assertThrows(ResourceNotFoundException::class.java) {
            userService.deleteUser(999L)
        }
        assertEquals("User not found with id: 999", exception.message)
        verify(userRepository, never()).deleteById(anyLong())
    }





}