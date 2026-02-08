package com.example.whyamihere

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform