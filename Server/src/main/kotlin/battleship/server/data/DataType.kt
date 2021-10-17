package battleship.server.data

enum class DataType {
    READ_LOBBY_LIST,
    CREATE_LOBBY,
    STATUS,
    JOIN_LOBBY,
    JOIN_LOBBY_FAILED,
    LOBBY_UPDATE,
    LOBBY_SETTINGS,
    SHIP_SELECTION,
    GAME_STATUS,
    GAME_STARTED,
    SHOT,
    SHOT_RESULT,
    SHOT_RECEIVED,
    CHANGED_NAME,
    GAME_SETTINGS
}