package battleship.client.animations

import battleship.client.interfaces.IAnimation
import battleship.client.interfaces.IView
import battleship.client.program.Sketch

class MoveAnimation(view: IView, private var speed: Float) : IAnimation(view) {

    override fun animate(){
        //bewegen view.position ändern
        view.position.x += speed
        if(view.position.x + 15f > Sketch.iniWidth){
            view.position.x = - view.size.x - 15f
        }

    }


}