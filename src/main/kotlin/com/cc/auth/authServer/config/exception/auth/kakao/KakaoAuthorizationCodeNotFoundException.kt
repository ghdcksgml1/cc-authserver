package com.cc.auth.authServer.config.exception.auth.kakao

import com.cc.auth.authServer.config.exception.ApiException

class KakaoAuthorizationCodeNotFoundException
    : ApiException("AuthorizationCode를 찾을 수 없습니다.") {
}