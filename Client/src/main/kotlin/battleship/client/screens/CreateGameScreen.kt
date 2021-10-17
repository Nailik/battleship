package battleship.client.screens

import battleship.client.animations.BackgroundAnimation
import processing.core.PVector
import battleship.client.program.Logic
import battleship.client.elements.Button
import battleship.client.elements.CheckBox
import battleship.client.resources.Colors
import battleship.client.elements.TextInput
import battleship.client.interfaces.IScreen
import battleship.client.resources.Images
import battleship.server.data.LobbySettings
import battleship.server.data.UserState

/**
 * Spiel erstellen (CreateGameScreen)

Elemente:
- Name des Spieles (damit es gefunden werden kann)
- Checkbox Passwort
- Passwortfeld (ausgegraut wenn checkbox aus ist)
- Button zum erstellen des Spieles
- zurückbutton

Funktion:
- Spieler kann Spiel erstellen
- Spiel kann mit Passwort versehen werden

 */
class CreateGameScreen : IScreen(background = Images.Background) {

    /**
     * Name des Spieles (damit es gefunden werden kann)
     */
    private val name = TextInput(PVector(300f, 200f), "Lobby Name", Images.Input_Text, Images.Input_Text_Hover).apply {
        txtColor = Colors.C_WHITE
        submit = {

        }
    }

    /**
     * Checkbox Passwort
     */
    private val enablePassword = CheckBox(
        PVector(300f, 400f),
        Images.Checkbox,
        Images.Checkbox_Checked,
        Images.Checkbox,
        Images.Checkbox_Checked,
        "Enable Password"
    ).apply {
        clickedLeft = {
            password.isEnabled = this.value
        }
    }

    /**
     * Passwortfeld (ausgegraut wenn checkbox aus ist)
     */
    private val password =
        TextInput(PVector(300f, 600f), "Password", Images.Input_Text, Images.Input_Text_Hover).apply {
            txtColor = Colors.C_WHITE
            isEnabled = false
            submit = {

            }
        }

    /**
     * Button zum erstellen des Spieles
     */
    private val createBtn = Button(PVector(300f, 800f), "create", Images.Button, Images.Button_Pressed).apply {
        clickedLeft = {
            name.input?.also { name ->
                Logic.onStartLobby(LobbySettings(name, password.input))
            }
        }
    }

    /**
     * zurückbutton
     */
    private val backBtn = Button(PVector(100f, 800f), null, Images.Back_Array, Images.Back_Array_Pressed).apply {
        clickedLeft = { Logic.userState = UserState.START_SCREEN }
    }

    override fun open() {
        addView(BackgroundAnimation)
        addView(name)
        addView(enablePassword)
        addView(password)
        addView(createBtn)
        addView(backBtn)
    }
}