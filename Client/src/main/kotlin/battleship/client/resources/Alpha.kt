package battleship.client.resources

import battleship.client.program.Sketch

enum class Alpha(var alpha: Float) {
    A_DEFAULT(255f),
    A_BACKGROUND(128f),
    A_MOUSE_OVER(100f),
    A_MOUSE_PRESSED(180f);
}