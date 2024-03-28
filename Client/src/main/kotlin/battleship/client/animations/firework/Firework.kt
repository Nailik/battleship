package battleship.client.animations.firework

import battleship.client.interfaces.IView
import battleship.client.program.Sketch
import battleship.client.program.plus
import processing.core.PConstants
import processing.core.PVector


class Firework : IView(PVector(0f, 0f)) {

    var fireworks = mutableListOf<ParticleSystem>()

    override fun draw(position: PVector) {
        Sketch.colorMode(PConstants.HSB)
        if (Sketch.random(1f) < 0.04) {
            fireworks.add(ParticleSystem())
        }
        Sketch.noStroke()

        for (i in fireworks.indices.reversed()) {
            val f = fireworks[i]
            f.run(this.position + this.offset)
            if (f.done()) {
                fireworks.removeAt(i)
            }
        }
        Sketch.colorMode(PConstants.RGB)
    }
}