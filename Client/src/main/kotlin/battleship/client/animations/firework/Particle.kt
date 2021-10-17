package battleship.client.animations.firework

import battleship.client.program.Sketch
import battleship.client.program.plus
import battleship.client.program.times
import processing.core.PVector


// Daniel Shiffman
// http://codingtra.in
// http://patreon.com/codingtrain
// Code for:

 class Particle {
    var location: PVector
    var velocity: PVector
    var acceleration: PVector
    var lifespan: Float
    var seed = false
    var hu: Float

    constructor(x: Float, y: Float, h: Float) {
        hu = h
        acceleration = PVector(0f, 0f)
        velocity = PVector(0f, Sketch.random(-15f, -15f))
        location = PVector(x, y)
        seed = true
        lifespan = 255.0f
    }

    constructor(l: PVector, h: Float) {
        hu = h
        acceleration = PVector(0f, 0f)
        velocity = PVector.random2D()
        velocity.mult(Sketch.random(4f, 8f))
        location = l.copy()
        lifespan = 255.0f
    }

    fun applyForce(force: PVector?) {
        acceleration.add(force)
    }

    fun run(offset: PVector) {
        update()
        display(offset)
    }

    fun explode(): Boolean {
        if (seed && velocity.y > 0) {
            lifespan = 0f
            return true
        }
        return false
    }

    // Method to update location
    fun update() {
        velocity.add(acceleration)
        location.add(velocity * Sketch.scaleFactor)
        if (!seed) {
            lifespan -= 5.0f
            velocity.mult(0.95f)
        }
        acceleration.mult(0f)
    }

    // Method to display
    fun display(offset: PVector) {
        Sketch.stroke(hu, 255f, 255f, lifespan)
        if (seed) {
            Sketch.strokeWeight(4f)
        } else {
            Sketch.strokeWeight(2f)
        }
        val currentLocation = location + offset
        Sketch.rect(currentLocation.x, currentLocation.y, 5f, 5f)
    }

    // Is the particle still useful?
    val isDead: Boolean
        get() = lifespan < 0.0
}