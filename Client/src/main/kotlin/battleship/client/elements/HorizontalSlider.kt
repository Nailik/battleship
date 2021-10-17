package battleship.client.elements

import battleship.client.interfaces.IViewGroup
import battleship.client.program.Sketch
import battleship.client.resources.Images
import processing.core.PVector

class HorizontalSlider(position: PVector, percentage: Float, var length: Float) :
    IViewGroup(position, null) {

    var button = Button(position.copy().apply { y -= 10 }, null, Images.SlideButton, Images.SlideButton_Pressed)

    var image = Image(position.copy(), Images.SlideBar, PVector(length, 20f))

    var onChange: ((percentage: Float) -> Unit)? = null

    init {
        addView(image)
        addView(button)

        //setup with current percentage
        button.position.x += length * percentage
    }

    override fun update() {
        if (button.isMouseOver && Sketch.mousePressed && !isActive) {
            isActive = true
        } else if (!Sketch.mousePressed) {
            isActive = false
        }

        //check if currently pressed and enabled
        if (isActive) {
            //move with mouse
            var x = button.position.x + Sketch.mousePosition.x - Sketch.oldMousePosition.x
            if (x < position.x) {
                x = position.x
            }
            if (x > position.x + length) {
                x = position.x + length
            }
            if (x != button.position.x) {
                button.position.x = x
                onChange?.invoke(((button.position.x - position.x) / length))

            }
        }

        super.update()
    }
}