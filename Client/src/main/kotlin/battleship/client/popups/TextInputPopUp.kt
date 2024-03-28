package battleship.client.popups

import battleship.client.elements.Button
import battleship.client.elements.TextInput
import battleship.client.resources.Images
import processing.core.PVector

class TextInputPopUp(text: String, hint: String? = null) : IPopUp(text) {

    var submit: ((input: String?) -> Unit)? = null

    private val textInput = TextInput(PVector(800f, 475f), hint, Images.Input_Text, Images.Input_Text_Hover)
        .also {
            it.submit = { input ->
                this@TextInputPopUp.close()
                this.submit?.invoke(input)
            }
        }

    private val submitButton = Button(PVector(1050f, 630f), "Ok", Images.Button, Images.Button_Pressed)
        .also {
            it.clickedLeft = {
                this@TextInputPopUp.close()
                this.submit?.invoke(textInput.input)
            }
        }

    private val cancelButton = Button(PVector(700f, 630f), "Cancel", Images.Button, Images.Button_Pressed).apply {
        clickedLeft = {
            this@TextInputPopUp.close()
        }
    }

    override fun open() {
        addView(textInput)
        addView(submitButton)
        addView(cancelButton)
    }

}