package com.cc.auth.authServer.config.exception.auth.kakao

import com.cc.auth.authServer.config.exception.ApiException

class KakaoTokenExpiredException
    : ApiException("카카오 토큰이 만료되었습니다.") {
}