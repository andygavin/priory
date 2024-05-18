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
SELECT i.id, i.sys_id, i.description, i.notes, i.status FROM item i

-- :name get-scores :? :1
-- :dpc get scores for item
SELECT s.item_id, s.score, st.headline, st.description, s.score_type_id
FROM scores s, score_type st
WHERE s.score_type_id = st.id
s.item_id = :item_id

-- :name get-weights :? :*
-- :dpc get scores for item
SELECT  w.weight, st.headline, st.description, w.score_type_id
FROM weights w, score_type st
WHERE w.score_type_id = st.id
