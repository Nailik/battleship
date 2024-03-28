package battleship.client.screens


/**
 * Ladebildschirm: (LoadingScreen)

Elemente:
- Ladeanimation

Funktion:
- (verbindet zu server)


Ladebildschirm: (LoadingScreen)

Elemente:
- Lustiger Text
- Ladeanimation

Funktion:
- Beide Spieler verbinden sich zum spiel oder Schiffe werden gespeichert
 */

import battleship.client.animations.RotationAnimation
import battleship.client.elements.Image
import battleship.client.elements.Text
import battleship.client.interfaces.IScreen
import battleship.client.resources.Alpha
import battleship.client.resources.Images
import processing.core.PConstants
import processing.core.PVector

open class LoadingScreen(textString: String = "") :
    IScreen(background = Images.Background, enableSoundSettings = false) {

    private val text = Text(PVector(size.x / 2f, (size.y / 2f) - 150f), textString)
        .apply { drawMode = PConstants.CENTER }

    //bild erstellen
    private var image = Image(PVector(size.x / 2f, size.y / 2f), Images.Loading_Wave).apply {
        //animation hinzufügen
        animations.add(RotationAnimation(this, 5f))
        drawMode = PConstants.CENTER
    }

    init {
        backgroundImage?.alpha = Alpha.A_BACKGROUND.alpha
    }

    override fun open() {
        addView(image)
        addView(text)
    }

}