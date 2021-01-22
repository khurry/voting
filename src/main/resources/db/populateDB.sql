DELETE FROM restaurants;
DELETE FROM MENUS;
DELETE FROM dishes;
DELETE FROM users;
DELETE FROM user_roles;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO RESTAURANTS (name)
VALUES ('Union Bar'),
       ('Restomarket');

INSERT INTO MENUS (RESTAURANT_ID)
VALUES ( 100000);
INSERT INTO MENUS (RESTAURANT_ID, CREATED)
VALUES (100000, '2020-12-21');
INSERT INTO MENUS (RESTAURANT_ID)
VALUES ( 100001);

INSERT INTO DISHES (MENU_ID, NAME, PRICE)
VALUES (100002, 'Cesar Salad', 2500),
       (100002, 'Cheese Soup', 3000),
       (100003, 'Fresh salad', 1500),
       (100003, 'Borsch', 1600),
       (100003, 'Rice with chicken', 1800),
       (100004, 'Salad with chicken', 3000),
       (100004, 'Burger', 3100);

INSERT INTO users (name, email, password)
VALUES ('User', 'user@yandex.ru', 'password'),
       ('Admin', 'admin@gmail.com', 'admin');

INSERT INTO user_roles (role, user_id)
VALUES ('USER', 100012),
       ('ADMIN', 100013);