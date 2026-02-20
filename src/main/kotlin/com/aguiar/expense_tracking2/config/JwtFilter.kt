package com.aguiar.expense_tracking2.config

import com.aguiar.expense_tracking2.service.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtFilter(
    private val jwtService: JwtService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        // 1. Take header authorization
        val authHeader = request.getHeader("Authorization")

        // 2. Checks if it has token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // 3. Extracts token (remove "Bearer ")
            val token = authHeader.substring(7)

            // 4. Validates token
            if (jwtService.validateToken(token)) {
                // 5. Extracts userId and puts it in the Spring Security Context
                val userId = jwtService.getUserIdFromToken(token)
                val authentication = UsernamePasswordAuthenticationToken(userId, null, emptyList())
                SecurityContextHolder.getContext().authentication = authentication
            }

        }
        // 6. Passes to the next filter
        filterChain.doFilter(request, response)

    }

}