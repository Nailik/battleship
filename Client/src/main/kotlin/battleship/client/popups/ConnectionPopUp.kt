package battleship.client.popups

import battleship.client.elements.Button
import battleship.client.elements.Text
import battleship.client.program.Logic
import battleship.client.resources.Images
import battleship.server.data.UserState
import processing.core.PConstants
import processing.core.PVector

/**
 * popup keine verbindung möglich
 *
 * erneut versuchen oder in server settings gehen
 */
class ConnectionPopUp : IPopUp("Unable to connect") {

    private val textView =
        Text(PVector(size.x / 2, 475f),
            "You can try to reconnect\nor change the server\nin the settings."
        ).apply { drawMode = PConstants.CENTER }

    private val server = Button(PVector(700f, 630f), "Settings", Images.Button, Images.Button_Pressed).apply {
        clickedLeft = {
            this@ConnectionPopUp.close()
            Logic.userState = UserState.SERVER_SETTINGS
        }
    }

    private val retry = Button(PVector(1050f, 630f), "Retry", Images.Button, Images.Button_Pressed).apply {
        clickedLeft = {
            this@ConnectionPopUp.close()
            Logic.tryConnect()
        }
    }

    override fun open() {
        addView(retry)
        addView(server)
        addView(textView)
    }

}