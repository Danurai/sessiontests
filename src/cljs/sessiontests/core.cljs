(ns sessiontests.core
   (:require [chord.client :refer [ws-ch]]
            [cljs.core.async :refer [<! put!]]
            [reagent.core :as r]
            [reagent.cookies :as cookies])
   (:require-macros [cljs.core.async.macros :refer [go]]))

(goog-define ws-uri "ws://localhost:9009/ws")

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (r/atom {:cmdr-id ""}))

(def container (.getElementById js/document "app"))

(defn- button-logon [logon?]
  (r/create-class {:reagent-render (fn [logon?] [:button.btn {:type "submit" :title (if logon? "Join" "Leave")} [:i.fas {:class (if logon? "fa-sign-in-alt" "fa-sign-out-alt")}]])
                 :component-did-mount (fn [this] (.tooltip (js/$ (r/dom-node this))))}))

(defn- logout-form [cmdr-id]
  [:form.form-inline.mr-auto {:action "/" :method "POST"}
    [:div.input-group
      [:input.form-control.bg-warning.text-dark {:disabled true :value (str "Joined as " cmdr-id)}]
      [:div.input-group-append
        [button-logon false]]]])
 
(defn- logon-form []
  [:form.form-inline.mr-auto {:action "/" :method "POST"}
    [:div.input-group
      [:input#id.form-control {:name "id" :type "text" :placeholder "Enter Name"}]
      [:div.input-group-append
        [button-logon true]]]])
        
        
(defn- navbar [ws-ch]
  [:nav.navbar.navbar-expand-md.navbar-dark.bg-dark  
    [:span.navbar-brand "Nav"]
    [:button.navbar-toggler {:type "button" :data-toggle "collapse" :data-target "#navbarcontent"}
      [:span.navbar-toggler-icon]]
    [:div#navbarcontent.navbar-collapse.collapse
      [:ul.navbar-nav.nav.mr-auto
        ;; [:li.nav-item
        ;;   [:a.nav-link {:class (if (nil? (cookies/get :cmdr-id)) "active") :href "#home" :data-toggle "tab"} "Home"]]
        [:li.nav-item
          [:a.nav-link {:class (if (nil? (cookies/get :cmdr-id)) "disabled" "active")
                      :href "#main" :data-toggle "tab"} "Main"]]
        [:li.nav-item
          [:a.nav-link {:class (if (nil? (cookies/get :cmdr-id)) "disabled ") :href "#manage" :data-toggle "tab"} "Manage"]]]
      [:ul.navbar-nav
        (if (nil? (cookies/get :cmdr-id))
          [logon-form]
          [logout-form (cookies/get :cmdr-id)])]]])

(defn- home-tab [ws-ch]
  [:div.container-fluid [:h1 "Home"]])

(defn- game-tab [ws-ch]
  [:div.container-fluid [:h1 "Main"]])

(defn- manage-tab [ws-ch]
  [:div.container-fluid 
    [:h1 "Manage"]
    [:h3 (str (:joined? @app-state))]
    [:button.btn.btn-warning {:on-click (fn [e] (put! ws-ch "websocket"))} "Click"]
    [:form {:action "/" :method "POST"}
        [button-logon false]]])
    
(defn- content [ws-ch]
  [:div.tab-content
    [:div#home.tab-pane.fade.container {:class (if (nil? (cookies/get :cmdr-id)) "active show") :role "tab-panel"}
      [home-tab ws-ch]]
    [:div#game.tab-pane.fade.container {:class (if (some? (cookies/get :cmdr-id)) "active show") :role "tab-panel"}
      [game-tab ws-ch]]
    [:div#manage.tab-pane.fade.container {:role "tab-panel"}
      [manage-tab ws-ch]]])
        
(defn- new-page [ws-ch]
  [:div
    [navbar ws-ch]
    [content ws-ch]]
  )
  
;;(defonce run-once 
  (go
    (let [{:keys [ws-channel error]} (<! (ws-ch ws-uri))]
       (if error
          (r/render [:div.container [:h2 "Oops, something went wrong"][:p (str error)]] container)
          (loop []
            (r/render [new-page ws-channel] container)
            (when-let [{:keys [message error]} (<! ws-channel)]
              ;; (prn (str message))
              (if (:connected? message) (swap! app-state assoc :joined? true))
              (prn @app-state)
              (recur))))));;)
            