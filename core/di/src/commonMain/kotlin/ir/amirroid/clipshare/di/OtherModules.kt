package ir.amirroid.clipshare.di

import kotlinx.serialization.json.Json
import org.koin.dsl.module

val otherModules = module {
    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
        }
    }
}