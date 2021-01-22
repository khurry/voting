DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS dishes;
DROP TABLE IF EXISTS menus;
DROP TABLE IF EXISTS restaurants;


DROP SEQUENCE IF EXISTS global_seq;

CREATE SEQUENCE global_seq START WITH 100000;

CREATE TABLE restaurants
(
    id   INT DEFAULT nextval('global_seq') PRIMARY KEY,
    name VARCHAR NOT NULL
);
CREATE UNIQUE INDEX restaurant_unique_name_idx ON restaurants (name);

CREATE TABLE menus
(
    id           INT       DEFAULT nextval('global_seq') PRIMARY KEY,
    created      TIMESTAMP DEFAULT now() NOT NULL,
    restaurant_id INT                     NOT NULL,
    votecount    INT       DEFAULT 0     NOT NULL,
    CHECK (votecount >= 0),
    FOREIGN KEY (restaurant_id) REFERENCES restaurants (id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX menu_unique_created_idx ON menus (created, restaurant_id);

CREATE TABLE dishes
(
    id     INT DEFAULT nextval('global_seq') PRIMARY KEY,
    menu_id INT     NOT NULL,
    name   VARCHAR NOT NULL,
    price  INT     NOT NULL,
    FOREIGN KEY (menu_id) REFERENCES menus (id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX dish_unique_name_idx ON dishes (name, menu_id);

CREATE TABLE users
(
    id         INT       DEFAULT nextval('global_seq') PRIMARY KEY,
    name       VARCHAR                 NOT NULL,
    email      VARCHAR                 NOT NULL,
    password   VARCHAR                 NOT NULL,
    registered TIMESTAMP DEFAULT now() NOT NULL,
    menu_id     INT,
    FOREIGN KEY (menu_id) REFERENCES menus (id)
);
CREATE UNIQUE INDEX users_unique_email_idx ON users (email);
-- CREATE UNIQUE INDEX users_unique_menu_idx ON users (id, menu);

CREATE TABLE user_roles
(
    user_id INT NOT NULL,
    role    VARCHAR,
    CONSTRAINT user_roles_idx UNIQUE (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);