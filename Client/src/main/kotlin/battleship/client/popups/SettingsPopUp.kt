package battleship.client.popups

import battleship.client.elements.CheckBox
import battleship.client.elements.HorizontalSlider
import battleship.client.elements.Image
import battleship.client.program.Logic
import battleship.client.resources.Images
import battleship.client.resources.Music
import processing.core.PConstants
import processing.core.PVector

class SettingsPopUp : IPopUp("Settings") {

    /**
     * Ton an/aus
     */
    private val soundBtn = CheckBox(
        PVector(680f, 450f),
        Images.Sound_Off,
        Images.Sound_On,
        Images.Sound_Off_Pressed,
        Images.Sound_On_Pressed
    ).apply {
        value = Logic.userSettings.soundOn
        clickedLeft = {
            Logic.userSettings.soundOn = this.value
            Logic.saveSettings()
        }
    }

    private val soundSlider =
        HorizontalSlider(PVector(450f, 243f), Logic.userSettings.soundLevel, 300f).apply {
            onChange = { percentage ->
                Logic.userSettings.soundLevel = percentage
                Logic.saveSettings()
            }
        }

    /**
     * Musik an/aus
     */
    private val musicBtn = CheckBox(
        PVector(680f, 600f),
        Images.Music_Off,
        Images.Music_On,
        Images.Music_Off_Pressed,
        Images.Music_On_Pressed
    ).apply {
        value = Logic.userSettings.musicOn
        clickedLeft = {
            Logic.userSettings.musicOn = this.value
            Music.changeMusicOn()
            Logic.saveSettings()
            if (this.value) {
                Music.Background_Menu.play()
            }
        }
    }

    private val musicSlider =
        HorizontalSlider(PVector(450f, 317f), Logic.userSettings.musicLevel, 300f).apply {
            onChange = { percentage ->
                Logic.userSettings.musicLevel = percentage
                println("loudness music $percentage")
                Music.changeLoudness()
                Logic.saveSettings()
            }
        }


    private val close = Image(PVector(1220f, 390f), Images.Cross).apply {
        drawMode = PConstants.CENTER
        clickedLeft = {
            this@SettingsPopUp.close()
        }
    }


    override fun open() {
        addView(soundBtn)
        addView(soundSlider)
        addView(musicBtn)
        addView(musicSlider)
        addView(close)
    }

}