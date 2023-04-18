package no.rmy.works.stats

import no.rmy.works.oss.schema.*
import kotlin.math.min

class SpeechDistance(val char: Role, val paragraphs: List<Paragraph>) {
    fun linesSince(p: List<Paragraph>): List<Int> {
        var dist = 100
        return p.map {
            if(it.charId.equals(char.id) || it.plainText.contains(char.name)) {
                dist = 0
            }
            else {
                ++dist
            }
            dist
        }
    }


    fun distance(): List<Pair<Int, Paragraph>> {
        val after = linesSince(paragraphs)
        val before = linesSince(paragraphs.reversed()).reversed()
        return before.zip(after).map { min(it.first, it.second) }.zip(paragraphs)
    }
}
