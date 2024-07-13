CREATE TABLE staff (
    id SERIAL PRIMARY KEY,
    staff_id CHAR(6) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    join_date DATE NOT NULL,
    "department" VARCHAR(100) NOT NULL,
    created_datetime TIMESTAMP NOT NULL DEFAULT(NOW() AT TIME ZONE 'utc'),
    updated_datetime TIMESTAMP NOT NULL DEFAULT(NOW() AT TIME ZONE 'utc')
);
