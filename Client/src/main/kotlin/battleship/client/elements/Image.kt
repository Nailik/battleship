package battleship.client.elements

import processing.core.PImage
import processing.core.PVector
import battleship.client.interfaces.IClickable
import battleship.client.program.Sketch
import battleship.client.resources.Sizes

open class Image(
    position: PVector, var pImage: PImage,
    size: PVector = PVector(
        pImage.width.toFloat() * Sizes.SIZE_PIXEL.size,
        pImage.height.toFloat() * Sizes.SIZE_PIXEL.size
    )
) :
    IClickable(position, null, size) {

    override fun draw(position: PVector) {
        Sketch.image(pImage, position.x, position.y, size.x, size.y)
    }

}