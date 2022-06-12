package battleship.client.screens

import battleship.client.animations.BackgroundAnimation
import battleship.client.elements.*
import battleship.client.interfaces.IListElement
import battleship.client.interfaces.IScreen
import battleship.client.program.ClientApi
import battleship.client.program.Logic
import battleship.client.resources.Images
import battleship.server.data.UserState
import battleship.server.program.DefaultGamePort
import battleship.server.webserver.ServerApi
import processing.core.PVector

/**
 * custom server
 * lokaler server
 *
 * server selbst hosten
 *
 * zurückbutton falls verbunden, sonst abbrechen button und ladescreen?
 */

class ServerScreen : IScreen(background = Images.Background) {

    private val textCurrentServer = Text(
        PVector(300f, 50f),
        if (ClientApi.isConnected()) "Connected to ${ClientApi.host}:${ClientApi.port}" else "Not Connected"
    )

    private val textServer = Text(PVector(300f, 150f), "Setup custom Server Connection")

    private val customServerIP: TextInput =
        TextInput(PVector(300f, 180f), "IP", Images.Input_Text, Images.Input_Text_Hover).apply {
            onChange = { text ->
                customServerConnect.isEnabled = text != null && customServerPort.input != null
            }
        }

    private val customServerPort: TextInput =
        TextInput(PVector(700f, 180f), "Port", Images.Input_Number, Images.Input_Number_Hover).apply {
            input = DefaultGamePort.toString()
            numbersOnly = true
            onChange = { text ->
                customServerConnect.isEnabled = text != null && customServerIP.input != null
            }
        }

    private val https = CheckBox(
        PVector(865f, 182f),
        Images.Checkbox,
        Images.Checkbox_Checked,
        Images.Checkbox,
        Images.Checkbox_Checked,
        textString = "HTTPS",
    ).apply {
        value = true
    }

    private val customServerConnect =
        Button(PVector(1100f, 172f), "Connect", Images.Button, Images.Button_Pressed).apply {
            clickedLeft = {
                customServerIP.input?.also { ip ->
                    customServerPort.input?.also { port ->
                        Logic.connectFromSettings(ip, Integer.valueOf(port), https.value)
                    }
                }
            }
        }

    private val textLocal = Text(
        PVector(300f, 400f),
        if (!ServerApi.isServerRunning()) "Setup local server, you will automatically connect" else "Local server is running, when stopping you will be connected to default server"
    )

    private val serverPort: TextInput =
        TextInput(PVector(300f, 430f), "Port", Images.Input_Number, Images.Input_Number_Hover).apply {
            input =
                if (ServerApi.isServerRunning()) ServerApi.serverSettings.port.toString() else DefaultGamePort.toString()
            numbersOnly = true
            onChange = {
                startServer.isEnabled = it != null
            }
        }

    private val startServer = Button(
        PVector(700f, 422f), if (!ServerApi.isServerRunning()) "Start" else "Stop", Images.Button, Images.Button_Pressed
    ).apply {
        clickedLeft = {
            serverPort.input?.also {
                if (!ServerApi.isServerRunning()) {
                    Logic.startLocalServer(it)
                } else {
                    Logic.stopLocalServer()
                }
            }
        }
    }

    private val text = Text(PVector(300f, 600f), "List of Local Servers")

    private val localServerList =
        ListView(PVector(300f, 650f), PVector(800f, 400f), listOf<Pair<String, Int>>(), 3) { lobby ->
            object : IListElement<Pair<String, Int>>(PVector(0f, 0f), PVector(800f, 80f), lobby) {

                val title = Text(PVector(25f, 50f), "title")

                override fun onClickedLeft() {
                    //connect to local server
                    Logic.connectFromSettings(data.first, data.second, false)
                }

                init {
                    this.addView(title)
                }

                override fun update(data: Pair<String, Int>) {
                    this.data = data
                    title.text = "${data.first}:${data.second}"
                }
            }
        }


    private val backBtn = Button(PVector(100f, 800f), null, Images.Back_Array, Images.Back_Array_Pressed).apply {
        clickedLeft = { Logic.userState = UserState.START_SCREEN }
    }

    override fun open() {
        addView(BackgroundAnimation)
        addView(textCurrentServer)
        addView(textServer)
        addView(customServerIP)
        addView(customServerPort)
        addView(https)
        addView(customServerConnect)
        addView(textLocal)
        addView(serverPort)
        addView(startServer)
        addView(text)
        addView(localServerList)
        if (ClientApi.isConnected()) {
            addView(backBtn)
        }
        ClientApi.searchLocalServer { list ->
            localServerList.updateData(list)
        }
    }

    override fun close() {
        ClientApi.stopSearchLocalServer()
        super.close()
    }
}