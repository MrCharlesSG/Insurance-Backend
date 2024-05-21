-- Roles
INSERT INTO ROLES (name) VALUES
('VEHICLE');

-- Users (password == user)
INSERT INTO users (username, password) VALUES
('1234ABC', '$2a$12$h0HcS2QDb/7zPASbLa2GoOTSRP6CWK0oX7pCK.dPjkM6L5N4pNovi'),
('5678DEF', '$2a$12$h0HcS2QDb/7zPASbLa2GoOTSRP6CWK0oX7pCK.dPjkM6L5N4pNovi'),
('9012GHI', '$2a$12$h0HcS2QDb/7zPASbLa2GoOTSRP6CWK0oX7pCK.dPjkM6L5N4pNovi'),
('3456JKL', '$2a$12$h0HcS2QDb/7zPASbLa2GoOTSRP6CWK0oX7pCK.dPjkM6L5N4pNovi'),
('7890MNO', '$2a$12$h0HcS2QDb/7zPASbLa2GoOTSRP6CWK0oX7pCK.dPjkM6L5N4pNovi');

-- Vehicles
INSERT INTO vehicles (user_info, brand, model, manufacturing_year) VALUES
(1, 'Toyota', 'Corolla', 2020),
(2, 'Honda', 'Civic', 2019),
(3, 'Ford', 'Focus', 2018),
(4, 'Chevrolet', 'Malibu', 2021),
(5, 'Nissan', 'Sentra', 2017);

-- Drivers
INSERT INTO driver (name, surnames, passport, email, birthday) VALUES
('Vicente', 'Maroto', 'X2345687', 'vic.mar@example.com', '1980-01-11'),
('German', 'Palomares', 'L0101010', 'ger.pa@example.com', '1980-01-11'),
('María', 'López', 'Y7654321', 'maria.lopez@example.com', '1990-02-02'),
('Carlos', 'Gomez', 'Z1234567', 'carlos.gomez@example.com', '1985-05-15'),
('Ana', 'Fernandez', 'A9876543', 'ana.fernandez@example.com', '1992-03-22');

-- Vehicle and Driver Mapping
INSERT INTO vehicle_driver_mapping (driver_id, vehicle_id) VALUES
(3, 1),
(2, 1),
(4, 2),
(5, 3);

-- InfoReportDriver
INSERT INTO info_report_driver (vehicle_id, driver_id, damages, status) VALUES
(1, 3, 'Scratch on the left door', 1),
(1, 2, 'Broken rear light', 0),
(2, 4, 'Minor dent on front bumper', 1),
(3, 5, 'Shattered windshield', 1);

-- Reports
INSERT INTO report (info_report_driver_a, info_report_driver_b, date, place, details) VALUES
(1, 3, '2024-05-10', 'Main Street', 'Collision at intersection'),
(4, 2, '2024-04-22', 'Broadway Avenue', 'Fender bender in parking lot');
