package battleship.client.animations

import battleship.client.program.Sketch
import battleship.client.interfaces.IAnimation
import battleship.client.interfaces.IView

class RotationAnimation(view: IView, private var speed: Float) : IAnimation(view) {

    override fun animate(){
         view.rotation += speed * Sketch.scaleFactor
    }

}