package com.cc.auth.authServer.common.aop

import com.cc.auth.authServer.config.exception.user.UserForbiddenException
import com.cc.auth.authServer.config.exception.user.UserNotFoundException
import com.cc.auth.authServer.user.model.User
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Aspect
@Component
class RoleCheckAOP {

    @Before("@annotation(com.cc.auth.authServer.common.aop.RoleCheck)")
    fun checkRole(joinPoint: JoinPoint) {
        val methodSignature = joinPoint.signature as MethodSignature
        val annotation = methodSignature.method.getAnnotation(RoleCheck::class.java)
        val role = annotation.role

        val authentication = SecurityContextHolder.getContext()?.authentication ?: throw UserNotFoundException()
        val principal = authentication.principal as User

        if (principal.role != role) {
            throw UserForbiddenException()
        }
    }
}