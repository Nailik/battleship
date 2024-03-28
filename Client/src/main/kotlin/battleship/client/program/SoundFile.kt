package battleship.client.program

import com.jsyn.data.FloatSample
import com.jsyn.util.SampleLoader
import fr.delthas.javamp3.Sound
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

//import processing.sound.Engine;
data class SoundFile(
    val var2: String,
    private val isSoundEffect: Boolean,
    private val camp: Float,
    private val loop: Double = 0.0
) : processing.sound.AudioSample(Sketch) {

    companion object {
        private val SAMPLECACHE: MutableMap<String, FloatSample> = HashMap<String, FloatSample>()
    }

    override fun play() {
        if (isSoundEffect && Logic.userSettings.soundOn) {
            amp(camp * Logic.userSettings.soundLevel)
            super.play()
        } else if (!isSoundEffect && Logic.userSettings.musicOn) {
            amp(camp * Logic.userSettings.musicLevel)
            super.play()
        }
    }

    fun updateSoundLevel() {
        if (isSoundEffect) {
            amp(camp * Logic.userSettings.soundLevel)
        } else if (!isSoundEffect) {
            amp(camp * Logic.userSettings.musicLevel)
        }
    }

    fun checkLoop(): SoundFile {
        if (isPlaying && loop > 0.0) {
            if (duration() - position() <= loop) {
                //start again, next instance
                return copy().apply {
                    play()
                }
            }
        }
        return this
    }

    init {
        sample = SAMPLECACHE[var2]
        if (sample == null) {

            var var4 = Sketch::class.java.getResourceAsStream(var2)



            if (var4 == null) {
                //Engine.printError("unable to find file " + var2);
            }
            try {
                sample = SampleLoader.loadFloatSample(var4)
            } catch (var21: IOException) {
                try {
                    var4 = BufferedInputStream(Sketch::class.java.getResourceAsStream(var2)!!)
                    val var6 = Sound(var4)
                    try {
                        val var7 = ByteArrayOutputStream()
                        var6.decodeFullyInto(var7)
                        val var8 = FloatArray(var7.size() / 2)
                        SampleLoader.decodeLittleI16ToF32(var7.toByteArray(), 0, var7.size(), var8, 0)
                        sample = FloatSample(var8, if (var6.isStereo) 2 else 1)
                    } catch (var16: IOException) {
                        throw var16
                    } catch (var17: NullPointerException) {
                        throw IOException()
                    } catch (var18: ArrayIndexOutOfBoundsException) {
                        throw IOException()
                    } finally {
                        var6.close()
                    }
                } catch (var20: IOException) {
                    // Engine.printError("unable to decode sound file " + var2);

                }
            }
            SAMPLECACHE[var2] = sample
        }
        initiatePlayer()
    }
}