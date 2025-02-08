CREATE TABLE special_type (
    id INTEGER PRIMARY KEY NOT NULL,
    json_column JSONB NOT NULL
);

INSERT INTO special_type (id, json_column) VALUES
(1, '{"header": "Header title", "description_list": ["Description 1", "Description 2"]}');
