package no.rmy.works.view

import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.routing.*
import kotlinx.html.*
import no.rmy.works.Works
import no.rmy.works.lucene.WorkIndex
import no.rmy.works.oss.schema.Work
import no.rmy.works.oss.schema.Role

fun Route.searchRoutes() {
    route("q") {
        get() {
            val q = call.parameters["q"]
            call.respondHtmlTemplate(LayoutTemplate()) {
                header { +"Search" }
                content {
                    articleTitle { +(q ?: "") }
                    articleText {
                        form(action = ResourcePath.query()) {
                            p {
                                +"Query: "
                                textInput(name = "q")
                                submitInput { value = "Search" }
                            }

                            q?.let { q ->
                                val docs = Works.workIndex.search(q)
                                val ms = Works.workIndex.managedSearcher(WorkIndex.Index.paragraphs)
                                p {
                                    "Total hits: "
                                    +docs.totalHits.toString()
                                }
                                ul {
                                    docs.scoreDocs.forEach {
                                        li {
                                            val d = ms.searcher.doc(it.doc)
                                            p {
                                                +(Role.all[d["charId"]]?.name ?: "")
                                                +" in "
                                                +(Work.all[d["workId"]]?.title ?: "")
                                                +" - "
                                                +d["section"]
                                                +"."
                                                +d["chapter"]
                                                +"."
                                                +d["line"]

                                                d["plainText"].split("[p]").forEach {
                                                    br {
                                                        +it
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                ms.release()
                            }
                        }
                    }
                }
            }
        }

        get("/{q}") {
            val q = call.parameters["q"]
            call.respondHtmlTemplate(LayoutTemplate()) {
                header { +"Search" }
                content {
                    articleTitle { +(q ?: "") }
                    articleText {
                        form(action = ResourcePath.query()) {
                            p {
                                +"Query: "
                                textInput(name = "q")
                                submitInput { value = "Search" }
                            }

                            q?.let { q ->
                                val docs = Works.workIndex.search(q)
                                val ms = Works.workIndex.managedSearcher(WorkIndex.Index.paragraphs)
                                p {
                                    "Total hits: "
                                    +docs.totalHits.toString()
                                }
                                ul {
                                    docs.scoreDocs.forEach {
                                        li {
                                            val d = ms.searcher.doc(it.doc)
                                            p {
                                                +(Role.all[d["charId"]]?.name ?: "")
                                                +" in "
                                                +(Work.all[d["workId"]]?.title ?: "")
                                                +" - "
                                                +d["section"]
                                                +"."
                                                +d["chapter"]
                                                +"."
                                                +d["line"]

                                                d["plainText"].split("[p]").forEach {
                                                    br {
                                                        +it
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                ms.release()
                            }
                        }
                    }
                }
            }
        }
    }

}
