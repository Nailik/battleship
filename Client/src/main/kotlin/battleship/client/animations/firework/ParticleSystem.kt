package battleship.client.animations.firework

import battleship.client.program.Sketch
import battleship.client.resources.Sounds
import processing.core.PVector


// Daniel Shiffman
// http://codingtra.in
// http://patreon.com/codingtrain
// Code for:

// A class to describe a group of Particles
// An ArrayList is used to manage the list of Particles

class ParticleSystem {

    var gravity = PVector(0f, 0.12f)
    var particles // An arraylist for all the particles
            : ArrayList<Particle>
    var firework: Particle?
    var hu: Float
    fun done(): Boolean {
        return firework == null && particles.isEmpty()
    }

    fun run(offset: PVector) {
        firework?.also {
            Sketch.fill(hu, 255f, 255f)
            it.applyForce(gravity)
            it.update()
            it.display(offset)
            if (it.explode()) {
                Sounds.Firework.play()
                for (i in 0..99) {
                    particles.add(Particle(it.location, hu)) // Add "num" amount of particles to the arraylist
                }
                firework = null
            }
        }
        for (i in particles.indices.reversed()) {
            val p = particles[i]
            p.applyForce(gravity)
            p.run(offset)
            if (p.isDead) {
                particles.removeAt(i)
            }
        }
    }

    // A method to test if the particle system still has particles
    fun dead(): Boolean {
        return particles.isEmpty()
    }

    init {
        hu = Sketch.random(255f)
        firework = Particle(Sketch.random(Sketch.iniWidth.toFloat() * Sketch.scaleFactor), Sketch.iniHeight.toFloat() * Sketch.scaleFactor, hu)
        particles = ArrayList() // Initialize the arraylist
    }
}