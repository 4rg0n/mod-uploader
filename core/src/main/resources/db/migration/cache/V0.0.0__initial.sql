CREATE TABLE IF NOT EXISTS steam_game (
    id IDENTITY PRIMARY KEY,
    searchable VARCHAR(255),
    game JAVA_OBJECT
);

CREATE TABLE IF NOT EXISTS modio_game (
    id IDENTITY PRIMARY KEY,
    searchable VARCHAR(255),
    game JAVA_OBJECT
);

CREATE TABLE IF NOT EXISTS nexus_game (
    id IDENTITY PRIMARY KEY,
    name VARCHAR(255),
    game JAVA_OBJECT
);