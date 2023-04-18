package no.rmy.works.view

import io.ktor.server.html.*
import kotlinx.html.*
import no.rmy.works.oss.schema.Chapter
import no.rmy.works.oss.schema.Role
import java.util.*

class SpeechTemplate(val chapter: Chapter, val skipSet: Set<Int> = setOf()) : Template<FlowContent> {
    override fun FlowContent.apply() {
        ul {
            var skipped = StringBuffer()
            val paragraphs = chapter.paragraphs
            val firstLineInChapter = paragraphs.firstOrNull()?.number ?: 0

            h3 {
                +"${chapter.section}.${chapter.chapter} ${chapter.description}"
            }
            paragraphs.map { para ->
                if (skipSet.contains(para.id)) {
                    skipped.append(".")
                } else {
                    if (skipped.isNotEmpty()) {
                        li { +skipped.toString() }
                        skipped = StringBuffer()
                    }
                    li {
                        val lines = (para.plainText.split("[p]"))
                        val lineInChapter = para.number - firstLineInChapter + 1
                        if (para.charId != "xxx") {
                            +((Role.all[para.charId]?.name
                                ?: "").uppercase(Locale.ENGLISH))
                            br { +" ${lines.first()} (${lineInChapter})" }
                            lines.drop(1).map { line ->
                                br { +line }
                            }
                        } else {
                            i {
                                +" ${lines.first()} (${lineInChapter})"
                                lines.drop(1).map { line ->
                                    +line
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
