package com.cc.auth.authServer.auth.service

import com.cc.auth.authServer.auth.dto.AuthRegisterResponse
import com.cc.auth.authServer.auth.dto.kakao.*
import com.cc.auth.authServer.auth.dto.AuthRequest
import com.cc.auth.authServer.auth.dto.AuthResponse
import com.cc.auth.authServer.config.exception.auth.kakao.KakaoAuthorizationCodeNotFoundException
import com.cc.auth.authServer.config.exception.auth.kakao.KakaoTokenExpiredException
import com.cc.auth.authServer.config.exception.user.UserNotFoundException
import com.cc.auth.authServer.config.jwt.JwtService
import com.cc.auth.authServer.user.UserRepository
import com.cc.auth.authServer.user.model.Role
import com.cc.auth.authServer.user.model.User
import com.proseed.api.auth.dto.AuthRegisterRequest
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: BCryptPasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager,
    private val kakaoValueBuilder: KakaoValueBuilder,
    private val restTemplate: RestTemplate
) {

    fun kakaoLoginPage(): KakaoLoginPageResponse {
        return kakaoValueBuilder.kakaoLoginPageResponse()
    }

    // 토큰 발급 시스템
    fun kakaoTokenProvider(code: String): Any {
        // Header 설정
        val tokenRequestHeader = HttpHeaders()
        tokenRequestHeader.contentType = MediaType.APPLICATION_FORM_URLENCODED

        // kakaoTokenRequest 객체 생성
        val kakaoTokenRequest = kakaoValueBuilder.kakaoTokenRequest(code)

        val kakaoTokenResponse = restTemplate.postForObject(
            "https://kauth.kakao.com/oauth/token",
            HttpEntity(KakaoTokenRequest.of(kakaoTokenRequest), tokenRequestHeader),
            KakaoTokenResponse::class.java
        ) ?: throw KakaoAuthorizationCodeNotFoundException()

        // Header 설정
        val userInfoHeader = HttpHeaders()
        userInfoHeader.set("Authorization", "Bearer " + kakaoTokenResponse.access_token)

        val entity = HttpEntity("", userInfoHeader)

        val kakaoUserInfo = restTemplate.exchange(
            "https://kapi.kakao.com/v2/user/me",
            HttpMethod.GET,
            entity,
            KakaoUserInfoResponse::class.java
        ).body ?: throw KakaoTokenExpiredException()

        var email = kakaoUserInfo.kakao_account.email

        // User 테이블에서 platformId를 찾는다.
        // 만약 이미 존재한다면 user정보를 가져오고 jwt토큰 생성
        // user 정보가 존재하지 않는다면, user정보를 등록하고 jwt토큰 생성

        var user = userRepository.findByEmail(email)

        if (user == null) { // 회원가입
            user = userRepository.saveAndFlush(
                User(
                    platformId = passwordEncoder.encode(kakaoUserInfo.id),
                    platformType = "KAKAO",
                    nickName = "%c*****".format(email.split("@")[0][0]),
                    role = Role.USER,
                    email = email,
                    company = ""
                )
            )

            return AuthRegisterResponse(
                platformId = user.platformId,
                platformType = user.platformType
            )
        } else  { // 닉네임, 프로필 사진 업데이트
            user.nickName = "%c*****".format(email.split("@")[0][0])

            user = userRepository.save(user)
        }

        val tokenHashMap = HashMap<String, String>()
        tokenHashMap["id"] = user.id.toString()
        tokenHashMap["role"] = user.role.toString()
        val jwtToken = jwtService.generateToken(tokenHashMap, user)

        return AuthResponse(jwtToken)
    }

    fun register(requestDto: AuthRegisterRequest): AuthResponse {

        val user = userRepository.findByPlatformId(requestDto.platformId) ?: throw UserNotFoundException()
        user.company = requestDto.company
        val savedUser = userRepository.saveAndFlush(user)

        val tokenHashMap = HashMap<String, String>()
        tokenHashMap["id"] = user.id.toString()
        tokenHashMap["role"] = user.role.toString()
        val jwtToken = jwtService.generateToken(tokenHashMap, savedUser)

        return AuthResponse(jwtToken)
    }
}
