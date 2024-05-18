(ns priory.calculator-test
  (:require [priory.calculator :as sut]
            [clojure.test :as t]
            [priory.db.core :as db]
            [mount.core :as mount]))

(defn mock:get-setting [& rest]
  {:keyname "prefs/score-formula" :val ""})

(defn fixture [f]
  (with-redefs [db/get-setting mock:get-setting]
    (mount/only #'priory.calculator/score-calculator))
  (f)
  )

(t/use-fixtures :once fixture)

(t/deftest test-scoring-function-generation
  (t/is (= (@sut/scorer-defined? false)))
  (t/is (= (sut/score-rows [1 2 3] [2 2 2]) (* 6 (* 4 2))))
  (t/is (= (@sut/scorer-defined true))))
