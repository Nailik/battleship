package battleship.client.elements

import battleship.client.animations.RotationAnimation
import battleship.client.interfaces.IViewGroup
import battleship.client.program.GameLogic
import battleship.client.program.Logic
import battleship.client.resources.Images
import battleship.server.data.UserState
import processing.core.PConstants
import processing.core.PVector

//separate klasse, damit drehung erhalten bleibt
object SettingsButton : IViewGroup(PVector(1850f, 70f)) {

    val image = Image(PVector(0f, 0f), Images.Settings).apply {
        drawMode = PConstants.CENTER
        clickedLeft = {
            if (Logic.userState != UserState.IN_GAME) {
                Logic.showSettings()
            } else {
                GameLogic.showSettings()
            }
        }
        mouseOver = {
            if (it) {
                if (animations.isEmpty()) {
                    animations.add(RotationAnimation(this, 0.5f))
                }
            } else {
                animations.clear()
            }
        }
    }

    init {
        addView(image)
    }

}