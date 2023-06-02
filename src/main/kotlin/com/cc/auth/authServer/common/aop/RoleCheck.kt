package com.cc.auth.authServer.common.aop

import com.cc.auth.authServer.user.model.Role
import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
annotation class RoleCheck(
    val role: Role = Role.USER
) {
}
