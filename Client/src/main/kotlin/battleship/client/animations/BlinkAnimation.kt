package battleship.client.animations

import battleship.client.interfaces.IAnimation
import battleship.client.interfaces.IView

class BlinkAnimation(view: IView, private var speed: Float) : IAnimation(view) {

    var initialAlpha = view.alpha
    var down = true

    override fun animate() {

        down = if (view.alpha == initialAlpha) {
            true
        } else if (view.alpha == 0f) {
            false
        } else down

        if (down) {
            view.alpha -= speed
        } else {
            view.alpha += speed
        }

        if (view.alpha > initialAlpha) {
            view.alpha = initialAlpha
        } else if (view.alpha < 0f) {
            view.alpha = 0f
        }
    }

}