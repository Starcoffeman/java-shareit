DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS comments CASCADE;



CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    name  VARCHAR(255) UNIQUE                               NOT NULL,
    email VARCHAR(512) UNIQUE                               NOT NULL
    );

CREATE TABLE IF NOT EXISTS items
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    name        varchar(255) UNIQUE,
    description varchar(255),
    is_available   boolean,
    owner_id       integer REFERENCES users(id),
    request_id     integer
    );

CREATE TABLE IF NOT EXISTS bookings
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    start_date   TIMESTAMP WITHOUT TIME ZONE,
    end_date TIMESTAMP WITHOUT TIME ZONE,
    item_id   integer REFERENCES items(id),
    booker_id  integer REFERENCES users(id),
    status     varchar(255)
    );



CREATE TABLE IF NOT EXISTS comments
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    text   varchar(512),
    item_id integer REFERENCES items(id),
    author_id   integer REFERENCES users(id)
    );








