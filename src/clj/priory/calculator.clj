(ns priory.calculator
  (:require
   [priory.db.core :as db]
   [mount.core :refer [defstate]]
   ))


(def scorer-defined? (atom false))


(defstate ^{:on-reload :noop} score-calculator
  :start (db/get-setting {:keyname "prefs/score-formula"})
  :stop (reset! scorer-defined? false))

(defn- define-calculator
  "Define the row scoring function dynamically from the provided configtion"
  []
  (let [cal-def (:val score-calculator)
        new-fn (str "(defn score-internal [v] (let " cal-def "))")]
    (try (eval (read-string new-fn))
         (catch Exception e (throw (RuntimeException. (str "Failed to define a scoring function" new-fn)))))
    )
  )

(defn score-outer
  "dummy should be redefined"
  [w s] (identity))

(defn score-row
  "Entry-point for scoring,  first time this defined the function from configuration"
  [weights scores]
  (try
    (if @scorer-defined?
      ;; call defined funtion
      (score-outer weights scores)
      ;; Define first time around
      (do
        (define-calculator)
        (defn score-outer [weights scores]
          (let [wscores (mapv * weights scores)
                rowscore-fn (resolve 'score-internal)
                rowscore (rowscore-fn wscores)]
            rowscore))
        (reset! scorer-defined? true)
        ;; Call the newly defined function with the initial arguments
        (score-outer weights scores)
        ))
    (catch Exception e (println (str "Failed to score row " (.getMessage e))))))
