package battleship.server.data

enum class UserState(val value: Int) {
    /**
     * user started program
     */
    STARTING(0),

    /**
     * user is in name selection screen
     */
    CHOOSING_NAME(1),

    /**
     * user is connecting to server
     */
    CONNECTING(2),

    /**
     * user is connected to server and startscreen is showing up
     */
    START_SCREEN(3),

    /**
     * user wants to create a lobby
     */
    CREATE_GAME(4),

    /**
     * user looks at lobby list
     */
    LOBBYLIST(5),

    /**
     * user is waiting inside lobby who he created
     */
    LOBBY_CREATED(6),

    /**
     * user is waiting inside lobby who he joined
     */
    LOBBY_JOINED(14),

    /**
     * user wants to edit settigs
     */
    SERVER_SETTINGS(7),

    /**
     * User is in queue to wait for any player
     */
    QUEUE(8),

    /**
     * user is ready in Lobby and wants to start game
     */
    LOBBY_READY(9),

    /**
     * user currently playing
     */
    IN_GAME(10)
}