CREATE TABLE score_type
(
id INTEGER PRIMARY KEY,
headline VARCHAR(20),
description VARCHAR(50)
)
--;;
INSERT INTO score_type (id,headline,description) VALUES (1,"Impact", "Impact");
--;;
INSERT INTO score_type (id,headline,description) VALUES (2,"Confidence", "Confidence");
--;;
INSERT INTO score_type (id,headline,description) VALUES (3,"Ease","Ease");
