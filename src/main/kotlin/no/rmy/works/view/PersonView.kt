package no.rmy.works.view

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.html.*
import kotlinx.html.*
import no.rmy.works.oss.schema.Role
import no.rmy.works.oss.schema.Work
import no.rmy.works.stats.SpeechDistance


fun Route.personRoutes() {
    route("person") {
        get("{workId}/{charId}") {
            val workId = call.parameters["workId"] as String
            val charId = call.parameters["charId"] as String

            Work.all[workId]?.let { work ->
                Role.all[charId]?.let { char ->
                    call.respondHtmlTemplate(LayoutTemplate(char.name)) {
                        header { +char.name }
                        crumb {
                            +" > "
                            a(href = ResourcePath.work(workId)) { +work.title }
                        }
                        content {
                            articleTitle {
                                +work.title
                                +": "
                                +char.name
                            }
                            articleText {
                                ul {
                                    work.chapters.filter { it.characters.contains(char.id) }.map { chapter ->
                                        li {
                                            val skipSet = SpeechDistance(char, chapter.paragraphs)
                                                .distance()
                                                .filter { it.first > 2 }
                                                .map { it.second.id }
                                                .toSet()
                                            insert(SpeechTemplate(chapter, skipSet)) {}
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        get("stripped/{workId}/{charId}") {
            val workId = call.parameters["workId"] as String
            val charId = call.parameters["charId"] as String

            Work.all[workId]?.let { work ->
                Role.all[charId]?.let { char ->
                    call.respondHtmlTemplate(LayoutTemplate()) {
                        header { +char.name }
                        crumb {
                            +" > "
                            a(href = ResourcePath.work(workId)) { +work.title }
                        }
                        content {
                            articleTitle { +char.name }
                            articleText {
                                ul {
                                    work.chapters.filter { it.characters.contains(char.id) }.map { chapter ->
                                        val firstLineInChapter = chapter.paragraphs.firstOrNull()?.number ?: 0
                                        li {
                                            insert(SpeechTemplate(chapter)) {}
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