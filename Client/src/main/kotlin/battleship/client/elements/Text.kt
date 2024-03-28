package battleship.client.elements

import battleship.client.interfaces.IView
import battleship.client.program.Sketch
import battleship.client.resources.Colors
import battleship.client.resources.Sizes
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PShape
import processing.core.PVector
import kotlin.math.abs

class Text(
    position: PVector,
    _text: String,
    var color: Colors = Colors.C_BLACK,
    var textSize: Sizes = Sizes.SIZE_TXT_NORMAL
) : IView(position) {

    var textWidth = 0f
        private set
    var textHeight = 0f
        private set

    private var whitespace = 0f
    private var maxY = 0f

    var text = _text
        set(value) {
            if (field != value) {
                field = value

                calculateSize()
            }
        }

    init {
        calculateSize()
    }

    override fun draw(position: PVector) {
        //fix center mode later
        Sketch.textSize(textSize.size)
        Sketch.fill(color.red(), color.green(), color.blue(), currentAlpha)

        //fix center mode
        if (drawMode != PConstants.CENTER) {
            Sketch.text(text, position.x, position.y)
        } else {
            Sketch.text(text, position.x - textWidth / 2f, position.y - maxY + textHeight / 2f)
        }
    }

    private fun calculateSize() {
        if (text.isEmpty()) {
            textWidth = 0f
            textHeight = 0f
            whitespace = 0f
            maxY = 0f
            return
        }

        Sketch.textSize(textSize.size)
        var textWidth = Sketch.textWidth(text) // call Processing method

        val font = Sketch.g.textFont
        var minY = Float.MAX_VALUE
        maxY = Float.NEGATIVE_INFINITY

        for (c in text.toCharArray()) {
            val character: PShape = font.getShape(c) // create character vector
            for (i in 0 until character.vertexCount) {
                minY = PApplet.min(character.getVertex(i).y, minY)
                maxY = PApplet.max(character.getVertex(i).y, maxY)
            }
        }

        var glyph = font.getGlyph(text[text.length - 1])

        glyph?.also {
            whitespace = (font.width(text[text.length - 1]) * font.size - it.width) / 2
            textWidth -= whitespace // subtract whitespace of last character
        }

        glyph = font.getGlyph(text[0])

        glyph?.also {
            whitespace = (font.width(text[0]) * font.size - font.getGlyph(text[0]).width) / 2
        }
        textWidth -= whitespace // subtract whitespace of first character

        this.textWidth = textWidth
        this.textHeight = abs(maxY + 2f - minY)
    }
}