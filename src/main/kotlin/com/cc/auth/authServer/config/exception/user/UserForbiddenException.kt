package com.cc.auth.authServer.config.exception.user

import com.cc.auth.authServer.config.exception.ApiException

class UserForbiddenException : ApiException("접근 권한이 없습니다.") {
}