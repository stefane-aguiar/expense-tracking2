package com.aguiar.expense_tracking2.service

import com.aguiar.expense_tracking2.exception.ResourceNotFoundException
import com.aguiar.expense_tracking2.dto.UserCreateDTO
import com.aguiar.expense_tracking2.dto.UserUpdateDTO
import com.aguiar.expense_tracking2.model.User
import com.aguiar.expense_tracking2.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService (
    private val userRepository: UserRepository
){
    // Create
    fun createUser(dto: UserCreateDTO): User {
        return userRepository.save(dto.toEntity())
    }

    // Get
    fun getAllUsers(): List<User> {
        return userRepository.findAll()
    }

    fun getUser(userId: Long): User {
        return userRepository.findById(userId)
            .orElseThrow{ ResourceNotFoundException("User not found with id: $userId") }
    }

    // Update
    fun updateUser(userId: Long, dto: UserUpdateDTO): User {

        // 1. Validates if user exists
        val existingUser = userRepository.findById(userId)
            .orElseThrow{ ResourceNotFoundException("User not found with id: $userId") }

        // 2. Updates only the fields which came in the request (PATCH)
        dto.name?.takeIf { it.isNotBlank() }?.let { existingUser.name = it }
        dto.email?.takeIf { it.isNotBlank() }?.let { existingUser.email = it }

        // 3. Saves and returns
        return userRepository.save(existingUser)
    }


    // Delete
    fun deleteUser(userId: Long) {
        if (!userRepository.existsById(userId)) {
            throw ResourceNotFoundException ("User not found with id: $userId")
        }
        userRepository.deleteById(userId)
    }


}