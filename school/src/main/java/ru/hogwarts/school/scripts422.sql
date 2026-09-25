-- Таблица Person
CREATE TABLE persons (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INT CHECK (age >= 18),
    has_driver_license BOOLEAN DEFAULT FALSE
);

-- Таблица Car
CREATE TABLE cars (
    id SERIAL PRIMARY KEY,
    brand VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    price DECIMAL(12, 2) NOT NULL CHECK (price >= 0)
);

-- Связующая таблица Person_Car
CREATE TABLE person_car (
    person_id INT REFERENCES persons(id) ON DELETE CASCADE,
    car_id INT REFERENCES cars(id) ON DELETE CASCADE,
    PRIMARY KEY (person_id, car_id)
);

-- Добавляем людей
INSERT INTO persons (name, age, has_driver_license) VALUES
('Гарри Поттер', 19, TRUE),
('Гермиона Грейнджер', 18, TRUE),
('Рон Уизли', 20, TRUE),
('Драко Малфой', 21, TRUE),
('Пэнси Паркинсон', 18, TRUE);

-- Добавляем машины
INSERT INTO cars (brand, model, price) VALUES
('Chery', 'tiggo8', 2500000),
('Toyota', 'corolla', 1950000),
('LADA', 'Vesta', 1500000),
('Geely', 'Tugella', 3800000);

INSERT INTO person_car (person_id, car_id) VALUES
(6, 1),
(7, 1),
(8, 3),
(9, 2),
(10, 4);