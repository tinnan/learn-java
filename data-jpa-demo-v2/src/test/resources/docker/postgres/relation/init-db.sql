CREATE TABLE relation_teacher (
    id INTEGER PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE relation_course (
    id INTEGER PRIMARY KEY,
    code CHAR(5) NOT NULL,
    name VARCHAR(100) NOT NULL,
    teacher_id INTEGER NOT NULL
);
