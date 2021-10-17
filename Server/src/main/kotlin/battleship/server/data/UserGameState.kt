package battleship.server.data

enum class UserGameState(val value: Int) {
    /**
     * no game running
     */
    NOTHING(-1),

    /**
     * game is loading
     */
    STARTING(0),

    /**
     * user selects it's ships
     */
    SELECT_SHIPS(1),

    /**
     * user finished ship selection and is ready to start
     */
    SELECT_SHIPS_READY(2),

    /**
     * user can shoot to enemy
     */
    SHOOTING(3),

    /**
     * user spectates while enemy is shooting
     */
    SPECTATING(4),

    /**
     * user Lost
     */
    END_SCREEN_LOST(5),

    /**
     * user won
     */
    END_SCREEN_WON(6),

    /**
     * user is waiting for some result
     */
    WAITING_RESULT(7),
}