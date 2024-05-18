(ns priory.ajax
  (:require
    [ajax.core :as ajax]
    [luminus-transit.time :as time]
    [cognitect.transit :as transit]
    [re-frame.core :as rf]))

(defn local-uri? [{:keys [uri]}]
  (not (re-find #"^\w+?://" uri)))

(defn default-headers [request]
  (if (local-uri? request)
    (-> request
        (update :headers #(merge {"x-csrf-token" js/csrfToken} %)))
    request))


;; (def bigdecimal-write-handler
;;   (transit/write-handler
;;    "bigdec"
;;    str
;;    (fn [x] (str x))))

;; (defn bigdecimal-read-handler [tagged-value]
;;   (transit/bigdec (.-rep tagged-value)))

;; injects transit serialization config into request options



(defn as-transit [opts]
  (merge {:format          (ajax/transit-request-format
                            {:writer (transit/writer :json time/time-serialization-handlers
                                                     ;; (update-in
                                                     ;;   time/time-serialization-handlers
                                                     ;;  [:handlers] assoc java.math.BigDecimal bigdecimal-write-handler)
                                                     )})
          :response-format (ajax/transit-response-format
                            {:reader (transit/reader :json time/time-deserialization-handlers
                                                     ;; (update-in
                                                     ;;  time/time-deserialization-handlers
                                                     ;;  [:handlers] assoc "bigdec" bigdecimal-read-handler)

                                                     )})}
         opts))

(defn load-interceptors! []
  (swap! ajax/default-interceptors
         conj
         (ajax/to-interceptor {:name "default headers"
                               :request default-headers})))
