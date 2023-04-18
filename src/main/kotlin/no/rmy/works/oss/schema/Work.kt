package no.rmy.works.oss.schema

import kotlinx.serialization.Serializable
import no.rmy.works.oss.io.OssFile

@Serializable
data class Work(
    val id: String,
    val title: String,
    val longTitle: String,
    val year: Int,
    val genre: String,
    val wordCount: Int,
    val paragraphCount: Int
) {
    constructor(m: Map<String, Any>) : this(
        // ~WorkID~,~Title~,~LongTitle~,~Date~,~GenreType~,~Notes~,~Source~,~TotalWords~,~TotalParagraphs~
        id = m["WorkID"] as String,
        title = m["Title"] as String,
        longTitle = m["LongTitle"] as String,
        year = m["Date"] as Int,
        genre = m["GenreType"] as String,
        wordCount = m["TotalWords"] as Int,
        paragraphCount = m["TotalParagraphs"] as Int
    )

    override fun toString(): String = "$title"
    val chapters: List<Chapter> get() = Chapter.byWork[id] ?: listOf()
    val roles: List<Role> get() = Role.byWork[id] ?: listOf()

    companion object {
        val all: Map<String, Work> = OssFile("oss/Works.txt").all
            .map { Work(it) }.associateBy { it.id }
    }
}
