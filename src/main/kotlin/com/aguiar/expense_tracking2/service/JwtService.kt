package com.aguiar.expense_tracking2.service

import com.aguiar.expense_tracking2.model.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Date

@Service
class JwtService (
    @Value("\${jwt.secret}")private val secretKey: String
) {

    private val expiration = 86400000L // 24 hours in milliseconds


    private fun getSigningKey() = Keys.hmacShaKeyFor(secretKey.toByteArray())


    fun generateToken(user: User): String {
        val now = Date()
        val expiryDate = Date(now.time + expiration)

        return Jwts.builder()
            .subject(user.id.toString())
            .claim("email", user.email)
            .claim("name", user.name)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(getSigningKey())
            .compact()
    }



    fun validateToken(token: String): Boolean {
        return try {
            getClaims(token)
            true
        } catch(e: Exception) {
            false
        }
    }



    fun getUserIdFromToken(token: String): Long {
        val claims = getClaims(token)
        return claims.subject.toLong()
    }



    private fun getClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .payload
    }

}