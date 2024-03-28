package battleship.client.elements

import battleship.client.interfaces.IViewGroup
import battleship.client.resources.Images
import battleship.client.resources.Sounds
import processing.core.PConstants
import processing.core.PImage
import processing.core.PVector

open class Button(
    position: PVector,
    textString: String?,
    var background_default: PImage? = Images.Button,
    var background_hover: PImage? = Images.Button_Pressed
) :
    IViewGroup(position, background_default) {

    val text = Text(PVector(size.x / 2f, size.y / 2f), textString ?: "")
        .apply { drawMode = PConstants.CENTER }

    init {
        addView(text)
    }

    override var isActive: Boolean
        get() = super.isActive
        set(value) {
            if (super.isActive != value) {
                super.isActive = value
                //update background when active state changes
                this.background = if (value) {
                    Sounds.Button_Click.play()
                    background_hover ?: background_default
                } else {
                    background_default
                }
            }
        }

    override fun onMouseOver(isMouseOver: Boolean) {
        //button is active when mouse is over
        this.background = if (isMouseOver) {
            Sounds.Button_Click.play()
            background_hover ?: background_default
        } else {
            background_default
        }
        super.onMouseOver(isMouseOver)
    }

}