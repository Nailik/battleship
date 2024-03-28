package battleship.client.resources

import battleship.client.program.Logic
import battleship.client.program.SoundFile

object Music {

    //music_menu.position() <= 3.830
    //musik_battle.position() <= 39.424

    var Background_Menu = SoundFile("/music/music_menu.mp3", false, .5f, 3.830)
        private set
    var Background_Game = SoundFile("/music/music_battle.mp3", false, .5f, 39.424)
        private set

    fun check() {
        Background_Menu = Background_Menu.checkLoop()
        Background_Game = Background_Game.checkLoop()
    }

    fun changeMusicOn() {
        if (!Logic.userSettings.musicOn) {
            Background_Menu.stop()
            Background_Game.stop()
        }
    }

    fun changeLoudness() {
        Background_Menu.updateSoundLevel()
        Background_Game.updateSoundLevel()
    }

}