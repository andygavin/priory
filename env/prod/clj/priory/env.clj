(ns priory.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[priory started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[priory has shut down successfully]=-"))
   :middleware identity})
