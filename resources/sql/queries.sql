-- :name update-weight! :! :n
-- :doc update weight value
UPDATE weights
SET weight = :weight
WHERE score_type_id = :id

-- :name update-score! :! :n
-- :doc update weight value
UPDATE scores
SET score = :score
WHERE score_type_id = :score_type
AND item_id = :item_id

-- :name get-items :? :*
-- :doc retrieves a user record given the id
SELECT i.id, i.sys_id, i.description, i.notes, i.status,i.is_active FROM item i

-- :name get-scores :? :*
-- :doc get score table body
SELECT score,score_type_id,item_id
FROM  scores
ORDER BY item_id, score_type_id

-- :name get-weights :? :*
-- :doc get scores for item
SELECT  w.weight, st.headline, st.description, w.score_type_id
FROM weights w
JOIN score_type st ON w.score_type_id = st.id

-- :name get-settings :? :*
-- :doc read settings table
SELECT keyname,val
FROM setting

-- :name get-setting  :? :1
-- :doc read setting  table
SELECT keyname,val
FROM setting
WHERE keyname = :keyname

-- :name get-score-types :? :*
-- :doc read score types in order
SELECT id, headline, description
FROM score_type
ORDER BY id
