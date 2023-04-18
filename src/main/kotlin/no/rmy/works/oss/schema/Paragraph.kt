package no.rmy.works.oss.schema

import no.rmy.works.oss.io.OssFile

data class Paragraph(
    val id: Int,
    val workId: String,
    val charId: String,
    val number: Int,
    val plainText: String,
    val phoneticText: String,
    val stemText: String,
    val section: Int,
    val chapter: Int,
    val charCount: Int,
    val wordCount: Int
) {
    // ~WorkID~,~ParagraphID~,~ParagraphNum~,~CharID~,~PlainText~,~PhoneticText~,~StemText~,~ParagraphType~,~Section~,~Chapter~,~CharCount~,~WordCount~
    // ~12night~,630978,281,~VIOLA~,~I think not so, my lord.
    //~,~I 0NK NT S M LRT ~,~i think not so my lord ~,~b~,1,4,25,6
    constructor(m: Map<String, Any>) : this(
        id = m["ParagraphID"] as Int,
        workId = m["WorkID"] as String,
        charId = m["CharID"] as String,
        number = m["ParagraphNum"] as Int,
        plainText = m["PlainText"] as String,
        phoneticText = m["PhoneticText"] as String,
        stemText = m["StemText"] as String,
        section = m["Section"] as Int,
        chapter = m["Chapter"] as Int,
        charCount = m["CharCount"] as Int,
        wordCount = m["WordCount"] as Int,
    )

    val line: Int
        get() =
            Chapter.byName[Chapter.name(workId, section, chapter)]?.let { ch ->
                number - (ch.paragraphs.firstOrNull()?.number ?: -1) + 1
            } ?: number

    companion object {
        private val paragraphs = OssFile("oss/Paragraphs.txt").all.map { Paragraph(it) }

        val all: Map<Int, Paragraph> = paragraphs.associateBy { it.id }

        val byWork: Map<String, List<Paragraph>> = paragraphs.groupBy { it.workId }
        val byChapter: Map<String, List<Paragraph>> =
            paragraphs.groupBy { Chapter.name(it.workId, it.section, it.chapter) }
    }
}
