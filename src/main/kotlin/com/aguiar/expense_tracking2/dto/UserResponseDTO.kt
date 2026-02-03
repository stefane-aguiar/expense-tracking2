package com.aguiar.expense_tracking2.dto

import com.aguiar.expense_tracking2.model.User

class UserResponseDTO(
    val id: Long,
    val name: String,
    val email: String
) {
    companion object {
        fun fromEntity(user: User): UserResponseDTO {
            return UserResponseDTO(id = user.id!!, name = user.name, email = user.email)
        }
    }
}