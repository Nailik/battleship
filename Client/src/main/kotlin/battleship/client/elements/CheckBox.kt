package battleship.client.elements

import processing.core.PImage
import processing.core.PVector
import battleship.client.interfaces.IViewGroup
import battleship.client.resources.Images
import battleship.client.resources.Sounds

class CheckBox(
    position: PVector, var background_unchecked: PImage, var background_checked: PImage,
    var background_unchecked_pressed: PImage, var background_checked_pressed: PImage, textString: String = ""
) : IViewGroup(position, Images.Sound_On) {

    val text = Text(PVector(85f, size.y / 2f + 5f), textString)

    init {
        addView(text)
        updateBackground()
    }

    var value = false
        set(value) {
            if (field != value) {
                field = value
                updateBackground()
            }
            field = value
        }

    override fun onClickedLeft() {
        value = !value
        super.onClickedLeft()
    }

    override fun onMouseOver(isMouseOver: Boolean) {
        if(isEnabled) {
            if (isMouseOver) {
                Sounds.Button_Click.play()
                background = if (value) {
                    background_checked_pressed
                } else {
                    background_unchecked_pressed
                }
            } else {
                updateBackground()
            }
        }
        super.onMouseOver(isMouseOver)
    }

    private fun updateBackground() {
        background = if (value) {
            if (isMouseOver) {
                background_checked_pressed
            } else {
                background_checked
            }
        } else {
            if (isMouseOver) {
                background_unchecked_pressed
            } else {
                background_unchecked

            }
        }
    }

}