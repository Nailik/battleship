package battleship.client.interfaces

import processing.core.PVector
import battleship.client.program.Sketch

abstract class IListElement<T>(position: PVector, size: PVector, var data: T) : IViewGroup(position, size = size) {

    //offset for position in list
    fun setPositionOffset(index: Int, space: Float){
        position.y += (size.y + space) * index
    }

    abstract fun update(data: T)

    override fun draw(position: PVector) {
        if(isMouseOver){
            Sketch.fill(240, 50f)
        }else{
            Sketch.fill(240, 20f)
        }
        Sketch.stroke(0)
        Sketch.rect(position.x, position.y, size.x, size.y, 5f)
        Sketch.noStroke()

        super.draw(position)
    }

}