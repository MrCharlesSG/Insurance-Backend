
INSERT INTO ROLES (name) VALUES
('VEHICLE');
-- password == user
INSERT INTO users (username, password) VALUES
('1234ABC', '$2a$12$h0HcS2QDb/7zPASbLa2GoOTSRP6CWK0oX7pCK.dPjkM6L5N4pNovi'),
('5678DEF', '$2a$12$h0HcS2QDb/7zPASbLa2GoOTSRP6CWK0oX7pCK.dPjkM6L5N4pNovi'),
('9012GHI', '$2a$12$h0HcS2QDb/7zPASbLa2GoOTSRP6CWK0oX7pCK.dPjkM6L5N4pNovi'),
('3456JKL', '$2a$12$h0HcS2QDb/7zPASbLa2GoOTSRP6CWK0oX7pCK.dPjkM6L5N4pNovi'),
('7890MNO', '$2a$12$h0HcS2QDb/7zPASbLa2GoOTSRP6CWK0oX7pCK.dPjkM6L5N4pNovi');

INSERT INTO vehicles (user_info, brand, model, manufacturing_year) VALUES
(1, 'Toyota', 'Corolla', 2020),
(2, 'Honda', 'Civic', 2019),
(3, 'Ford', 'Focus', 2018),
(4, 'Chevrolet', 'Malibu', 2021),
(5, 'Nissan', 'Sentra', 2017);

INSERT INTO driver (name, surnames, passport, email, birthday) VALUES
('Vicente', 'Maroto', 'X2345687', 'vic.mar@example.com', '1980-01-11'),
('German', 'Palomares', 'L0101010', 'ger.pa@example.com', '1980-01-11'),
('María', 'López', 'Y7654321', 'maria.lopez@example.com', '1990-02-02');

INSERT INTO vehicle_driver_mapping (driver_id, vehicle_id) VALUES
(3, 1),
(2, 1);