package com.niclauscott.jetdrive.core.di

import org.koin.dsl.module
import java.util.Random

val appModule = module {

    factory<String> {
        //"http://localhost:8080"
        //"http://192.168.107.127:8080"

        val baseUrl = if ((1..10).random() % 2 == 0) "http://192.168.107.127:8080"
        else "http://127.0.0.1:8080"
        //baseUrl

        "http://192.168.86.127:9001"
        //"http://localhost:8080"
    }

}