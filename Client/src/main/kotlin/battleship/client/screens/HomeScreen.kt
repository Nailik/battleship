package battleship.client.screens

import battleship.client.animations.BackgroundAnimation
import battleship.client.elements.Button
import battleship.client.elements.TextInput
import battleship.client.interfaces.IScreen
import battleship.client.program.ClientApi
import battleship.client.program.DataPacket
import battleship.client.program.Logic
import battleship.client.resources.Images
import battleship.server.data.DataType
import battleship.server.data.UserState
import processing.core.PVector

/**
 * Startbildschirm: (StartScreen)

Elemente:
- Spiel erstellen
- Spiel beitreten
- schnellstart
- button um settings zu öffnen

Funktion:
- Spieler kann spiel starten/beitreten
- Spieler kann zu den eintstellungen gehen
 */
class HomeScreen : IScreen(background = Images.Background) {

    private val userNameText: TextInput = TextInput(PVector(300f, 50f), "Name", Images.Input_Text).apply {
        input = Logic.userSettings.userName
        onChange = {
            userNameSave.isVisible = true
        }
    }


    private val userNameSave = Button(PVector(700f, 50f), "Save", Images.Button, Images.Button_Pressed).apply {
        isVisible = false
        clickedLeft = {
            userNameText.input?.also {
                Logic.userSettings.userName = it
                Logic.saveSettings()
                ClientApi.sendData(DataPacket(DataType.CHANGED_NAME, it))
                isVisible = false
            }
        }
    }


    /**
     * Spiel erstellen
     */
    private val createBtn = Button(PVector(300f, 200f), "Create", Images.Button, Images.Button_Pressed).apply {
        clickedLeft = { Logic.userState = UserState.CREATE_GAME }
    }

    /**
     * Spiel beitreten
     */
    private val joinBtn = Button(PVector(300f, 400f), "Connect", Images.Button, Images.Button_Pressed).apply {
        clickedLeft = { Logic.userState = UserState.LOBBYLIST }
    }

    /**
     * Queue beitreten
     */
    private val joinQueueBtn = Button(PVector(300f, 600f), "Queue", Images.Button, Images.Button_Pressed).apply {
        clickedLeft = { Logic.userState = UserState.QUEUE }
    }

    /**
     * button um settings zu öffnen
     */
    private val settingsBtn = Button(PVector(300f, 800f), "Server", Images.Button, Images.Button_Pressed).apply {
        clickedLeft = { Logic.userState = UserState.SERVER_SETTINGS }
    }

    override fun open() {
        addView(BackgroundAnimation)
        addView(userNameText)
        addView(userNameSave)
        addView(createBtn)
        addView(joinBtn)
        addView(joinQueueBtn)
        addView(settingsBtn)
    }
}