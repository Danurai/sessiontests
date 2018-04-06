(ns sessiontests.web
   (:require [chord.http-kit :refer [with-channel]]
            [clojure.java.io :as io]
            [compojure.core :refer [defroutes GET POST]]
            [compojure.route :refer [not-found resources]]
            [clojure.core.async :refer [>! <! chan go go-loop mult tap]]
            [ring.middleware.session :refer :all] 
            [ring.middleware.params :refer :all]
            [ring.util.response :refer [resource-response]]
            [sessiontests.gameloop :refer [start-game-loop]]))
            
(defn ws-handler [req]
;; client connected
    (with-channel req ws-ch
      (start-game-loop ws-ch)))

(defn- logon-handler [id]
  (assoc 
    (resource-response "index.html" {:root "public"})
    :cookies 
      (if (nil? id)
        {"cmdr-id" {:value "" :max-age -1}} ;; deletes cookie
        {"cmdr-id" {:value id}})))
  
  ;;(str (assoc req :session {:name (get-in req [:params "id")})))

(defroutes app-routes
   (GET "/ws" [] ws-handler)
   (POST "/" [id] (logon-handler id))
   (GET "/" [] (slurp (io/resource "public/index.html")))
   (resources "/"))
   
(def app
  (-> app-routes
    ;;(wrap-defaults site-defaults) ;;(assoc-in site-defaults [:security :anti-forgery] false))
    (wrap-session {:cookie-attrs {:max-age 3600} :cookie-name :ring-session})
    (wrap-params)
    ))