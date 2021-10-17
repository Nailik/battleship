package battleship.client.interfaces

import processing.event.KeyEvent

interface IKeyListener {
    fun onKeyEvent(event: KeyEvent)
}