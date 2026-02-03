package com.aguiar.expense_tracking2.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime

@RestControllerAdvice
class GlobalExceptionHandler {

    // 1. Validation errors (@valid)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationErrors(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, Any>> {
        val response = mutableMapOf<String, Any>()
        response["timestamp"] = LocalDateTime.now()
        response["status"] = HttpStatus.BAD_REQUEST.value()

        // Extracts validation errors
        val errors = mutableMapOf<String, String>()
        ex.bindingResult.fieldErrors.forEach { error ->
            errors[error.field]= error.defaultMessage ?: "Invalid value"}

        response["errors"] = errors
        return ResponseEntity.badRequest().body(response)
    }

    // 2. Errors of JSON conversion (wrong type, wrong format)
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleJsonParseErrors(ex: HttpMessageNotReadableException): ResponseEntity<Map<String, Any>> {
        val response = mutableMapOf<String, Any>()
        response["timestamp"] = LocalDateTime.now()
        response["status"] = HttpStatus.BAD_REQUEST.value()

        // Extracts more friendly message
        val message = ex.message ?: ""
        if (message.contains("LocalDate")) {
            response["message"] = "Invalid date format. Use: YYYY-MM-DD (e.g., 2024-12-21)"
        } else if (message.contains("BigDecimal")) {
            response["message"] = "Invalid number format"
        } else {
            response["message"] = "Invalid request format"
        }

        response["details"] = ex.mostSpecificCause.message ?: "No details available"
        return ResponseEntity.badRequest().body(response)
    }

    // 3. Resource not found
    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFound(ex: ResourceNotFoundException): ResponseEntity<Map<String, Any>> {
        val response = mutableMapOf<String, Any>()
        response["timestamp"] = LocalDateTime.now()
        response["status"] = HttpStatus.NOT_FOUND.value()
        response["message"] = ex.message ?: "Resource not found"

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response)
    }


    // 4. Any other exception
    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<Map<String, Any>> {
        val response = mutableMapOf<String, Any>()
        response["timestamp"] = LocalDateTime.now()
        response["status"] = HttpStatus.INTERNAL_SERVER_ERROR.value()
        response["message"] = "An unexpected error ocurred"

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
    }

}