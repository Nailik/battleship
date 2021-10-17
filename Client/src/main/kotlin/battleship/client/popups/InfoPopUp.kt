package battleship.client.popups

import battleship.client.elements.Button
import battleship.client.elements.Text
import battleship.client.interfaces.IView
import battleship.client.resources.Images
import processing.core.PConstants
import processing.core.PVector

class InfoPopUp(title: String, message: String) : IPopUp(title) {

    private val textView =
        Text(PVector(size.x / 2, 475f), message).apply { drawMode = PConstants.CENTER }

    private val ok = Button(PVector(1050f, 630f), "OK", Images.Button, Images.Button_Pressed).apply {
        clickedLeft = {
            this@InfoPopUp.close()
        }
    }

    override fun open() {
        addView(textView)
        addView(ok)
    }
}