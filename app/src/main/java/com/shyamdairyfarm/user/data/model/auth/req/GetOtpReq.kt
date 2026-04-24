package com.shyamdairyfarm.user.data.model.auth.req

import com.shyamdairyfarm.user.data.utils.getDeviceInfo

data class GetOtpReq(
    val device_model: String ,
    val device_type: String = "android",
    val mobile_no: String
)