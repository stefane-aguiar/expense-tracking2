package com.aguiar.expense_tracking2.dto

import com.aguiar.expense_tracking2.model.User

class UserSummaryDTO(
    val id: Long,
    val name: String

) {
    companion object {
        fun fromEntity(user: User): UserSummaryDTO {
            return UserSummaryDTO(id = user.id!!, name = user.name)
        }
    }
}