package battleship.client.screens

import processing.core.PVector
import battleship.client.elements.Button
import battleship.client.elements.Text
import battleship.client.interfaces.IScreen
import battleship.client.program.Logic
import battleship.client.resources.Images
import battleship.server.data.UserState

class QueueScreen: LoadingScreen("Searching other Player") {

    /**
     * zurückbutton
     */
    private val backBtn = Button(PVector(100f, 800f), null, Images.Back_Array, Images.Back_Array_Pressed).apply {
        clickedLeft = { Logic.userState = UserState.START_SCREEN }
    }

    override fun open() {
        super.open()
        addView(backBtn)
    }
}