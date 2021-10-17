package battleship.client.screens

import battleship.client.animations.BackgroundAnimation
import battleship.client.animations.firework.Firework
import processing.core.PVector
import battleship.client.program.GameLogic
import battleship.client.elements.Button
import battleship.client.elements.Text
import battleship.client.interfaces.IScreen
import battleship.client.resources.Images
import battleship.server.data.UserGameState
import processing.core.PConstants

/**
 * Ende Bildschirm:

Elemente:
- text gewonnen/ verloren
- info geschossen/getroffen % quote, selbst noch vorhandene schiffe, dauer etc
- button nochmal spielen -> zurück zur lobby / hauptbildschirm -> zum hauptbildschirm/ beenden -> beendet spiel
- liste an chatnachrichten
- eingabefeld für chatnachrichten
- button um chatnachrichten abzuschicken

Funktion:
- Spieler sieht spielinformationen
- kann auswählen was er als nächstes tut
- chat

 */
class EndScreen : IScreen(background = Images.Background) {

    private var endText = Text(
        PVector(size.x / 2f, size.y / 2f), if (GameLogic.gameState == UserGameState.END_SCREEN_WON) {
            "You have won!"
        } else {
            "You have lost, unfortunately."
        }
    ).apply { drawMode = PConstants.CENTER }

    private var homeBtn = Button(PVector(100f, 800f), "Close", Images.Button, Images.Button_Pressed).also {
        it.clickedLeft = { GameLogic.finish() }
    }

    override fun open() {
        if (GameLogic.gameState == UserGameState.END_SCREEN_WON) {
            BackgroundAnimation.ship1.pImage = Images.Ship_Deko
        }else{
            BackgroundAnimation.ship1.pImage = Images.Ship_Deko_Broken
        }

        addView(BackgroundAnimation)
        addView(Firework())
        addView(endText)
        addView(homeBtn)
    }

}