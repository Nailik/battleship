package battleship.client.interfaces

import battleship.client.elements.Image
import battleship.client.program.Sketch
import battleship.client.program.plus
import battleship.client.resources.Sizes
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PImage
import processing.core.PVector

abstract class IView(
    open var position: PVector,
    _background: PImage? = null,
    _size: PVector? = null
) {

    var drawMode = PConstants.CORNER

    var size: PVector = _size ?: PVector(
        (_background?.width?.toFloat() ?: 0f) * Sizes.SIZE_PIXEL.size,
        (_background?.height?.toFloat() ?: 0F) * Sizes.SIZE_PIXEL.size
    )

    var isEnabled = true
    var isVisible = true
    var rotation = 0f
    open var isActive = false

    open var disableOffset = false

    var parent: IView? = null

    var offset = PVector(0f, 0f)
        private set

    val animations = mutableListOf<IAnimation>()

    var background: PImage? = _background
        set(value) {
            field = value
            field?.also {
                backgroundImage = Image(PVector(0f, 0f), it).apply { attach(this@IView) }
            }
        }

    var backgroundImage: Image? = background?.let { Image(PVector(0f, 0f), it).apply { attach(this@IView) } }

    //between 0 and 255
    var alpha = 254f
    var currentAlpha = alpha

    fun draw() {
        currentAlpha = if (!isEnabled || parent?.isEnabled == false) alpha / 2f else alpha

        if (isVisible) {
            animations.forEach {
                it.animate()
            }
        }

        if (isVisible) {
            val finalPosition = if (!disableOffset) {
                offset = PVector(0f, 0f)
                offset += parent?.position
                offset += parent?.offset
                position + offset
            } else {
                position
            }

            if (rotation != 0f) {
                //always set to center in order to rotate
                Sketch.imageMode(PConstants.CENTER)
                Sketch.ellipseMode(PConstants.CENTER)
                Sketch.rectMode(PConstants.CENTER)

                // Now we want to rotate the image 'in position'
                Sketch.pushMatrix()
                //position where image should be

                //depending on wanted mode (center/corner) move it to wanted position
                if (drawMode == PConstants.CORNER) {
                    Sketch.translate(
                        finalPosition.x - size.x / 2f,
                        finalPosition.y - size.y / 2f
                    )
                } else if (drawMode == PConstants.CENTER) {
                    Sketch.translate(finalPosition.x, finalPosition.y)
                }

                Sketch.rotate(PApplet.radians(rotation)) // rotate

                Sketch.tint(255f, currentAlpha)

                //draw on 0/0 because of translation
                //draw background
                backgroundImage?.draw()

                draw(PVector(0f, 0f))

                Sketch.popMatrix() // restore previous graphics matrix

                //always restore modes
                Sketch.imageMode(PConstants.CORNER)
                Sketch.ellipseMode(PConstants.CORNER)
                Sketch.rectMode(PConstants.CORNER)
            } else {
                Sketch.imageMode(drawMode)
                Sketch.ellipseMode(drawMode)
                Sketch.rectMode(drawMode)

                Sketch.tint(255f, currentAlpha)

                //draw background
                backgroundImage?.draw()
                draw(finalPosition)
            }
        }
    }

    fun attach(newParent: IView) {
        parent = newParent
    }

    open fun update() {

    }


    open fun drawModeChanged() {

    }

    abstract fun draw(position: PVector)

    open fun onClickedLeft(view: IView) {
        parent?.onClickedLeft(view)
    }
}