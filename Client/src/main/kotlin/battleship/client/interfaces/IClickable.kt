package battleship.client.interfaces

import battleship.client.program.Sketch
import battleship.client.program.plus
import processing.core.PConstants
import processing.core.PImage
import processing.core.PVector

abstract class IClickable(position: PVector, background: PImage?, size: PVector?) : IView(position, background, size) {

    var isMouseOver = false
        set(value) {
            if (field != value) {
                field = value
                onMouseOver(value)
            }
        }

    private var isCurrentlyPressedLeft = false
        set(value) {
            if (field && !value && isEnabled && isMouseOver) {
                field = value
                onClickedLeft()
            } else {
                field = value
            }
        }

    private var isCurrentlyPressedRight = false
        set(value) {
            if (field && !value && isEnabled && isMouseOver) {
                field = value
                onClickedRight()
            } else {
                field = value
            }
        }

    var clickedLeft: (() -> Unit)? = null
    var clickedRight: (() -> Unit)? = null

    var mouseOver: ((isOver: Boolean) -> Unit)? = null

    open fun checkIsMouseOver(): Boolean {
        //ob x und y im rect
        //doesn't work yet when rotation on view is active
        val finalPosition = position + offset

        return if (drawMode == PConstants.CENTER) {
            Sketch.mousePosition.x >= (finalPosition.x - size.x / 2f) * (Sketch.scaleFactor) + 1 &&
                    Sketch.mousePosition.x <= (finalPosition.x + size.x / 2f) * (Sketch.scaleFactor) - 1 &&
                    Sketch.mousePosition.y >= (finalPosition.y - size.y / 2f) * (Sketch.scaleFactor) + 1 &&
                    Sketch.mousePosition.y <= (finalPosition.y + size.y / 2f) * (Sketch.scaleFactor) - 1
        } else {
            //corner
            Sketch.mousePosition.x >= (finalPosition.x) * (Sketch.scaleFactor) + 1 &&
                    Sketch.mousePosition.x <= (finalPosition.x + size.x) * (Sketch.scaleFactor) - 1 &&
                    Sketch.mousePosition.y >= (finalPosition.y) * (Sketch.scaleFactor) + 1 &&
                    Sketch.mousePosition.y <= (finalPosition.y + size.y) * (Sketch.scaleFactor) - 1
        }
    }

    override fun update() {
        updateMouse()
    }

    private fun updateMouse() {
        isMouseOver = checkIsMouseOver()

        if (!isMouseOver) {
            isCurrentlyPressedLeft = false
            isCurrentlyPressedRight = false
        } else if (isMouseOver) {
            isCurrentlyPressedLeft = Sketch.isMouseLeftPressed
            isCurrentlyPressedRight = Sketch.isMouseRightPressed
        }
    }

    open fun onMouseOver(isMouseOver: Boolean) {
        mouseOver?.also {
            it.invoke(isMouseOver)
        }
    }

    fun mouseReleased() {

    }

    open fun onClickedLeft() {
        parent?.onClickedLeft(this)
        clickedLeft?.invoke()
    }

    open fun onClickedRight() {
        clickedRight?.invoke()
    }

}