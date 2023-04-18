package no.rmy.works.view

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.html.*
import kotlinx.html.*
import no.rmy.works.oss.schema.Chapter
import no.rmy.works.oss.schema.Work


fun Route.chapterRoutes() {
    route("chapter") {
        get("{workId}/{chapterId}") {
            val workId = call.parameters["workId"] as String
            val chapterId = call.parameters["chapterId"]?.toInt() ?: -1

            Work.all[workId]?.let { work ->
                Chapter.all[chapterId]?.let { chapter ->
                    call.respondHtmlTemplate(LayoutTemplate()) {
                        header { +chapter.toString() }
                        crumb {
                            +" > "
                            a(href = ResourcePath.work(workId)) { +work.title }
                        }
                        content {
                            articleTitle {
                                +work.title
                                + ": "
                                +chapter.toString()
                            }
                            articleText {
                                insert(SpeechTemplate(chapter)) {}
                            }
                        }
                    }
                }
            }
        }
    }
}
