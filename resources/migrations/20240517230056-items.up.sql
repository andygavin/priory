CREATE TABLE item
(id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
 sys_id VARCHAR(10),
 description VARCHAR(30),
 notes VARCHAR(30),
 status VARCHAR(30),
 is_active BOOLEAN);
