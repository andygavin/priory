(ns priory.events
  (:require
    [re-frame.core :as rf]
    [ajax.core :as ajax]
    [reitit.frontend.easy :as rfe]
    [reagent.debug :as dbg]
    [reitit.frontend.controllers :as rfc]))

;;dispatchers

(rf/reg-event-db
  :common/navigate
  (fn [db [_ match]]
    (let [old-match (:common/route db)
          new-match (assoc match :controllers
                                 (rfc/apply-controllers (:controllers old-match) match))]
      (assoc db :common/route new-match))))

(rf/reg-fx
  :common/navigate-fx!
  (fn [[k & [params query]]]
    (rfe/push-state k params query)))

(rf/reg-event-fx
  :common/navigate!
  (fn [_ [_ url-key params query]]
    {:common/navigate-fx! [url-key params query]}))

(rf/reg-event-db
  :set-docs
  (fn [db [_ docs]]
    (assoc db :docs docs)))

(rf/reg-event-fx
  :fetch-docs
  (fn [_ _]
    {:http-xhrio {:method          :get
                  :uri             "/docs"
                  :response-format (ajax/raw-response-format)
                  :on-success       [:set-docs]}}))

(rf/reg-event-db
  :set-weights
  (fn [db [_ docs]]
    (assoc db :weights docs)))

(rf/reg-event-db
  :set-scores
  (fn [db [_ docs]]
    (assoc db :scores docs :processing? false)))

(rf/reg-event-db
  :set-settings
  (fn [db [_ docs]]
    (assoc db :settings docs)))




(rf/reg-event-fx
 :fetch-weights
 (fn [_ _]
   {:http-xhrio {:method          :get
                 :uri             "/weights"
                 :response-format (ajax/transit-response-format)
                 :on-success       [:set-weights]}}))

(rf/reg-event-fx
  :fetch-scores
  (fn [_ _]
    {:http-xhrio {:method          :get
                  :uri             "/scores"
                  :response-format (ajax/transit-response-format)
                  :on-success       [:set-scores]}}))

(rf/reg-event-fx
  :fetch-settings
  (fn [_ _]
    {:http-xhrio {:method          :get
                  :uri             "/settings"
                  :response-format (ajax/transit-response-format)
                  :on-success       [:set-settings]}}))

(defn update-weight-for-score [weights score-type-id weight]
  (mapv (fn [m]
          (if (= (:score_type_id m) score-type-id)
            (assoc m :weight weight)
            m))
        weights))

;; Update weight in the UI model
(rf/reg-event-db
 :update-weight
 (fn [db [_ updated-weight]]
   (let [{oldw :weights} db
         {score-type :score_type_id weight :value} updated-weight]
     (assoc db :weights (update-weight-for-score oldw score-type weight)))))

(rf/reg-event-fx
  :commit-weights
  (fn [{:keys [db]} [_ a]]
    (let [{weights :weights} db]

      {:http-xhrio {:method          :post
                    :uri             "/weights"
                    :params          weights
                    :format          (ajax/transit-request-format)
                    :response-format (ajax/transit-response-format)
                    :on-success       [:set-scores]
                    :on-failure       [:error-response "POST weights"]}
       :db  (assoc db :processing? true)}
      )
    )
  )

(defn update-list [coll idx new-val]
  (map-indexed (fn [i v]
                 (if (= i idx)
                   new-val
                   v))
               coll))


(defn update-score-value-in-rows [item-id idx new-val items]
  (let [update-map
        (fn [m]
          (if (= (:id m) item-id)
            (update m :scores assoc idx new-val)
            m))]
    (mapv update-map items)))


(rf/reg-event-db
 :update-score
 (fn [db [_ updated-score]]
   (let [{olds :scores} db
         {inner :scores} olds
         {score-index :score_idx score :value position :item} updated-score
         updated-scores (update-score-value-in-rows position score-index score inner)]
     (assoc db :scores (assoc olds :scores updated-scores))
     )
   )
 )

(rf/reg-event-fx
  :commit-score-row
  (fn [{:keys [db]} [_ a]]
    (let [{scores-payload :scores} db
          {scores :scores
           types :score-types} scores-payload
          {item :item} a
          replacement (first (filter #(= item (:id %)) scores))
          type_ids (map #(:id %) types)]
      {:http-xhrio {:method          :post
                    :uri             "/score-row"
                    :params          { :types type_ids :scores replacement :item item }
                    :format (ajax/transit-request-format)
                    :response-format (ajax/transit-response-format)
                    :on-success       [:set-scores]
                    :on-failure       [:error-response "POST weights"]}
       :db  (assoc db :processing? true)
       }
      )
    )
  )



(rf/reg-event-db
 :success-response
 (fn [db [_ method result]]
   (assoc db :processing? false)))

(rf/reg-event-db
  :error-response
  (fn [db [_ method result]]
    (assoc db :last-error result
              :processing? false)))

(rf/reg-event-db
 :common/set-error
 (fn [db [_ error]]
   (assoc db :common/error error)))

(rf/reg-event-fx
  :page/init-home
  (fn [_ _]
    {:dispatch [:fetch-docs]}))

(rf/reg-event-fx
  :page/init-priority
  (fn [_ _]
    {:fx [[:dispatch [:fetch-weights]]
          [:dispatch [:fetch-scores]]
          [:dispatch [:fetch-settings]]]}))

;;subscriptions

(rf/reg-sub
 :common/route
 (fn [db _]
   (-> db :common/route)))

(rf/reg-sub
  :common/page-id
  :<- [:common/route]
  (fn [route _]
    (-> route :data :name)))

(rf/reg-sub
  :common/page
  :<- [:common/route]
  (fn [route _]
    (-> route :data :view)))

(rf/reg-sub
  :docs
  (fn [db _]
    (:docs db)))

(rf/reg-sub
  :weights
  (fn [db _]
    (:weights db)))

(rf/reg-sub
 :scores
 (fn [db _]
   (:scores db)))

(rf/reg-sub
 :common/error
 (fn [db _]
   (:common/error db)))
