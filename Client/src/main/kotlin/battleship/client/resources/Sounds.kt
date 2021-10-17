package battleship.client.resources

import battleship.client.program.SoundFile

object Sounds {

    val Button_Click = SoundFile("/sounds/button_click.wav", true, .05f)
    val Firework = SoundFile("/sounds/firework.mp3", true, .4f)
    val Shot_Hit = SoundFile("/sounds/explosion_hit.wav", true, .4f)
    val Shot_Miss = SoundFile("/sounds/splash_miss.wav", true, .4f)
    val Aye_Captain = SoundFile("/sounds/aye_captain.mp3", true, .4f)
    val Aye_Pirate = SoundFile("/sounds/aye_pirate.mp3", true, .4f)

    fun loadAll(){ }


}