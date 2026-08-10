package com.carenest.provider.core.network.socket.stomp

data class StompFrame(
    val command: StompCommand,
    val headers: Map<String, String> = emptyMap(),
    val body: String? = null
) {
    fun encode(): String {
        val builder = StringBuilder()
        builder.append(command.name).append("\n")
        headers.forEach { (key, value) ->
            val escapedKey = escapeHeader(key)
            val escapedValue = escapeHeader(value)
            builder.append(escapedKey).append(":").append(escapedValue).append("\n")
        }
        builder.append("\n")
        if (body != null) {
            builder.append(body)
        }
        builder.append("\u0000")
        return builder.toString()
    }

    companion object {
        fun decodeAll(raw: String): List<StompFrame> {
            val frames = mutableListOf<StompFrame>()
            var offset = 0
            while (offset < raw.length) {
                // Skip leading heartbeats/whitespace
                while (offset < raw.length && (raw[offset] == '\n' || raw[offset] == '\r')) {
                    offset++
                }
                if (offset >= raw.length) break

                val nullIndex = raw.indexOf('\u0000', offset)
                if (nullIndex == -1) {
                    // Try to decode what's left if no null terminator, though spec says there should be one
                    decodeSingle(raw.substring(offset))?.let { frames.add(it) }
                    break
                }

                val frameText = raw.substring(offset, nullIndex)
                decodeSingle(frameText)?.let { frames.add(it) }
                offset = nullIndex + 1
            }
            return frames
        }

        private fun decodeSingle(text: String): StompFrame? {
            val lines = text.replace("\r\n", "\n").split("\n")
            if (lines.isEmpty() || lines[0].isBlank()) return null

            val commandStr = lines[0].trim()
            val command = try {
                StompCommand.valueOf(commandStr)
            } catch (e: Exception) {
                return null
            }

            val headers = mutableMapOf<String, String>()
            var lineIndex = 1
            while (lineIndex < lines.size && lines[lineIndex].isNotBlank()) {
                val line = lines[lineIndex]
                val colonIndex = line.indexOf(':')
                if (colonIndex != -1) {
                    val key = unescapeHeader(line.substring(0, colonIndex).trim())
                    val value = unescapeHeader(line.substring(colonIndex + 1).trim())
                    headers[key] = value
                }
                lineIndex++
            }

            // Body starts after the first blank line
            val body = if (lineIndex < lines.size - 1) {
                val bodyText = lines.drop(lineIndex + 1).joinToString("\n")
                if (bodyText.isEmpty()) null else bodyText
            } else {
                null
            }

            return StompFrame(command, headers, body)
        }

        private fun escapeHeader(value: String): String {
            return value
                .replace("\\", "\\\\")
                .replace("\n", "\\n")
                .replace(":", "\\c")
                .replace("\r", "\\r")
        }

        private fun unescapeHeader(value: String): String {
            return value
                .replace("\\r", "\r")
                .replace("\\c", ":")
                .replace("\\n", "\n")
                .replace("\\\\", "\\")
        }
    }
}
