package battleship.client.screens

import battleship.client.elements.*
import battleship.client.interfaces.IScreen
import battleship.client.program.Logic
import battleship.client.resources.Images
import battleship.server.data.*
import processing.core.PConstants
import processing.core.PVector

/**
 * Spielstart (LobbyScreen)

Elemente:
- 2 Zahlenfelder für eingabe, wie groß das Feld werden soll

- Liste mit Schiffen (Spalte mit schiffstyp und anzahl, anzahl als eingabefeld, editierbar)
- Eingabefeld für nummer (schifflänge)

- eingabefeld für anzahl
- button um schiff mit länge und anzahl hinzuzufügen

- Liste mit Spielern in der Lobby

- Button zum Ready drücken
- liste an chatnachrichten
- eingabefeld für chatnachrichten
- button um chatnachrichten abzuschicken
- Infobox, ob es möglich ist, alle schiffe zu setzten (sonst kann spiel nicht gestartet werden)

Funktion:
- Spielfeldgröße kann festgelegt werden (nur vom Spieler der das Spiel erstellt hat)
- Einstellungen können getätigt werden (anzahl Schiffe etc) (nur vom Spieler der das Spiel erstellt hat)
- Spieler kann gekickt werden (nur vom Spieler der das Spiel erstellt hat)
- Spieler kann angeben, dass er ready ist
- Chat
- spieler wird aus liste entfernt, wenn er die verbindung verliert, wenn der Spieler der das spiel erstellt hat
die verbindung verliert, ist der andere der lobbyleiter
 */
class LobbyScreen : IScreen(background = Images.Background) {

    private val lobbyName = Text(PVector(300f, 100f), Logic.currentLobby?.name ?: "").apply {
        drawMode = PConstants.CORNER
    }

    private val playerIcon = Image(PVector(50f, 20f), if (Logic.hasCreatedLobby) Images.Captain else Images.Pirate)

    private var secondPlayer = Text(PVector(300f, 200f), "").apply {
        drawMode = PConstants.CORNER
        isVisible = false
    }

    private var infoText = Text(PVector(300f, 350f), "Field Size")

    /**
     * 2 Zahlenfelder für eingabe, wie groß das Feld werden soll
     */
    private val width = TextInput(PVector(300f, 400f), "width", Images.Input_Number, Images.Input_Number_Hover).apply {
        isEnabled = Logic.hasCreatedLobby
        onChange = { text ->
            text?.also {
                Logic.currentLobby!!.gameSettings.fieldWidth = it.toIntOrNull() ?: 0
                Logic.updateGameSettings()
                readyBtn.isEnabled =
                    (Logic.currentLobby!!.gameSettings.fieldWidth != 0 && Logic.currentLobby!!.gameSettings.fieldHeight != 0)
            }
        }
    }

    private val cross = Image(PVector(432f, 400f), Images.Cross).apply {
        isEnabled = Logic.hasCreatedLobby
    }

    private val height =
        TextInput(PVector(500f, 400f), "height", Images.Input_Number, Images.Input_Number_Hover).apply {
            isEnabled = Logic.hasCreatedLobby
            onChange = { text ->
                text?.also {
                    Logic.currentLobby!!.gameSettings.fieldHeight = it.toIntOrNull() ?: 0
                    Logic.updateGameSettings()
                    readyBtn.isEnabled =
                        (Logic.currentLobby!!.gameSettings.fieldWidth != 0 && Logic.currentLobby!!.gameSettings.fieldHeight != 0)
                }
            }
        }

    /**
     * Checkbox erlauben schiffe berührung
     */
    private val touchingCbx = CheckBox(
        PVector(960f, 400f),
        Images.Checkbox,
        Images.Checkbox_Checked,
        Images.Checkbox,
        Images.Checkbox_Checked,
        textString = "Allow Ships to Touch each other"
    ).apply {
        isEnabled = Logic.hasCreatedLobby
        clickedLeft = {
            Logic.currentLobby!!.gameSettings.touching = value
            Logic.updateGameSettings()
        }
    }


