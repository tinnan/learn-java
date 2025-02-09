CREATE TABLE relation_teacher (
    id INTEGER PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

INSERT INTO relation_teacher(id, name) VALUES
(1, 'Teacher 1'),
(2, 'Teacher 2')
;

CREATE TABLE relation_course (
    id INTEGER PRIMARY KEY,
    code CHAR(5) NOT NULL,
    name VARCHAR(100) NOT NULL,
    teacher_id INTEGER NOT NULL
);

INSERT INTO relation_course(id, code, name, teacher_id) VALUES
(1, 'C0001', 'Course 1', 1),
(2, 'C0002', 'Course 2', 1),
(3, 'C0003', 'Course 3', 2)
;
