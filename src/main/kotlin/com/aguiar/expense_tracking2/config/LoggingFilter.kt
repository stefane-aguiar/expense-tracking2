package com.aguiar.expense_tracking2.config

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import org.slf4j.MDC
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class LoggingFilter : Filter {

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        try {
            MDC.put("requestId", UUID.randomUUID().toString().substring(0, 8))
            chain.doFilter(request, response)
        } finally {
            MDC.clear()
        }
    }

}