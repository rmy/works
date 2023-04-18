package no.rmy.works.view

import io.ktor.server.routing.*

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.routing
import no.rmy.works.oss.schema.Role
import no.rmy.works.oss.schema.Work
import org.slf4j.LoggerFactory
import java.io.File

fun Application.configureRouting() {
    routing {
        route("works") {
            workRoutes()
            nGramRoutes()
            personRoutes()
            chapterRoutes()
            searchRoutes()

            get("characters") {
                call.respond(Role.all)
            }
            get("characters/{id}") {
                val id = call.parameters["id"]
                Work.all[id]?.let { work ->
                    val ch = Role.byWork[work.id]?.map { it.name }
                    call.respond(mapOf(work.id to ch))
                }
                call.respond(
                    HttpStatusCode.NotFound,
                    "There is no character with id $id\nTry one of: ${Role.all.keys.joinToString(", ")}"
                )
            }
            get("chapters") {
                val r = Work.all.values.associate { w -> w.title to w.chapters.map { ch -> ch.toString() } }
                call.respond(r)
            }
            get("chapters/{workId}") {
                val workId = call.parameters["workId"]
                Work.all[workId]?.let { work ->
                    val chapters = work.chapters.map { it.toString() }
                    call.respond(mapOf(work.title to chapters))
                }
                call.respond(
                    HttpStatusCode.NotFound,
                    "There is no work with id $workId\nTry one of: ${Work.all.keys.joinToString(", ")}"
                )
            }
        }
        route("/") {
            get {
                call.respondRedirect("/works/hamlet")
            }
        }
        static(ResourcePath.root) {
            listOf("src/main/dist", ".")
                .firstOrNull { File("$it/css").exists() }
                ?.let { cssPath ->
                    staticRootFolder = File(cssPath)
                }
            static("css") {
                files("css")
            }
        }
    }
}

val logger = LoggerFactory.getLogger("Route")