package battleship.client.elements

import processing.core.PApplet
import processing.core.PConstants
import processing.core.PImage
import processing.core.PVector
import processing.event.KeyEvent
import processing.event.KeyEvent.RELEASE
import processing.event.KeyEvent.TYPE
import battleship.client.animations.BlinkAnimation
import battleship.client.interfaces.IViewGroup
import battleship.client.interfaces.IKeyListener
import battleship.client.resources.Colors
import battleship.client.resources.Images
import battleship.client.resources.Sounds

/**
 * clicken zum auswählen -> fängt dann texteingabe
 * enter zum "bestätigen"
 * pfeiltastetn links/rechts
 * del zum löschen
 * cursor (blinkt)
 * unterstrich
 * //TODO maximale länge
 * //TODO nur nummern
 */
class TextInput(
    position: PVector,
    private var hint: String?,
    var background_default: PImage? = Images.Input_Text,
    var background_hover: PImage? = Images.Input_Text_Hover
) : IViewGroup(position, background_default),
    IKeyListener {

    var input: String? = null
        set(value) {
            field = value
            if (value?.isNotEmpty() == true) {
                textView.alpha = 254f
            } else {
                textView.alpha = 150f
            }
            textView.text = if (value?.isNotEmpty() == true) value else hint ?: ""
            cursor.position.x = (size.x + textView.textWidth) / 2f + 5f
            onChange?.invoke(input)
        }

    var txtColor = Colors.C_DEFAULT
    var submit: ((input: String?) -> Unit)? = null
    var onChange: ((input: String?) -> Unit)? = null
    var isReadOnly = false
    var numbersOnly = false

    private var textView = Text(PVector(size.x / 2f, size.y / 2f), hint ?: "", Colors.C_WHITE).apply {
        drawMode = PConstants.CENTER
        alpha = 150f
    }

    override var isActive: Boolean = false
        get() = super.isActive
        set(value) {
            field = value
            cursor.isVisible = value
            onMouseOver(isMouseOver)
        }

    private var cursor = Rect(PVector(size.x / 2f, size.y / 2f), PVector(5f, 30f), Colors.C_WHITE).apply {
        drawMode = PConstants.CENTER
        isVisible = false
        animations.add(BlinkAnimation(this, 10f))
    }

    init {
        addView(textView)
        addView(cursor)
    }

    override fun onMouseOver(isMouseOver: Boolean) {
        if (!isReadOnly && isEnabled) {
            if (isActive) {
                this.background = background_hover
            } else {
                this.background = if (isMouseOver || isActive) {
                    if (isMouseOver) {
                        Sounds.Button_Click.play()
                    }
                    background_hover ?: background_default
                } else {
                    background_default
                }
            }
        }
        super.onMouseOver(isMouseOver)
    }

    override fun onClickedLeft() {
        if (!isReadOnly) {
            super.onClickedLeft()
        }
    }

    override fun onClickedRight() {
        if (!isReadOnly) {
            super.onClickedRight()
        }
    }

    override fun onKeyEvent(event: KeyEvent) {
        if (event.action == RELEASE) {
            when (event.key) {
                PApplet.BACKSPACE -> {
                    input?.also {
                        if (it.isNotBlank()) {
                            input = it.dropLast(1)
                        }
                    }
                }
                PApplet.DELETE -> {
                    input = null
                }
                PApplet.ENTER,
                PApplet.RETURN,
                PApplet.TAB -> {
                    submit?.invoke(input)
                }
                '.',
                ',',
                '_',
                ' ',
                in 'a'..'z',
                in 'A'..'Z' -> {
                    if(!numbersOnly) {
                        input?.also {
                            input += event.key
                        } ?: run {
                            input = event.key.toString()
                        }
                    }
                }
                in '0'..'9' -> {
                    input?.also {
                        input += event.key
                    } ?: run {
                        input = event.key.toString()
                    }
                }
            }
        }

    }
}