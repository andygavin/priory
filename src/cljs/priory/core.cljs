(ns priory.core
  (:require
    [day8.re-frame.http-fx]
    [reagent.dom :as rdom]
    [reagent.core :as r]
    [reagent.debug :as dbg]
    [re-frame.core :as rf]
    [re-catch.core :as rc]
    [goog.events :as events]
    [goog.history.EventType :as HistoryEventType]
    [markdown.core :refer [md->html]]
    [priory.ajax :as ajax]
    [priory.events]
    [reitit.core :as reitit]
    [reitit.frontend.easy :as rfe]
    [clojure.string :as string])
  (:import goog.History))

(defn nav-link [uri title page]
  [:a.navbar-item
   {:href   uri
    :class (when (= page @(rf/subscribe [:common/page-id])) :is-active)}
   title])

(defn navbar []
  (r/with-let [expanded? (r/atom false)]
              [:nav.navbar.is-info>div.container
               [:div.navbar-brand
                [:a.navbar-item {:href "/" :style {:font-weight :bold}} "priory"]
                [:span.navbar-burger.burger
                 {:data-target :nav-menu
                  :on-click #(swap! expanded? not)
                  :class (when @expanded? :is-active)}
                 [:span][:span][:span]]]
               [:div#nav-menu.navbar-menu
                {:class (when @expanded? :is-active)}
                [:div.navbar-start
                 [nav-link "#/" "Home" :home]
                 [nav-link "#/about" "About" :about]
                 [nav-link "#/prioritise" "Prioritise" :prioritise]]]]))

(defn about-page []
  [:section.section>div.container>div.content
   [:img {:src "/img/warning_clojure.png"}]])

(defn home-page []
  [:section.section>div.container>div.content
   (when-let [docs @(rf/subscribe [:docs])]
     [:div {:dangerouslySetInnerHTML {:__html (md->html docs)}}])])


(defn on-enter-press [event callback]
  (when (= (.-keyCode event) 13) ;; 13 is the Enter key
    (callback)))


(defn editable-field
  [value on-change on-commit]
  [:input
   {:type "text"
    :value value
    :required true
    :on-change (fn [event] (on-change (-> event .-target .-value)))
    :on-key-down #(on-enter-press % on-commit)
    }])



(defn weights-table-component []
  (let [weights @(rf/subscribe [:weights])]
        [:div.weights
         [:table [:thead [:tr
                          (for [row weights]
                            ^{:key (str "wh_" (get row :score_type_id))}
                            [:th (get row :headline)])
                          ]]
          [:tbody [:tr
                   (for [row weights]
                     ^{:key (str "we_" (get row :score_type_id))}
                     [:td [editable-field
                           (row :weight)
                           #(rf/dispatch [:update-weight { :score_type_id (row :score_type_id) :value %}])
                           #(rf/dispatch [:commit-weights])]
                      ] )]]

          ]])
)


(defn- score-field
  "produces the hiccup vector for the editable score field"
  [score-row
   [val id] pair]
  (vector :td [editable-field val
               #(rf/dispatch [:update-score { :score_idx id :item (:id score-row) :value %}])
               #(rf/dispatch [:commit-score-row {:item (:id score-row)}])]))

(defn scores-table-component [scores]
  (let [scores @(rf/subscribe [:scores])
        {score-headings :score-types
         score-body :scores} scores
        score-ids (map #(:id %) score-headings)]
    [rc/catch [:div.scores
               [:table
                [:thead
                 (into [:tr] (mapv #(vector :th %) (concat ["Description" "ID"]
                                                           (map #(:headline %) score-headings)
                                                           ["Score" "Notes" "Status"])))]
                [rc/catch (if (not-empty score-body)
                    (into [:tbody]
                          (mapv
                           (fn [row]
                             (prn row)
                             (into [:tr] (concat

                                          [[:td (:description row)]
                                           [:td (:sys_id row)]]
                                            (mapv (partial score-field row) (map vector (:scores row) (range)))
                                          [
                                           [:td (:score row)]
                                           [:td (:notes row)]
                                           [:td (:status row)]
                                           ]
                                          ))

                               ) score-body)
                          )
                    )]
                 ]
               ]
     ]

    ))


(defn prioritise-page []
  [:section.section>div.container>div.content

   [weights-table-component]
   [scores-table-component]
   ;; (scores-table-component scores)
   ])


(defn page []
  (if-let [page @(rf/subscribe [:common/page])]
    [:div
     [navbar]
     [page]]))

(defn navigate! [match _]
  (rf/dispatch [:common/navigate match]))

(def router
  (reitit/router
    [["/" {:name        :home
           :view        #'home-page
           :controllers [{:start (fn [_] (rf/dispatch [:page/init-home]))}]}]
     ["/about" {:name :about
                :view #'about-page}]
     ["/prioritise" {:name :prioritise
                     :view #'prioritise-page
                     :controllers [{:start (fn [_] (rf/dispatch [:page/init-priority]))}]}]

     ]))

(defn start-router! []
  (rfe/start!
    router
    navigate!
    {}))

;; -------------------------
;; Initialize app
(defn ^:dev/after-load mount-components []
  (rf/clear-subscription-cache!)
  (rdom/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (start-router!)
  (ajax/load-interceptors!)
  (mount-components))
