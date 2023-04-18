package no.rmy.works.oss.schema

import kotlinx.serialization.Serializable
import no.rmy.works.oss.io.OssFile

@Serializable
class Chapter(val workId: String, val id: Int, val section: Int, val chapter: Int, val description: String) {
    constructor(m: Map<String, Any>) : this(
        // ~WorkID~,~ChapterID~,~Section~,~Chapter~,~Description~
        workId = m["WorkID"] as String,
        id = m["ChapterID"] as Int,
        section = m["Section"] as Int,
        chapter = m["Chapter"] as Int,
        description = m["Description"] as String
    )

    val work: String get() = Work.all[workId].toString()

    override fun toString(): String =
        "$section.$chapter $description"

    val paragraphs: List<Paragraph> get() = Paragraph.byChapter[ name(workId, section, chapter) ] ?: listOf()
    val characters: Set<String> get() = paragraphs.map { it.charId }.toSet()

    companion object {
        private val ch = OssFile("oss/Chapters.txt").all.map { Chapter(it) }

        val all: Map<Int, Chapter> = ch.associateBy { it.id }
        val byWork: Map<String, List<Chapter>> = ch.groupBy { it.workId }

        fun name(workId: String, section: Int, chapter: Int) = "$workId.$section.$chapter"
        val byName: Map<String, Chapter> = ch.associateBy { name(it.workId, it.section, it.chapter) }

    }
}