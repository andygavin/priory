CREATE TABLE weights(
 id INTEGER PRIMARY KEY,
 score_type_id INTEGER,
 description CHAR(30),
 weight INTEGER);
--;;
INSERT INTO weights (1,1,"Impact Weight", 1);
--;;
INSERT INTO weights (2,2,"Confidence Weight", 1);
--;;
INSERT INTO weights (3,3,"Ease Weight",1);
