package org.mbari.biauv.integration

import java.io.{FileInputStream, File}
import java.util.regex.Pattern
import java.nio.charset.Charset
import java.nio.channels.FileChannel
import java.nio.{MappedByteBuffer, CharBuffer, ByteOrder}
import org.slf4j.LoggerFactory

/**
 * This object does the actual reading of a file
 *
 * @author Brian Schlining
 * @since Sep 7, 2010
 */
object LogRecordReader {

    private val log = LoggerFactory.getLogger(getClass)

    /**
     * Reads a file and returns the records in the log file (including all data)
     *
     * @param file The file to parse
     * @return A List of all records in the log file
     */
    def read(file: File): List[LogRecord] = {
        val startTime = System.nanoTime
        log.debug("Reading " + file.getCanonicalPath)
        val fileInputStream = new FileInputStream(file)
        val fileChannel = fileInputStream.getChannel
        // Get the filessize and map it into memory
        val mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size())
        mappedByteBuffer.order(ByteOrder.LITTLE_ENDIAN)
         // Decode the file into a char buffer
        val (records, position) = readHeader(mappedByteBuffer)
        log.debug("Found " + records.size + " data types")
        records.foreach { println(_) }
        log.debug("Reading binary data, starting at byte " + position)
        mappedByteBuffer.position(position)

        def readValue(logRecord: LogRecord) = {
            logRecord.format match {
                case "Float" => mappedByteBuffer.getFloat()
                case "Int" => mappedByteBuffer.getInt()
                case "Short" => mappedByteBuffer.getShort()
                case "Double" => mappedByteBuffer.getDouble()
            }
        }

        // Read binary data
        var i = 0
        try {
            while(true) {
                records.foreach { r =>
                    r.data = readValue(r) :: r.data
                }
                i = i + 1
            }
        }
        catch {
            case _ => log.debug("Done. Found " + i + " records")
        }

        records.foreach { r => r.data = r.data.reverse }
        val elapsedTime = (System.nanoTime - startTime) / 1000D / 1000D / 1000D
        log.debug(String.format("Elapsed time is %12.9f nano seconds\n", Array(elapsedTime)))
        return records

    }

    /**
     * Parses the ASCII header into something useful
     *
     * @param buffer The buffer of the memory mapped log file
     * @return A tuple with a list of LogRecords that have not yet been populated with data
     *      as well as the byte offset to start reading the data with
     *
     */
    private def readHeader(mappedByteBuffer: MappedByteBuffer): (List[LogRecord], Int) = {
        val charsetDecoder = Charset.forName("ISO-8859-15").newDecoder()
        val charBuffer = charsetDecoder.decode(mappedByteBuffer)

        var records: List[LogRecord] = Nil
        val linePattern = Pattern.compile(".*\r?\n")
        val lineMatcher = linePattern.matcher(charBuffer)
        var continue = lineMatcher.find()
        var numberOfBytes = 0
        while(continue) {
            val line = lineMatcher.group().toString // The current line
            numberOfBytes = numberOfBytes + line.getBytes.length
            log.debug("--- Parsing: " + line.replace('\r', ' ').replace('\n', ' '))
            val parts = line.split(" ")
            continue = !parts(1).startsWith("begin")
            if (continue) {
                try {
                    val otherParts = line.split(",")
                    records = newLogRecord(parts(1), parts(2), otherParts(1), otherParts(2)) :: records
                }
                catch {
                    case _ => log.debug("!!! Invalid line")
                }
                continue = lineMatcher.find()
            }
        }
        return (records.reverse, numberOfBytes)
    }


    /**
     * Factory method that standardizes some fields of the log records.
     *
     */
    private def newLogRecord(format: String, shortName: String, longName: String, units: String): LogRecord = {
        val f = format match {
            case "float" => "Float"
            case "integer" => "Int"
            case "short" => "Short"
            case _ => "Double"
        }

        val u = shortName match {
            case "time" => "seconds since 1970-01-01 00:00:00Z"
            case _ => units.replaceAll("[\r\n]", "").trim
        }

        return new LogRecord(f, shortName, longName, u)
    }

}