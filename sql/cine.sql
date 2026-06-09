-- ============================================================
--  Cine Granada — Script SQL
-- ============================================================

CREATE DATABASE IF NOT EXISTS cine_granada;
USE cine_granada;

-- Tabla raíz de usuarios
CREATE TABLE usuarios (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    username  VARCHAR(50)  NOT NULL UNIQUE,
    password  VARCHAR(100) NOT NULL,
    email     VARCHAR(100) NOT NULL UNIQUE,
    nombre    VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    dni       VARCHAR(9)   NOT NULL UNIQUE,
    rol       ENUM('cliente','empleado') NOT NULL DEFAULT 'cliente'
);

-- Tabla hija clientes (Joined Table Inheritance)
CREATE TABLE clientes (
    usuario_id  INT PRIMARY KEY,
    telefono    VARCHAR(15),
    puntos      INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_cliente FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- Tabla hija empleados (Joined Table Inheritance)
CREATE TABLE empleados (
    usuario_id   INT PRIMARY KEY,
    num_empleado VARCHAR(20) NOT NULL,
    puesto       VARCHAR(50) NOT NULL DEFAULT 'taquillero',
    CONSTRAINT fk_empleado FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- Tabla entidad principal: peliculas
CREATE TABLE peliculas (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    titulo      VARCHAR(150) NOT NULL,
    genero      VARCHAR(50)  NOT NULL,
    duracion    INT NOT NULL,
    director    VARCHAR(100),
    anio        INT NOT NULL,
    precio      DECIMAL(8,2) NOT NULL DEFAULT 8.00
);

-- Tabla sesiones: fechas y horas de proyección de cada película
CREATE TABLE sesiones (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    pelicula_id INT  NOT NULL,
    fecha       DATE NOT NULL,
    hora        TIME NOT NULL,
    sala        VARCHAR(10) NOT NULL DEFAULT 'Sala 1',
    aforo       INT NOT NULL DEFAULT 100,
    CONSTRAINT fk_ses_pelicula FOREIGN KEY (pelicula_id) REFERENCES peliculas(id) ON DELETE CASCADE
);

-- Tabla relación N:M: entradas (clientes <-> sesiones)
CREATE TABLE entradas (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id  INT  NOT NULL,
    sesion_id   INT  NOT NULL,
    cantidad    INT  NOT NULL DEFAULT 1,
    CONSTRAINT fk_ent_cliente  FOREIGN KEY (cliente_id) REFERENCES clientes(usuario_id) ON DELETE CASCADE,
    CONSTRAINT fk_ent_sesion   FOREIGN KEY (sesion_id)  REFERENCES sesiones(id)         ON DELETE CASCADE
);

-- Datos de prueba
INSERT INTO usuarios (username, password, email, nombre, apellidos, dni, rol) VALUES
('admin',  '1234', 'admin@cine.es',  'Carlos',  'García Ruiz',    '12345678A', 'empleado'),
('laura',  '1234', 'laura@cine.es',  'Laura',   'Pérez Molina',   '23456789B', 'empleado'),
('ana',    '1234', 'ana@cine.es',    'Ana',      'Martínez Vega',  '34567890C', 'cliente'),
('pedro',  '1234', 'pedro@cine.es',  'Pedro',    'Sánchez Gil',    '45678901D', 'cliente'),
('lucia',  '1234', 'lucia@cine.es',  'Lucía',    'Fernández Mora', '56789012E', 'cliente');

INSERT INTO empleados (usuario_id, num_empleado, puesto) VALUES
(1, 'EMP-001', 'gerente'),
(2, 'EMP-002', 'taquillero');

INSERT INTO clientes (usuario_id, telefono, puntos) VALUES
(3, '600111222', 150),
(4, '611222333',  50),
(5, '622333444', 200);

INSERT INTO peliculas (titulo, genero, duracion, director, anio, precio) VALUES
('Dune: Parte Dos',        'Ciencia ficción', 166, 'Denis Villeneuve', 2024, 9.50),
('Oppenheimer',            'Drama histórico', 180, 'Christopher Nolan', 2023, 8.00),
('El reino del planeta de los simios', 'Acción', 145, 'Wes Ball', 2024, 9.00),
('Pobres criaturas',       'Drama',           141, 'Yorgos Lanthimos', 2023, 8.00),
('Wonka',                  'Fantasía',        116, 'Paul King',         2023, 7.50),
('La sociedad de la nieve','Drama',           144, 'J.A. Bayona',       2023, 8.00),
('Kung Fu Panda 4',        'Animación',        94, 'Mike Mitchell',     2024, 7.00),
('Godzilla y Kong',        'Acción',          115, 'Adam Wingard',      2024, 9.00);

-- Sesiones de prueba para las películas
INSERT INTO sesiones (pelicula_id, fecha, hora, sala) VALUES
(1, '2026-06-10', '16:00:00', 'Sala 1'),
(1, '2026-06-10', '19:30:00', 'Sala 1'),
(1, '2026-06-11', '20:00:00', 'Sala 2'),
(2, '2026-06-10', '17:00:00', 'Sala 3'),
(2, '2026-06-12', '21:00:00', 'Sala 3'),
(3, '2026-06-11', '16:30:00', 'Sala 2'),
(3, '2026-06-13', '19:00:00', 'Sala 1'),
(4, '2026-06-10', '16:00:00', 'Sala 5'),
(4, '2026-06-12', '19:00:00', 'Sala 5'),
(5, '2026-06-10', '18:00:00', 'Sala 4'),
(6, '2026-06-11', '17:30:00', 'Sala 5'),
(6, '2026-06-14', '20:30:00', 'Sala 5'),
(7, '2026-06-10', '17:30:00', 'Sala 2'),
(7, '2026-06-11', '16:00:00', 'Sala 1'),
(8, '2026-06-10', '18:30:00', 'Sala 4'),
(8, '2026-06-13', '21:30:00', 'Sala 3');

-- Entradas de prueba referenciando sesiones
INSERT INTO entradas (cliente_id, sesion_id, cantidad) VALUES
(3, 1, 2),
(3, 4, 1),
(4, 3, 1),
(5, 9, 2),
(3, 8, 3);
