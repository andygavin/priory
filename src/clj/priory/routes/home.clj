(ns priory.routes.home
  (:require
   [priory.layout :as layout]
   [priory.db.core :as db]
   [clojure.java.io :as io]
   [priory.middleware :as middleware]
   [ring.util.response]
   [priory.calculator :as calc]
   [ring.util.http-response :as response]
   [clojure.string :as str]))



(defn home-page [request]
  (layout/render request "home.html"))

(defn weights-handler
  [_]
  (let [weights (map #(update-in % [:weight] str) (db/get-weights))]
    (response/ok weights)
    ))


(defn group-scores
  "Takes the score row output and converts to map-by-item_id to vector scores"
  [scores]
  (let [grouped-scores (group-by :item_id scores)]
   (update-vals grouped-scores (partial map :score))))


(defn convert-decimals [lines]
  (map (fn [z]
         (update-in
          (update-in z [:score] str)
          [:scores] (fn [x] (mapv str x)))) lines
  )
)

(defn join-scores
  "Join items with the score vector to allow scores to be added. Items are the complete rows for injection, scores are all the scorings"
  [items scores weight-settings]
  (let [score-extract (group-scores scores)
        weights (into [] (map #(:weight %) weight-settings))
        scored (map (fn [item-row]
                  (let [row-scores (into [] (score-extract (item-row :id)))
                        score (calc/score-row weights row-scores)]
                    (assoc item-row :scores row-scores :score score))) items)
        ]
    (convert-decimals (sort-by :score > scored))
    )
  )

(defn scores-handler
  [_]
  (let [scores (db/get-scores)
        items (db/get-items)
        score-type (db/get-score-types)
        weights (db/get-weights)
        joined (join-scores items scores weights)]
    (prn scores (type (nth (map :score scores) 0)))

    (response/ok {:score-types score-type :scores joined})
    ))

(defn settings-handler
  [_]
  (let [settings (db/get-settings)]
    (response/ok settings)
    ))


(defn set-weights-handler
  [request]
  (let [data (:body-params request)]
    (doseq [weight-map data]
      (db/update-weight! {:weight (:weight weight-map) :id (:score_type_id weight-map)})
      ))
  (scores-handler nil))

(defn set-scores-handler
  [request]
  (let [data (:body-params request)]
    (let [{item :item
           score-row :scores
           types :types} data
          {scorev :scores} score-row]
      (doseq [[score-type score-value] (map vector types scorev)]
        (db/update-score! {:score score-value :score_type score-type :item_id item}))
      ))
  (scores-handler nil))


(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/docs" {:get (fn [_]
                    (-> (response/ok (-> "docs/docs.md" io/resource slurp))
                        (response/header "Content-Type" "text/plain; charset=utf-8")))}]
   ["/weights"
    {:get weights-handler :post set-weights-handler}
    ]
   ["/scores"
    {:get scores-handler}
    ]
   ["/settings"
    {:get settings-handler}
    ]
   ["/score-row"
    {:post set-scores-handler}]
   ])
