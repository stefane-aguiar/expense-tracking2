package com.aguiar.expense_tracking2.controller

import com.aguiar.expense_tracking2.dto.AuthResponseDTO
import com.aguiar.expense_tracking2.dto.LoginDTO
import com.aguiar.expense_tracking2.dto.UserCreateDTO
import com.aguiar.expense_tracking2.dto.UserResponseDTO
import com.aguiar.expense_tracking2.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/register")
    fun register(@Valid @RequestBody dto: UserCreateDTO): ResponseEntity<UserResponseDTO> {
        val user = authService.register(dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(user)
    }


    @PostMapping("/login")
    fun login(@Valid @RequestBody dto: LoginDTO): ResponseEntity<AuthResponseDTO> {
        val response = authService.login(dto)
        return ResponseEntity.ok(response)
    }


}