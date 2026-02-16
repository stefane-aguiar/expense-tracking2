package com.aguiar.expense_tracking2.service

import com.aguiar.expense_tracking2.exception.ResourceNotFoundException
import com.aguiar.expense_tracking2.dto.UserCreateDTO
import com.aguiar.expense_tracking2.dto.UserUpdateDTO
import com.aguiar.expense_tracking2.model.User
import com.aguiar.expense_tracking2.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class UserService (
    private val userRepository: UserRepository
){

    private val logger = LoggerFactory.getLogger(UserService::class.java)

    // Create
    fun createUser(dto: UserCreateDTO): User {
        logger.info("Creating user")

        val saved = userRepository.save(dto.toEntity())
        logger.info("User created: id=${saved.id}")
        return saved
    }

    // Get
    fun getAllUsers(): List<User> {
        logger.info("Fetching all users")
        return userRepository.findAll()
    }

    fun getUser(userId: Long): User {
        logger.info("Fetching user id=$userId")
        return userRepository.findById(userId)
            .orElseThrow{
                logger.warn("User not found with id=$userId")
                ResourceNotFoundException("User not found with id: $userId")
            }
    }

    // Update
    fun updateUser(userId: Long, dto: UserUpdateDTO): User {
        logger.info("Updating user id=$userId")

        // 1. Validates if user exists
        val existingUser = userRepository.findById(userId)
            .orElseThrow{
                logger.warn("User not found with id=$userId")
                ResourceNotFoundException("User not found with id: $userId")
            }

        // 2. Updates only the fields which came in the request (PATCH)
        dto.name?.takeIf { it.isNotBlank() }?.let { existingUser.name = it }
        dto.email?.takeIf { it.isNotBlank() }?.let { existingUser.email = it }

        // 3. Saves and returns
        val saved = userRepository.save(existingUser)
        logger.info("User udpated: id=${saved.id}")
        return saved
    }


    // Delete
    fun deleteUser(userId: Long) {
        logger.info("Deleting user id=$userId")

        if (!userRepository.existsById(userId)) {
            logger.warn("User not found with id=$userId")
            throw ResourceNotFoundException ("User not found with id: $userId")
        }
        userRepository.deleteById(userId)
        logger.info("User deleted: id=$userId")
    }


}