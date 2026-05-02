package com.aguiar.expense_tracking2.service

import com.aguiar.expense_tracking2.model.User
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class JwtServiceTest {

    private val testSecret = "test-secret-key-exactly-32-chars"
    private val jwtService = JwtService(testSecret)

    @Test
    fun shouldSetUserIdAsTokenSubject() {
        val user = User(id = 42L, name = "Ana", email = "ana@test.com", password = "pass")

        val token = jwtService.generateToken(user)

        val claims = Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(testSecret.toByteArray()))
            .build()
            .parseSignedClaims(token)
            .payload

        assertEquals(user.id.toString(), claims.subject)
    }
}
