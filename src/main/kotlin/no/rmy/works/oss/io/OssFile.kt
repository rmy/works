package no.rmy.works.oss.io

import no.rmy.works.Works
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.File

class OssFile(filename: String) {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private var nextCh = ' '

    val all = if(File("src/main/dist/$filename").exists())
        parse("src/main/dist/$filename")
    else
        parse("$filename")



    private fun fetchNextChar(reader: BufferedReader): Boolean {
        var chInt = reader.read()
        if(chInt == 10)
            chInt = reader.read()
        logger.trace("Char: $chInt")
        if (chInt == -1)
            return false
        nextCh = chInt.toChar()
        return true
    }

    private fun readString(b: BufferedReader): String {
        val s = StringBuffer()
        nextCh = b.read().toChar()
        while (nextCh != '~') {
            s.append(nextCh)
            nextCh = b.read().toChar()
        }
        nextCh = b.read().toChar()

        logger.trace("Read string: ${s}")

        return s.toString().replace("&#8217;", "'")
    }


    private fun readNum(b: BufferedReader): Int {
        val s = StringBuffer()
        s.append(nextCh)
        nextCh = b.read().toChar()
        while (nextCh.isDigit()) {
            s.append(nextCh)
            nextCh = b.read().toChar()
        }
        logger.trace("Read number: ${s}")
        return s.toString().toInt()
    }

    private fun parseList(reader: BufferedReader): List<List<Any>> {
        val allLines = mutableListOf<List<Any>>()
        var line = mutableListOf<Any>()
        while (fetchNextChar(reader)) {
            val w = when (nextCh) {
                '~' -> readString(reader)
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> readNum(reader)
                ',' -> ""
                else -> {
                    logger.warn("Something rotten: '${nextCh}'")
                    throw Exception("Something rotten")
                }
            }
            logger.trace("Adding word ${w.toString()}")
            line.add(w)
            when (nextCh) {
                13.toChar() -> {
                    allLines.add(line)
                    line = mutableListOf()
                }

                ' ' -> {
                    while (nextCh == ' ')
                        fetchNextChar(reader)
                }

                ',' -> {}

                else -> {
                    throw Exception("Unknown delimiter: ${nextCh.code} ${nextCh}")
                }
            }
        }

        return allLines
    }

    fun parse(filename: String) : List<Map<String, Any>> {
        logger.info("Reading $filename")
        val reader = File(filename).bufferedReader()
        try {
            val allLines = parseList(reader)
            allLines.firstOrNull()?.let { fields ->
                fields as List<String>
                Works.logger.info("Fields: $fields")
                return allLines.drop(1).map { fields.zip(it).toMap() }
            }
        }
        finally {
            reader.close()
        }
        return listOf()
    }
}
