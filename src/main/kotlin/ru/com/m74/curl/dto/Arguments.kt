package ru.com.m74.curl.dto

import org.springframework.http.HttpMethod
import org.springframework.util.LinkedMultiValueMap

data class Keystore(
    var type: String,
    var path: String,
    var password: String,
    var keyPassword: String,
)

data class Arguments(
    var url: String?,
    var ssl: Boolean,
    var method: HttpMethod,
    var headers: LinkedMultiValueMap<String, String>,
    var insecure: Boolean,
    var keystore: Keystore?,
    var body: String?,
    var verbose: Boolean,
    var help: Boolean,
)
