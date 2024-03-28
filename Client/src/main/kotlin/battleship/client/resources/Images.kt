package battleship.client.resources

import battleship.client.program.Sketch
import mu.KotlinLogging
import processing.core.PGraphics
import processing.core.PImage
import java.awt.Graphics2D
import javax.imageio.ImageIO

private val logger = KotlinLogging.logger {}

object Images {

    val Ship_Deko = getImage("/images/ship_deko.png")
    val Ship_Deko_Broken = getImage("/images/ship_deko_broken.png")
    val Cloud_1 = getImage("/images/cloud_one.png")
    val Cloud_2 = getImage("/images/cloud_two.png")
    val Cloud_3 = getImage("/images/cloud_three.png")
    val Cloud_4 = getImage("/images/cloud_four.png")
    val Loading_Wave = getImage("/images/loading_wave.png")
    val Background = getImage("/images/background.png")
    val Button = getImage("/images/button.png")
    val Button_Pressed = getImage("/images/button_pressed.png")
    val Battlefield = getImage("/images/battlefield.png")
    val Checkbox = getImage("/images/checkbox.png")
    val Checkbox_Checked = getImage("/images/checkbox_checked.png")
    val Music_On = getImage("/images/music_on.png")
    val Music_On_Pressed = getImage("/images/music_on_pressed.png")
    val Music_Off = getImage("/images/music_off.png")
    val Music_Off_Pressed = getImage("/images/music_off_pressed.png")
    val Sound_On = getImage("/images/sound_on.png")
    val Sound_On_Pressed = getImage("/images/sound_on_pressed.png")
    val Sound_Off = getImage("/images/sound_off.png")
    val Sound_Off_Pressed = getImage("/images/sound_off_pressed.png")
    val Back_Array = getImage("/images/back_arrow.png")
    val Back_Array_Pressed = getImage("/images/back_arrow_pressed.png")
    val Captain = getImage("/images/person_captain.png")
    val Pirate = getImage("/images/person_pirate.png")
    val Lock = getImage("/images/lock.png")
    val Input_Text = getImage("/images/input_text.png")
    val Input_Text_Hover = getImage("/images/input_text_hover.png")
    val Input_Number = getImage("/images/input_number.png")
    val Input_Number_Hover = getImage("/images/input_number_hover.png")
    val Wooden_Panel = getImage("/images/wooden_panel.png")
    val SlideBar = getImage("/images/slide_bar.png")
    val SlideButton = getImage("/images/slide_button.png")
    val SlideButton_Pressed = getImage("/images/slide_button_pressed.png")
    val Settings = getImage("/images/settings.png")

    val Ship_2_H = getImage("/images/ship_two.png")
    val Ship_3_H = getImage("/images/ship_three.png")
    val Ship_4_H = getImage("/images/ship_four.png")
    val Ship_5_H = getImage("/images/ship_five.png")
    val Ship_2_V = getImage("/images/ship_two_v.png")
    val Ship_3_V = getImage("/images/ship_three_v.png")
    val Ship_4_V = getImage("/images/ship_four_v.png")
    val Ship_5_V = getImage("/images/ship_five_v.png")

    val Cross = getImage("/images/x.png")
    val Shot_Missed = getImage("/images/shot_missed.png")
    val Shot_Hit = getImage("/images/shot_hit.png")

    private fun getImage(path: String): PImage {
        try {
            val inputStream = Sketch::class.java.getResourceAsStream(path)
            if (inputStream == null) {
                logger.error { "could not read image from $path" }
            }
            val bi = ImageIO.read(inputStream)
            val g: PGraphics = Sketch.createGraphics(bi.width, bi.height)
            g.beginDraw()
            val g2d = g.native as Graphics2D
            g2d.drawImage(bi, 0, 0, bi.width, bi.height, null)
            g.endDraw()

            return g.copy()
        } catch (e: Exception) {
            logger.error { "Exception $e" }
        } catch (e: Error) {
            logger.error { "Error $e" }
        } catch (e: Throwable) {
            logger.error { "Throwable $e" }
        }
        return null!!
    }

    fun loadAll() {}

}