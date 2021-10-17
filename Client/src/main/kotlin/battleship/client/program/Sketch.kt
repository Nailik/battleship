package battleship.client.program

import battleship.client.interfaces.IScreen
import battleship.client.resources.Music
import battleship.server.data.UserState
import com.jogamp.opengl.GL.GL_LINE_STRIP
import mu.KotlinLogging
import processing.core.PApplet
import processing.core.PVector
import processing.event.KeyEvent
import processing.event.MouseEvent
import processing.event.MouseEvent.PRESS
import processing.event.MouseEvent.RELEASE
import processing.opengl.PGraphicsOpenGL


/**
 * Main Game, shows Game Screens and knows if mouse is pressed
 */
private val logger = KotlinLogging.logger {}

object Sketch : PApplet() {

    const val iniWidth = 1920f
    const val iniHeight = 1080f

    private var startWidth = 1f
    private var startHeight = 1f

    var scaleFactor = 1f
    var currentOffset = PVector(0f, 0f)
        private set

    var oldScreen: IScreen? = null

    var isMouseLeftPressed = false
    var isMouseRightPressed = false

    var oldMousePosition = PVector(mouseX.toFloat(), mouseY.toFloat())
    var mousePosition = PVector(mouseX.toFloat(), mouseY.toFloat())

    init {
        this.runSketch()
    }

    override fun settings() {
        try {
            println("path: ${Sketch.sketchPath()}")

            startWidth = displayWidth * 0.7f
            startHeight = startWidth / 16f * 9f

            if (startHeight >= displayHeight) {
                //in 16/9 the height ist too big
                startHeight = displayHeight * 0.6f
                startWidth = startHeight / 9f * 16f
            }
            //size is depending on display
            size(startWidth.toInt(), startHeight.toInt(), P2D)
            //  noSmooth()
            super.settings()
        } catch (e: Exception) {
            logger.error { "Exception $e" }
        } catch (e: Error) {
            logger.error { "Error $e" }
        } catch (e: Throwable) {
            logger.error { "Throwable $e" }
        }
    }

    override fun setup() {
        surface.setResizable(true)
        try {
            frameRate(60F)
            //TODO FONT aus datei laden
            val font = createFontFromRessourceTTF("/Minecraft.ttf", 25f) // arial, size 96
            textFont(font)
            //  textSize(200f)
        } catch (e: Exception) {
            logger.error { "Exception $e" }
        } catch (e: Error) {
            logger.error { "Error $e" }
        } catch (e: Throwable) {
            logger.error { "Throwable $e" }
        }
        //"no smooth" functionality for p2d
        //GL_LINE_STRIP
        //GL_LINE_LOOP
        surface.setResizable(true)
        (g as PGraphicsOpenGL).textureSampling(GL_LINE_STRIP)
    }


    override fun draw() {
        Music.check()
        background(200)

        calculateScaleFactorAndOffset()

        mousePosition = PVector(mouseX.toFloat(), mouseY.toFloat())

        if (Logic.userState != UserState.IN_GAME) {
            Logic.currentScreen.position = currentOffset
            Logic.currentScreen.update()
            Logic.currentScreen.draw()
        } else {
            GameLogic.currentScreen.position = currentOffset
            GameLogic.currentScreen.update()
            GameLogic.currentScreen.draw()
        }

        oldScreen?.close()
        oldScreen = null
        oldMousePosition = mousePosition
    }

    private fun calculateScaleFactorAndOffset() {
        scaleFactor = width.toFloat() / iniWidth

        (height.toFloat() / iniHeight).also {
            if (it < scaleFactor) {
                scaleFactor = it
            }
        }

        currentOffset = PVector(0f, 0f)

        if (scaleFactor * iniWidth < width) {
            currentOffset.x = ((width - scaleFactor * iniWidth) / 2f) / scaleFactor
        } else if (scaleFactor * iniWidth > width) {
            println("error width too wide")
        }

        if (scaleFactor * iniHeight < height) {
            currentOffset.y = ((height - scaleFactor * iniHeight) / 2f) / scaleFactor
        } else if (scaleFactor * iniHeight > height) {
            println("error height too high")
        }

        scale(scaleFactor, scaleFactor)
    }


    override fun handleMouseEvent(event: MouseEvent?) {
        super.handleMouseEvent(event)

        event?.also {
            if (it.action == PRESS) {
                if (it.button == LEFT) {
                    isMouseLeftPressed = true
                }
                if (it.button == RIGHT) {
                    isMouseRightPressed = true
                }
            } else if (it.action == RELEASE) {
                if (it.button == LEFT) {
                    isMouseLeftPressed = false
                }
                if (it.button == RIGHT) {
                    isMouseRightPressed = false
                }
            }
        }
    }

    override fun handleKeyEvent(event: KeyEvent) {
        if (Logic.userState != UserState.IN_GAME) {
            Logic.currentPopUp?.also {
                it.handleKeyEvent(event)
            } ?: run {
                Logic.currentScreen.handleKeyEvent(event)
            }
        } else {
            GameLogic.currentScreen.handleKeyEvent(event)
        }
    }
}

fun main(args: Array<String>) {
    try {
        Sketch
    } catch (e: Exception) {
        logger.error { "Exception $e" }
    } catch (e: Error) {
        logger.error { "Error $e" }
    } catch (e: Throwable) {
        logger.error { "Throwable $e" }
    }
}
