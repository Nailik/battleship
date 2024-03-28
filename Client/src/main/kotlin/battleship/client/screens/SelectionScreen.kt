package battleship.client.screens

import battleship.client.elements.*
import battleship.client.gameviews.GameField
import battleship.client.gameviews.ShipView
import battleship.client.interfaces.IScreen
import battleship.client.program.GameLogic
import battleship.client.program.Logic
import battleship.client.resources.Images
import battleship.server.data.*
import processing.core.PVector

/**
 * Schiffsauswahl: (SelectionScreen)

Elemente:
- Schiffeversenken feld
- Liste mit Schiffen (Länge, anzahl gesetzt, noch zu setzten, click zum auswählen)
- Info box wie man schiffe dreht (Pfeiltasten)
- Button zum Ready drücken (Speichern)
- liste an chatnachrichten
- eingabefeld für chatnachrichten
- button um chatnachrichten abzuschicken

Funktion:
- Beide Spieler können ihre Schiffe auswählen (drehen, standort, welches schiff)
- Schiffsauswahl wird gespeichert
- Chat
- Wieder Verbinden Popup, wenn ein Spieler verbindung verliert
 */
class SelectionScreen : IScreen(background = Images.Battlefield) {

    private var floatingShip: ShipView? = null

    private var playerName = Text(PVector(280f, 120f), Logic.userSettings.userName)
    private val playerIcon = Image(PVector(50f, 20f), if (Logic.hasCreatedLobby) Images.Captain else Images.Pirate)

    /**
     * game field for selection
     */
    private val gameField = GameField(PVector(100f, 220f), 840f)

    /**
     * info text
     */
    private val infoText = Text(
        PVector(1050f, 180f), "Click on a ship and place it\nat the desired position.\n" +
                "Use right mouse button to rotate.\n" +
                "Ships can be moved by clicking on them."
    )

    /**
     * button to save
     */
    private val btnSaveSelection = Button(PVector(1600f, 900f), "Save", Images.Button, Images.Button_Pressed).apply {
        isEnabled = false
        clickedLeft = {
            GameLogic.sendShipSelection()
            isVisible = false
        }
    }

    /**
     * ships
     */
    private val numShip2 = Text(PVector(1160f, 450f), "")
    private val numShip3 = Text(PVector(1160f, 600f), "")
    private val numShip4 = Text(PVector(1160f, 750f), "")
    private val numShip5 = Text(PVector(1160f, 900f), "")

    private val ship2 = Image(PVector(1180f, 450f), Images.Ship_2_H).apply {
        clickedLeft = { updateFloatingShip(2) }
    }
    private val ship3 = Image(PVector(1180f, 600f), Images.Ship_3_H).apply {
        clickedLeft = { updateFloatingShip(3) }
    }
    private val ship4 = Image(PVector(1180f, 750f), Images.Ship_4_H).apply {
        clickedLeft = { updateFloatingShip(4) }
    }
    private val ship5 = Image(PVector(1180f, 900f), Images.Ship_5_H).apply {
        clickedLeft = { updateFloatingShip(5) }
    }

    init {
        gameField.clickedField = clickedField@{ clickedPos ->
            //save ship here
            floatingShip?.also { shipView ->
                //nochmal prüfen ob das schiff hier sein darf, sonst keine aktion
                if (GameLogic.isShipAllowed(clickedPos, shipView.ship)) {
                    shipView.ship.centerPosition = clickedPos
                    GameLogic.addToShipSelection(shipView.ship)
                    updateRemainingShips()

                    //deaktivieren, bis benutzer mit maus woanders war, sonst wird durch festlegen
                    //des shiffes auf das feld auch clicked left gleich wieder ausgeführt und das schiff wieder
                    //aufgenommen
                    shipView.mouseOver = {
                        if (!it && shipView.clickedLeft == null) {
                            //schiff nochmal klicken macht es bewegbar
                            shipView.clickedLeft = {
                                if (floatingShip == null) {
                                    GameLogic.removeFromShipSelection(shipView.ship)
                                    updateRemainingShips()
                                    shipView.mouseOver = null
                                    shipView.clickedLeft = null
                                    floatingShip = shipView
                                }
                            }
                        }
                    }

                    floatingShip = null
                }
            }
        }

        gameField.mouseOverField = hoverField@{ mousePos, fieldCenterPosition ->
            floatingShip?.also { shipView ->
                shipView.isVisible = true
                //shiff ausgrauen wenn es hier nicht sein darf (auserhalb des spielfeldes oder über einem anderen schiff)
                shipView.isEnabled = (GameLogic.isShipAllowed(mousePos, shipView.ship))
                shipView.updateFieldPosition(mousePos)
            }
        }

        if (GameLogic.gameSettings.touching) {
            infoText.text += "\nShips can touch each other."
        } else {
            infoText.text += "\nShips need space in between."
        }
    }


    override fun open() {
        addView(playerIcon)
        addView(playerName)

        addView(gameField)
        addView(infoText)
        addView(btnSaveSelection)

        addView(ship2)
        addView(ship3)
        addView(ship4)
        addView(ship5)

        addView(numShip2)
        addView(numShip3)
        addView(numShip4)
        addView(numShip5)

        clickedRight = {
            floatingShip?.also { shipView ->
                shipView.updateOrientation(
                    if (shipView.ship.orientation == Orientation.HORIZONTAL) {
                        Orientation.VERTICAL
                    } else {
                        Orientation.HORIZONTAL
                    }
                )
                shipView.isEnabled = (GameLogic.isShipAllowed(shipView.ship.centerPosition, shipView.ship))
            }
        }

        updateRemainingShips()
        GameLogic.checkShipSelectionFinished()
    }

    private fun updateRemainingShips() {
        numShip2.text = (GameLogic.remainingShips.find { it.length == 2 }?.num ?: 0).toString()
        numShip3.text = (GameLogic.remainingShips.find { it.length == 3 }?.num ?: 0).toString()
        numShip4.text = (GameLogic.remainingShips.find { it.length == 4 }?.num ?: 0).toString()
        numShip5.text = (GameLogic.remainingShips.find { it.length == 5 }?.num ?: 0).toString()
    }

    private fun updateFloatingShip(size: Int) {
        if ((GameLogic.remainingShips.find { it.length == size }?.num ?: 0) > 0) {
            floatingShip?.also {
                it.updateSize(size)
            } ?: run {
                floatingShip = gameField.createShipView(size).apply {
                    isVisible = false
                }
            }
        }
    }

    fun finishedSelection() {
        btnSaveSelection.isEnabled = true
    }
}