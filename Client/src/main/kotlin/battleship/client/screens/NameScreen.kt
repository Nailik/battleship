package battleship.client.screens

import processing.core.PVector
import battleship.client.program.Logic
import battleship.client.elements.Button
import battleship.client.elements.Text
import battleship.client.elements.TextInput
import battleship.client.interfaces.IScreen
import battleship.client.resources.Images

/**
 * Beim Ersten Öffnen: (NameScreen)

Elemente:
- Text
- Texteingabe
- speichern button

Funktion:
- Benutzer kann einen Namen auswählen
 */
class NameScreen : IScreen(background = Images.Background) {

    private val welcomeText = Text(PVector(300f, 100f), "Welcome to Battleship - please select a username")

    /**
     * Benutzername
     */
    private val nameInput = TextInput(PVector(300f, 300f), "UserName", Images.Input_Text, Images.Input_Text_Hover).apply {
        submit = { it?.also { Logic.onChooseName(it) } }
        onChange = {
            if (it?.isNotBlank() == true) {
                startButton.isEnabled = true
            }
        }
    }

    /**
     * zu startbildschirm
     */
    private val startButton: Button = Button(PVector(300f, 500f), "Start", Images.Button, Images.Button_Pressed).apply {
        isEnabled = false
        clickedLeft = { nameInput.input?.also { name -> Logic.onChooseName(name) } }
    }

    override fun open() {
        /**
         * Benutzername
         */
        addView(welcomeText)
        addView(nameInput)
        addView(startButton)
    }
}