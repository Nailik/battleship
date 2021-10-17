package battleship.client.animations

import battleship.client.elements.Image
import battleship.client.interfaces.IViewGroup
import battleship.client.resources.Images
import processing.core.PVector

//clouds in background
object BackgroundAnimation : IViewGroup(PVector(0f,0f)){

    /**
     * animation
     */
    private var cloud1 = Image(PVector(100f, 200f), Images.Cloud_1).apply {
        //animation hinzufügen
        animations.add(MoveAnimation(this, 0.1f))
    }
    private var cloud2 = Image(PVector(540f, 100f), Images.Cloud_2).apply {
        //animation hinzufügen
        animations.add(MoveAnimation(this, 0.2f))
    }

    private var cloud3 = Image(PVector(950f, 250f), Images.Cloud_3).apply {
        //animation hinzufügen
        animations.add(MoveAnimation(this, 0.15f))
    }

    private var cloud4 = Image(PVector(1450f, 100f), Images.Cloud_4).apply {
        //animation hinzufügen
        animations.add(MoveAnimation(this, 0.18f))
    }

    private var cloud5 = Image(PVector(-250f, 100f), Images.Cloud_4).apply {
        //animation hinzufügen
        animations.add(MoveAnimation(this, 0.18f))
    }

    var counter = 0

    var ship1 = Image(PVector(600f, 500f), Images.Ship_Deko).apply {
        //animation hinzufügen
        animations.add(MoveAnimation(this, 0.30f))
        clickedLeft = {
            counter ++
            if(counter >= 10){
                this.pImage = Images.Ship_Deko_Broken
                counter = 0
            }
        }
    }

    init{
        addView(cloud1)
        addView(cloud2)
        addView(cloud3)
        addView(cloud4)
        addView(cloud5)
        addView(ship1)
    }

}