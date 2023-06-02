package com.cc.auth.authServer.user

import com.cc.auth.authServer.user.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {

    fun findByEmail(email: String): User?
    fun findByPlatformId(platformId: String): User?
}