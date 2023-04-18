package no.rmy.works.stats

import no.rmy.works.oss.schema.Role
import no.rmy.works.oss.schema.Paragraph
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class NGramCount {
    data class Count(val gram: String, val count: Int)

    private val gramCounter = mutableMapOf<String, AtomicInteger>()

    fun aggregate(s: String, gramSize: Int = 3) {
        for (index in 0 until (s.length - gramSize)) {
            val gram = s.substring(index until index + gramSize)
            gramCounter.getOrPut(gram) { AtomicInteger(0) }.incrementAndGet()
        }
    }

    fun aggregateByWords(s: String, gramSize: Int = 3) {
        val words = s.split("\\s+".toRegex()).toTypedArray()
        for (index in 0 until (words.size - gramSize)) {
            val gWords = words.copyOfRange(index, index + gramSize)
            val gram = gWords.joinToString(" ")
            // logger.debug("${gWords.size} - '${gWords.joinToString(" ")}': ${words.joinToString(" ")}")
            gramCounter.getOrPut(gram) { AtomicInteger(0) }.incrementAndGet()
        }
    }

    fun top(maxCount: Int = 100): List<Count> {
        return gramCounter.map { Count(it.key, it.value.get()) }
            .sortedByDescending { it.count }
            .take(maxCount)
    }

    fun ofWork(workId: String): List<Count> {
        val names = Role.byWork[workId]
            ?.filter { it.name != "All" }
            ?.map { it.name }?.toSet()
            ?: setOf()
        Paragraph.byWork[workId]?.let { paragraphs ->
            paragraphs.filter { !it.charId.equals("xxx") }.forEach {
                val cleaned = it.plainText
                    .replace("\\[.*?\\]".toRegex(), " ")
                    .replace("[p]", " ")
                    .replace("\\p{Punct}".toRegex(), "")
                    .replace("\\s+".toRegex(), " ")
                    .split("\\s+".toRegex())
                    .map { if (names.contains(it)) "X" else it }
                    .toTypedArray()
                    .joinToString(" ")
                    .lowercase(Locale.ENGLISH)
                aggregateByWords(cleaned)
            }
        }
        return top()
    }

    companion object {
        val logger = LoggerFactory.getLogger(this.javaClass)
    }
}
