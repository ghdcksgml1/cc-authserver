package com.cc.auth.authServer.config.exception

import com.cc.auth.authServer.utils.logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestController

@ControllerAdvice
@RestController
class GlobalExceptionHandler {
    private val logger = logger<GlobalExceptionHandler>()

    @ExceptionHandler(value = [ApiException::class])
    fun apiException(exception: ApiException): ResponseEntity<ExceptionDto> {
        logger.info("ApiException Running..")

        return ResponseEntity(ExceptionDto(message = exception.message ?: "에러발생"), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(value = [HttpMessageNotReadableException::class])
    fun bindingException(exception: HttpMessageNotReadableException): ResponseEntity<ExceptionDto> {
        logger.info("HttpMessageNotReadableException Running..")

        return ResponseEntity(ExceptionDto(message = exception.message ?: "에러발생"), HttpStatus.BAD_REQUEST)
    }
}