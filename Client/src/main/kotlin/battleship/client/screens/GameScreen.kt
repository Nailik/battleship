package battleship.client.screens

import battleship.client.elements.Image
import battleship.client.elements.Text
import battleship.client.gameviews.GameField
import battleship.client.interfaces.IScreen
import battleship.client.program.GameLogic
import battleship.client.program.Logic
import battleship.client.resources.Images
import battleship.server.data.ShootResponse
import battleship.server.data.UserGameState
import processing.core.PVector

/**
 * Spiel: (GameScreen)

Elemente:
- Info die anzeigt ob der spieler an der Reihe ist
- info wie oft schon geschossen wurde
- info die nochmal anzeigt, welche schiffe es alles gibt
- eigenes spielfeld mit beschuss
- gegnerisches spielfeld mit beschuss
- countdown (wie lange man selbst oder der gegner noch überlegen kann)
- aufgeben (button zum aufgeben)
- ladepopup wenn ein spieler die verbindung verloren hat
- liste an chatnachrichten
- eingabefeld für chatnachrichten
- button um chatnachrichten abzuschicken

Funktion:
- es wird zufällig gewählt, welcher spieler anfängt
- Ein spieler hat für den Beschuss nur eine gewisse zeit lang zeit - sonst darf der nächste spieler, nach 3 mal hat man verloren
- wenn getroffen nochmal, sonst der andere spieler am zug
- es wird gezeigt, wenn das spiel zuende ist
- Chat
- Wieder Verbinden Popup, wenn ein Spieler verbindung verliert
- Ende screen wenn ein Spieler gewonnen hat
 */
class GameScreen : IScreen(background = Images.Battlefield) {

    private var statusText = Text(PVector(1400f, 1050f), "")

    private val playerIcon =
        Image(PVector(50f, 20f), if (Logic.hasCreatedLobby) Images.Captain else Images.Pirate).apply {
            isEnabled = false
        }

    private var playerName = Text(
        PVector(280f, 120f),
        if (Logic.hasCreatedLobby) Logic.currentLobby!!.createdPlayer else Logic.currentLobby!!.joinedPlayer!!
    )

    private var playerStatus = Text(PVector(480f, 120f), "0 | 0")

    private val enemyIcon =
        Image(PVector(1000f, 20f), if (Logic.hasCreatedLobby) Images.Pirate else Images.Captain).apply {
            isEnabled = false
        }

    private var enemyPlayerName = Text(
        PVector(1230f, 120f),
        if (!Logic.hasCreatedLobby) Logic.currentLobby!!.createdPlayer else Logic.currentLobby!!.joinedPlayer!!
    )

    private var enemyPlayerStatus = Text(PVector(1430f, 120f), "0 | 0")

    private val shipField = GameField(PVector(50f, 250f), 750f).apply {
        GameLogic.shipSelection.forEach {
            createShipView(it)
        }
    }

    private val enemyField = GameField(PVector(1000f, 250f), 750f).apply {
        clickedField = { pos ->
            GameLogic.shoot(pos)
        }
    }

    override fun open() {
        addView(playerIcon)
        addView(playerName)
        addView(playerStatus)
        addView(enemyPlayerStatus)
        addView(enemyIcon)
        addView(enemyPlayerName)
        addView(statusText)
        addView(enemyField)
        addView(shipField)
        updateState()
    }

    fun updateState() {
        playerIcon.isEnabled = GameLogic.gameState == UserGameState.SHOOTING
        enemyIcon.isEnabled = GameLogic.gameState == UserGameState.SPECTATING

        statusText.text = if (GameLogic.gameState == UserGameState.SHOOTING) {
            "Your turn."
        } else {
            "Your opponent's turn."
        }
    }

    fun updateOwnField(shootResponse: ShootResponse) {
        enemyPlayerStatus.text = "${GameLogic.enemyPlayerRate.first} | ${GameLogic.enemyPlayerRate.second}"
        shipField.showShootResponse(shootResponse)
    }

    fun updateEnemyField(shootResponse: ShootResponse) {
        playerStatus.text = "${GameLogic.playerRate.first} | ${GameLogic.playerRate.second}"
        enemyField.showShootResponse(shootResponse)
    }
}