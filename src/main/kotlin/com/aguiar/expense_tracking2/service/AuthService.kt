package com.aguiar.expense_tracking2.service

import com.aguiar.expense_tracking2.dto.AuthResponseDTO
import com.aguiar.expense_tracking2.dto.LoginDTO
import com.aguiar.expense_tracking2.dto.UserCreateDTO
import com.aguiar.expense_tracking2.dto.UserResponseDTO
import com.aguiar.expense_tracking2.exception.ResourceNotFoundException
import com.aguiar.expense_tracking2.model.User
import com.aguiar.expense_tracking2.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService (
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService
){

    private val logger = LoggerFactory.getLogger(AuthService::class.java)


    fun register(dto: UserCreateDTO): UserResponseDTO {
        logger.info("Registering user with email=${dto.email}")

        // 1. Encrypts password
        val encryptedPassword = passwordEncoder.encode(dto.password)!!

        // 2. Creates user with encrypted password
        val user = User(
            name = dto.name,
            email = dto.email,
            password = encryptedPassword
        )

        val saved = userRepository.save(user)
        logger.info("User registered: id=${saved.id}")
        return UserResponseDTO.fromEntity(saved)
    }


    fun login(dto: LoginDTO): AuthResponseDTO {
        logger.info("Login attempt for email=${dto.email}")

        // 1. Search user by email
        val user = userRepository.findByEmail(dto.email)
            ?: throw ResourceNotFoundException("Invalid email or password")

        // 2. Checks password
        if (!passwordEncoder.matches(dto.password, user.password)) {
            logger.warn("Invalid password for email=${dto.email}")
            throw ResourceNotFoundException("Invalid email or password")
        }

        // 3. Generates token
        val token = jwtService.generateToken(user)
        logger.info("Login successful for userId=${user.id}")
        return AuthResponseDTO(token = token)

    }

}