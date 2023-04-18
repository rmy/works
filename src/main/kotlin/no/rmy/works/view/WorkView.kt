package no.rmy.works.view

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.html.*
import io.ktor.http.*
import kotlinx.html.*
import no.rmy.works.oss.schema.Work


fun Route.workRoutes() {
    route("api") {
        get {
            call.respond(Work.all)
        }
    }

    get {
        val heading = "Works"
        call.respondHtml(HttpStatusCode.OK) {
            head { title { +heading } }
            body {
                h1 { +heading }
                ul {
                    Work.all.values.map {
                        li { a(ResourcePath.work(it.id)) { +it.title } }
                    }
                }

                form(action = ResourcePath.query()) {
                    p {
                        +"Query: "
                        textInput(name = "q")
                        submitInput() {
                            value = "Search"
                        }
                    }
                }
            }
        }
    }

    get("/{id}") {
        val workId = call.parameters["id"] as String
        Work.all[workId]?.let { work ->
            val heading = work.title
            call.respondHtmlTemplate(LayoutTemplate()) {
                header { +heading }
                content {
                    articleTitle { +work.title }
                    articleText {
                        h2 { +"Persona" }
                        ul {
                            work.roles.map {
                                li {
                                    a(href = ResourcePath.person(workId, it.id)) {
                                        +it.name
                                    }
                                    +" - ${it.description}".trimEnd('-', ' ')
                                }
                            }
                        }

                        h2 { +"Parts" }
                        ul {
                            work.chapters.map {
                                li {
                                    a(href = ResourcePath.chapter(it.workId, it.id)) {
                                        +"${it.section}.${it.chapter} ${it.description}"
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } ?: call.respondHtmlTemplate(NotFoundTemplate()) {
            work { +workId }
        }
    }

}

