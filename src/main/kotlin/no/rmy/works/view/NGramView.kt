package no.rmy.works.view

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.html.*
import kotlinx.html.*
import no.rmy.works.oss.schema.Work
import no.rmy.works.stats.NGramCount

fun Route.nGramRoutes() {
    route("/gram") {
        get {
            call.respondHtmlTemplate(LayoutTemplate()) {
                val heading = "Trigram"
                header { +heading }
                content {
                    articleTitle { +heading }
                    articleText {
                        ul {
                            Work.all.values.map {
                                li { a(ResourcePath.gram(it.id, null)) { +it.title } }
                            }
                        }
                    }
                }
            }
        }


        get("/{id}") {
            val id = call.parameters["id"]!!
            Work.all[id]?.let { work ->
                call.respondHtmlTemplate(LayoutTemplate()) {
                    val heading = "Trigram"
                    header { +heading }
                    content {
                        articleTitle { +heading }
                        articleText {
                            table {
                                tr {
                                    attributes["valign"] = "top"
                                    td {
                                        h2 { +work.title }
                                        ul {
                                            NGramCount().ofWork(work.id).map {
                                                li { +"${it.gram}: ${it.count}" }
                                            }
                                        }
                                    }
                                    td {

                                        h2 { +"Compare with one of:" }
                                        ul {
                                            Work.all.values.map {
                                                li { a(ResourcePath.gram(id, it.id)) { +it.title } }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }  ?: call.respondHtmlTemplate(NotFoundTemplate()) {
                work { id }
            }
        }


        get("/{id1}/{id2}") {
            val id1 = call.parameters["id1"]!!
            val id2 = call.parameters["id2"]!!

            Work.all[id1]?.let { work1 ->
                Work.all[id2]?.let { work2 ->
                    call.respondHtmlTemplate(LayoutTemplate()) {
                        val heading = "Trigram"
                        header { +heading }
                        content {
                            articleTitle { +heading }
                            articleText {
                                table {
                                    tr {
                                        td {
                                            h2 { +work1.title }
                                            ul {
                                                NGramCount().ofWork(work1.id).map {
                                                    li { +"${it.gram}: ${it.count}" }
                                                }
                                            }
                                        }
                                        td {
                                            h2 { +work2.title }
                                            ul {
                                                NGramCount().ofWork(work2.id).map {
                                                    li { +"${it.gram}: ${it.count}" }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}

