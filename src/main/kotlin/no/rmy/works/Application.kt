package no.rmy.works

import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import no.rmy.works.view.configureRouting


fun main(args: Array<String>) {
    Works.init()
    EngineMain.main(args)
}

fun Application.module() {
    configureRouting()

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }
}

