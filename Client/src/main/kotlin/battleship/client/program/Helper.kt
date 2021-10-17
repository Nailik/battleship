package battleship.client.program

import kotlinx.serialization.json.encodeToJsonElement
import processing.core.PFont
import processing.core.PVector
import battleship.server.data.DataPacket
import battleship.server.data.DataType
import battleship.server.program.jsonFormat
import java.applet.Applet
import java.awt.Font
import java.io.InputStream
import java.util.*

inline fun <reified T>DataPacket(type: DataType, data: T) : DataPacket {
    return DataPacket(type, jsonFormat.encodeToJsonElement(data).toString(), Logic.userSettings.uuid)
}

operator fun PVector.plus(position: PVector?): PVector {
    return position?.let {
        PVector(this.x + it.x, this.y + it.y)
    }?: kotlin.run {
        this
    }
}

operator fun PVector.minus(position: PVector?): PVector {
    return position?.let {
        PVector(this.x - it.x, this.y - it.y)
    }?: kotlin.run {
        this
    }
}

operator fun PVector.times(factor: Float): PVector {
    return PVector(this.x * factor, this.y * factor)
}

fun createFontFromRessourceTTF(name: String, size: Float): PFont {
    val baseFont = Font.createFont(0, Sketch::class.java.getResourceAsStream(name))
    return PFont(baseFont.deriveFont(size * Sketch.g.parent.pixelDensity.toFloat()), false, null, true, Sketch.g.parent.pixelDensity)
}