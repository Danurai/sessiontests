(ns sessiontests.gameloop
  (:require
    [clojure.core.async :refer [>! <! chan go go-loop mult tap]]))

(def out-ch (chan))            ;; Channel to broadcast on
(def out-mult (mult out-ch))     ;; Mult to attach channels to

(def app-state (atom {}))

(defn start-game-loop [ws-ch]
  (tap out-mult ws-ch)    ;; Tap user channel to mult
  
  (go
    (loop []
      (>! out-ch @app-state)
      (if-let [{:keys [message]} (<! ws-ch)]
        (do
          (>! ws-ch {:connected? true})
          ;; (prn message)
          (recur))
        (do
          ;; disconnect user
          (>! out-ch @app-state)
          )))))