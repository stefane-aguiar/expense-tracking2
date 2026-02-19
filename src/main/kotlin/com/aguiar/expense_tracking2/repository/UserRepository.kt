package com.aguiar.expense_tracking2.repository

import com.aguiar.expense_tracking2.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User ?
}