    /**
     * ships
     */
    private val numShip2 =
        TextInput(PVector(300f, 700f), "count", Images.Input_Number, Images.Input_Number_Hover).apply {
            isEnabled = Logic.hasCreatedLobby
            onChange = { text ->
                text?.also { num ->
                    Logic.currentLobby!!.gameSettings.allowedShipSelection.find { it.length == 2 }?.num =
                        num.toIntOrNull() ?: 0
                    Logic.updateGameSettings()
                }
            }
        }
    private val numShip3 =
        TextInput(PVector(300f, 850f), "count", Images.Input_Number, Images.Input_Number_Hover).apply {
            isEnabled = Logic.hasCreatedLobby
            onChange = { text ->
                text?.also { num ->
                    Logic.currentLobby!!.gameSettings.allowedShipSelection.find { it.length == 3 }?.num =
                        num.toIntOrNull() ?: 0
                    Logic.updateGameSettings()
                }
            }
        }
    private val numShip4 =
        TextInput(PVector(960f, 700f), "count", Images.Input_Number, Images.Input_Number_Hover).apply {
            isEnabled = Logic.hasCreatedLobby
            onChange = { text ->
                text?.also { num ->
                    Logic.currentLobby!!.gameSettings.allowedShipSelection.find { it.length == 4 }?.num =
                        num.toIntOrNull() ?: 0
                    Logic.updateGameSettings()
                }
            }
        }
    private val numShip5 =
        TextInput(PVector(960f, 850f), "count", Images.Input_Number, Images.Input_Number_Hover).apply {
            isEnabled = Logic.hasCreatedLobby
            onChange = { text ->
                text?.also { num ->
                    Logic.currentLobby!!.gameSettings.allowedShipSelection.find { it.length == 5 }?.num =
                        num.toIntOrNull() ?: 0
                    Logic.updateGameSettings()
                }
            }
        }

    private val ship2 = Image(PVector(500f, 700f), Images.Ship_2_H)
    private val ship3 = Image(PVector(500f, 850f), Images.Ship_3_H)
    private val ship4 = Image(PVector(1160f, 700f), Images.Ship_4_H)
    private val ship5 = Image(PVector(1160f, 850f), Images.Ship_5_H)

    private val readyBtn = Button(PVector(1600f, 900f), "ready", Images.Button, Images.Button_Pressed).apply {
        clickedLeft = {
            Logic.toggleReady()
            text.text = if (Logic.userState == UserState.LOBBY_READY) "wait" else "ready"
        }
    }

    override fun open() {
        Logic.currentLobby?.also { lobby ->
            addView(lobbyName)
            addView(playerIcon)

            addView(readyBtn)
            addView(secondPlayer)

            addView(infoText)
            addView(width)
            addView(cross)
            addView(height)
            addView(touchingCbx)

            addView(ship2)
            addView(ship3)
            addView(ship4)
            addView(ship5)

            addView(numShip2)
            addView(numShip3)
            addView(numShip4)
            addView(numShip5)

            updateData(lobby)
            updateGameSettings()
        } ?: run {
            println("error in Lobby Screen, no lobby")
        }
    }

    fun updateData(lobbyData: LobbyData) {
        lobbyData.joinedPlayer?.also {
            secondPlayer.text =
                if (Logic.hasCreatedLobby) "Playing against $it" else "Playing against ${lobbyData.createdPlayer}"
            secondPlayer.isVisible = true
            readyBtn.isVisible = true
        } ?: run {
            secondPlayer.isVisible = false
            readyBtn.isVisible = false
        }
    }

    fun updateGameSettings() {
        width.input = Logic.currentLobby!!.gameSettings.fieldWidth.toString() //TODO nullsafe
        height.input = Logic.currentLobby!!.gameSettings.fieldHeight.toString() //TODO nullsafe
        touchingCbx.value = Logic.currentLobby!!.gameSettings.touching
        numShip2.input = (Logic.currentLobby!!.gameSettings.allowedShipSelection.find { it.length == 2 }?.num
            ?: 0).toString() //TODO nullsafe
        numShip3.input = (Logic.currentLobby!!.gameSettings.allowedShipSelection.find { it.length == 3 }?.num
            ?: 0).toString() //TODO nullsafe
        numShip4.input = (Logic.currentLobby!!.gameSettings.allowedShipSelection.find { it.length == 4 }?.num
            ?: 0).toString() //TODO nullsafe
        numShip5.input = (Logic.currentLobby!!.gameSettings.allowedShipSelection.find { it.length == 5 }?.num
            ?: 0).toString() //TODO nullsafe
        //reset ready when lobby was changed
        readyBtn.text.text = if (Logic.userState == UserState.LOBBY_READY) "wait" else "ready"
    }
}