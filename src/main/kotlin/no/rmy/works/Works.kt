package no.rmy.works

import no.rmy.works.lucene.WorkIndex
import no.rmy.works.oss.io.OssFile
import no.rmy.works.oss.schema.Work
import org.slf4j.LoggerFactory


object Works {
    var chapters: List<Map<String, Any>> = listOf()
    var characters: List<Map<String, Any>> = listOf()
    var paragraphs: List<Map<String, Any>> = listOf()
    var works: List<Map<String, Any>> = listOf()
    var wordForms: List<Map<String, Any>> = listOf()
    val logger = LoggerFactory.getLogger(this.javaClass)
    val workIndex = WorkIndex()

    fun init(): Boolean {
        chapters = OssFile("oss/Chapters.txt").all
        characters = OssFile("oss/Characters.txt").all
        paragraphs = OssFile("oss/Paragraphs.txt").all
        wordForms = OssFile("oss/WordForms.txt").all

        works = OssFile("oss/Works.txt").all
        works.map {
            Work(it)
        }

        OssFile("oss/Works.txt").all.map { Work(it) }

        workIndex.writeAll()

        return true
    }
}
