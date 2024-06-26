(ns priory.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [priory.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[priory started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[priory has shut down successfully]=-"))
   :middleware wrap-dev})
