package battleship.client.screens

import battleship.client.animations.BackgroundAnimation
import battleship.client.elements.*
import processing.core.PVector
import battleship.client.program.Logic
import battleship.client.interfaces.IListElement
import battleship.client.interfaces.IScreen
import battleship.client.resources.Images
import battleship.server.data.*

/**
 * Spiel beitreten (LobbyListScreen)

Elemente:
- Suchfeld um namen einzugeben (name des spiels oder name des spielers), x um feld zu leeren
- Liste mit offnen Spielen (Enthält jeweils name des Spieles, Name des Spielers, Icon (Schloss) ob passwort notwendig)
- zurückbutton

Funktion:
- Spieler kann nach spiel suchen
- Spieler kann sich mit Spiel verbinden
 */
class LobbyListScreen : IScreen(background = Images.Background) {

    private var data = arrayListOf<LobbyData>()
    private var filteredData = data
    private var filterText: String? = null

    private val lobbyList = ListView(PVector(580f, 100f), PVector(1200f, 820f), listOf<LobbyData>()) { lobby ->
        object : IListElement<LobbyData>(PVector(0f, 0f), PVector(800f, 150f), lobby) {

            val title = Text(PVector(25f, 50f), "title")

            val enemy = Text(PVector(25f, 100f), "enemy")

            val pwd = Image(PVector(690f, 80f), Images.Lock)

            override fun onClickedLeft() {
                Logic.onRequestLobby(lobby)
            }

            init {
                this.addView(title)
                this.addView(enemy)
                this.addView(pwd)
            }

            override fun update(data: LobbyData) {
                this.data = data
                title.text = data.name
                enemy.text = data.createdPlayer
                pwd.isVisible = data.passwordEnabled
            }
        }
    }

    /**
     * zurückbutton
     */
    private val backBtn = Button(PVector(100f, 800f), null, Images.Back_Array, Images.Back_Array_Pressed).apply {
        clickedLeft = { Logic.userState = UserState.START_SCREEN }
    }

    private val search = TextInput(PVector(100f, 100f), "Search").apply {
        onChange = { text ->
            lobbyList.pageIndex = 0
            filterText = text
            updateLobbyList(data)
        }
    }

    override fun open() {
        addView(BackgroundAnimation)
        addView(search)
        addView(lobbyList)
        addView(backBtn)
        Logic.onReadLobbyList()
    }

    fun updateLobbyList(lobbyData: List<LobbyData>) {
        data = lobbyData as ArrayList<LobbyData>
        filterText?.also { text ->
            filteredData =
                data.filter { (it.name.contains(text) || it.createdPlayer.contains(text)) } as ArrayList<LobbyData>
        } ?: run {
            filteredData = data
        }
        lobbyList.pageIndex = 0
        lobbyList.updateData(filteredData)
    }

    fun lobbyJoinFailed(lobbyJoinResponse: LobbyJoinResponse) {
        TODO("Not yet implemented")
    }

}