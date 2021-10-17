package battleship.client.gameviews

import processing.core.PVector
import battleship.client.program.Sketch
import battleship.client.interfaces.IClickable
import battleship.client.resources.Alpha
import battleship.client.resources.Colors
import battleship.client.resources.Images

class Field(position: PVector, size: Float, var boolean: Boolean? = null) : IClickable(position, null, PVector(size, size)){

    override fun draw(position: PVector) {
     /*   Sketch.noFill()

        when(boolean){
            //shot result positive
            true -> Sketch.image(Images.Shot_Hit, position.x, position.y, size.x, size.y)
            //shot result negative
            false -> Sketch.image(Images.Shot_Missed, position.x, position.y, size.x, size.y)
            //nothing
            null -> Sketch.fill(Colors.C_WHITE.red(), Colors.C_WHITE.green(), Colors.C_WHITE.blue(), if(isMouseOver){ Alpha.A_MOUSE_OVER.alpha } else 0f)
        }
*/
        Sketch.fill(Colors.C_WHITE.red(), Colors.C_WHITE.green(), Colors.C_WHITE.blue(), if(isMouseOver){ Alpha.A_MOUSE_OVER.alpha } else 0f)
        Sketch.stroke(Colors.C_WHITE.red(), Colors.C_WHITE.green(), Colors.C_WHITE.blue())
        Sketch.rect(position.x, position.y, size.x , size.y)
    }

}