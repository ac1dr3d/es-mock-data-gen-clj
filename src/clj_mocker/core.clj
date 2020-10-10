(ns clj-mocker.core
  (:require [clj-time.core :as t]
            [clj-time.local :as l]
            [clj-time.format :as f]
            [clj-time.coerce :as c]
            [clj-http.client :as client]
            [cheshire.core :as cs]
            [clj-uuid :as uuid]
            [clojure.pprint :as pp])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [
        max_users 1000
        max_events 1000
        now (l/local-now)
        week (t/minus now (t/days 7))
        time_range (- (c/to-long now) (c/to-long week))
        ]
    (client/with-connection-pool {:timeout 5 :threads 4 :insecure? false :default-per-route 10}
      (loop [x max_users]
        (when (> x 0)
          (let [
                user_id (str (uuid/v4))
                ]
            (loop [y max_events]
              (when (> y 0)
                (let [
                      ev_time (rand-int time_range)
                      d {
                         :unique_visitor_id user_id
                         :server_date ev_time
                         }
                      ]
                  (client/post "http://localhost:9200/index1/_doc"
                               {:async? true
                                :body (cs/encode d)
                                :content-type :json
                                :socket-timeout 1000
                                :connection-timeout 1000
                                :accept :json}
                               (fn [response] )
                               (fn [exception] (println "exception message is: " (.getMessage exception)))
                               )
                  )
                (recur (- y 1)))
              )
            )
          (recur (- x 1)))
        ))
    )
  )
