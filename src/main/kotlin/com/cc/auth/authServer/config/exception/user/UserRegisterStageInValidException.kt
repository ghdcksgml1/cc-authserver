package com.cc.auth.authServer.config.exception.user

import com.cc.auth.authServer.config.exception.ApiException

class UserRegisterStageInValidException : ApiException {
    constructor() : super("User의 RegisterStage가 정확하지 않습니다.")
}