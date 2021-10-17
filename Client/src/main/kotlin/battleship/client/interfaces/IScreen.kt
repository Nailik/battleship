package battleship.client.interfaces

import battleship.client.elements.SettingsButton
import battleship.client.elements.TextInput
import battleship.client.popups.IPopUp
import battleship.client.program.Sketch
import battleship.client.program.plus
import battleship.client.program.times
import processing.core.PImage
import processing.core.PVector
import processing.event.KeyEvent

abstract class IScreen(
    position: PVector = PVector(0f, 0f),
    background: PImage? = null,
    size: PVector? = null,
    enableSoundSettings: Boolean = true
) :
    IViewGroup(position, background, size) {

    abstract fun open()

    init {
        if (enableSoundSettings) {
            addView(SettingsButton)
        }
        backgroundImage?.size = PVector(Sketch.iniWidth, Sketch.iniHeight) + (Sketch.currentOffset * 2f)
        backgroundImage?.disableOffset = true
    }

    open fun handleKeyEvent(event: KeyEvent) {
        (activeView as? TextInput)?.onKeyEvent(event)
    }

    private var currentPopUp: IPopUp? = null

    fun setPopUp(popup: IPopUp?) {
        popup?.also {
            addView(it)
            it.open()
        } ?: run {
            currentPopUp?.also {
                it.close()
                removeView(it)
            }
        }
        this.currentPopUp = popup
    }

    override fun update() {
        backgroundImage?.size = PVector(Sketch.iniWidth, Sketch.iniHeight) + (Sketch.currentOffset * 2f)
        backgroundImage?.disableOffset = true

        currentPopUp?.also {
            it.update()
        } ?: run {
            super.update()
        }
    }

    override fun close() {
        currentPopUp?.close()
        super.close()
    }
}