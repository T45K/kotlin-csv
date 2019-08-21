package com.github.doyaaaaaken.kotlincsv.client

import com.github.doyaaaaaken.kotlincsv.dsl.context.CsvReaderContext
import com.github.doyaaaaaken.kotlincsv.dsl.context.ICsvReaderContext
import com.github.doyaaaaaken.kotlincsv.parser.CsvParser
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import kotlin.streams.asSequence

/**
 * CSV Reader class, which decides where to read from and how to read.
 *
 * @author doyaaaaaken
 */
class CsvReader(ctx: CsvReaderContext = CsvReaderContext()) : ICsvReaderContext by ctx {

    private val parser = CsvParser()

    fun read(data: String): List<List<String>> {
        return readAsSequence(data).toList()
    }

    fun read(file: File): List<List<String>> {
        return readAsSequence(file).toList()
    }

    fun read(ips: InputStream): List<List<String>> {
        return readAsSequence(ips).toList()
    }

    fun readAsSequence(data: String): Sequence<List<String>> {
        val br = data.byteInputStream(charset).bufferedReader(charset)
        return readWithBufferedReader(br)
    }

    fun readAsSequence(file: File): Sequence<List<String>> {
        val br = file.inputStream().bufferedReader(charset)
        return readWithBufferedReader(br)
    }

    fun readAsSequence(ips: InputStream): Sequence<List<String>> {
        val br = ips.bufferedReader(charset)
        return readWithBufferedReader(br)
    }

    private fun readWithBufferedReader(br: BufferedReader): Sequence<List<String>> {
        var leftOver = ""
        return br.lines().asSequence().mapNotNull { line ->
            //TODO: retain line separator as it exists in csv file
            val lineSeparator = System.lineSeparator()
            val value = if (leftOver.isEmpty()) {
                "${line}$lineSeparator"
            } else {
                "${leftOver}$lineSeparator${line}$lineSeparator"
            }
            val parsedLine = parser.parseRow(value, quoteChar, delimiter, escapeChar)

            //TODO: check if list size is valid as csv requirement

            if (parsedLine == null) {
                leftOver = "${leftOver}${line}"
            } else {
                leftOver = ""
            }
            parsedLine
        }
    }
}
