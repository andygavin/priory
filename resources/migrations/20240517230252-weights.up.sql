CREATE TABLE weights (
 id INTEGER PRIMARY KEY,
 score_type_id INTEGER,
 description CHAR(30),
 weight NUMERIC(20,2));
--;;
INSERT INTO weights (id, score_type_id, description, weight) VALUES (1,1,'Impact Weight', 1);
--;;
INSERT INTO weights (id, score_type_id, description, weight) VALUES (2,2,'Confidence Weight', 1);
--;;
INSERT INTO weights (id, score_type_id, description, weight) VALUES (3,3,'Ease Weight',1);
