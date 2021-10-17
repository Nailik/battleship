package battleship.client.resources

import battleship.client.program.Sketch

enum class Colors(var color: Int) {
    C_OK(0x36a832),
    C_DEFAULT(0x324aa8),
    C_PRESS(0x0373fc),
    C_ERROR(0xdb0f0f),
    C_WHITE(0xFFFFFF),
    C_BLACK(0x000000),
    C_BLUE(0x187ccd);

    fun red(): Float{
        return Sketch.red(color)
    }
    fun blue(): Float{
        return Sketch.blue(color)
    }
    fun green(): Float{
        return Sketch.green(color)
    }
}