package battleship.client.popups

import battleship.client.elements.Text
import battleship.client.interfaces.IScreen
import battleship.client.program.GameLogic
import battleship.client.program.Logic
import battleship.client.resources.Images
import battleship.client.resources.Sizes
import battleship.server.data.UserState
import processing.core.PConstants
import processing.core.PVector

abstract class IPopUp(title: String) : IScreen(background = Images.Wooden_Panel, enableSoundSettings = false) {

    private val titleView = Text(PVector(size.x / 2, 390f), title).apply {
        drawMode = PConstants.CENTER
        textSize = Sizes.SIZE_TXT_BIG
    }

    init {
        addView(titleView)
    }

    override fun close() {
        Logic.closeCurrentPopUp()
        GameLogic.closeCurrentPopUp()
    }
}