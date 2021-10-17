package battleship.server.webserver

//gameslots und maxclientcount hat noch keinen affekt
class ServerSettings(val gameSlots: Int, val maxClientCount: Int, val isLocalServer: Boolean, val port: Int)