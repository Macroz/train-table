(ns train-table.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]
            [cljs-time.core :as time]
            [cljs-time.format :as format]
            [cljs-time.local :as localtime]
            [clojure.string :as s]))

;;;; Subscriptions

(re-frame/reg-sub
 :loading-r-trains?
 (fn [db]
   (:loading-r-trains? db)))

(re-frame/reg-sub
 :r-trains
 (fn [db]
   (:r-trains db)))

;;;; Events

(re-frame/reg-event-db
 :initialize-db
 (fn  [_ _]
   {:loading-r-trains? true}))

(re-frame/reg-event-fx
 :load-r-trains
 (fn [{:keys [db]} _]
   {:db (assoc db :loading-trains? true)
    :http-xhrio {:method :get
                 :uri "https://rata.digitraffic.fi/api/v1/live-trains"
                 :params {:arrived_trains 0
                          :arriving_trains 100
                          :departed_trains 0
                          :departing_trains 0
                          :station "HKI"}
                 :format (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [:load-r-trains-response]
                 :on-failure [:load-r-trains-failure]}}))

(defn clean-timetables [train]
  (update train :timeTableRows #(filter (comp #{"ARRIVAL"} :type) %)))

(defn in-the-future? [t]
  (time/before? (localtime/local-now)
                (localtime/to-local-date-time t)))

(defn assoc-current-position [train]
  (assoc train :current-position
         (->> (:timeTableRows train)
              (filter :trainStopping)
              (filter :liveEstimateTime)
              (filter (comp in-the-future? :liveEstimateTime))
              (first))))

(re-frame/reg-event-db
 :load-r-trains-response
 (fn [db [_ response]]
   (let [stations (group-by :stationShortCode (:stations db))]
     (assoc db
            :r-trains (->> (js->clj response)
                           (remove :cancelled)
                           (filter (comp #{"R"} :commuterLineID))
                           (map clean-timetables)
                           (map assoc-current-position))
            :loading-r-trains? false))))

(re-frame/reg-event-db
 :load-r-trains-failure
 (fn [db [_ response]]
   (assoc db
          :r-trains []
          :loading-r-trains? false)))


;;;; Views

(defn show-time [t]
  (localtime/format-local-time t :hour-minute))

(def time-format
  (format/formatter "HH:mm" (time/default-time-zone)))

(defn show-local-time [time]
  (format/unparse-local time-format (time/to-default-time-zone time)))

(defn main-panel []
  (let [r-trains (re-frame/subscribe [:r-trains])
        loading-r-trains? (re-frame/subscribe [:loading-r-trains?])]
    [:div.full.center
     (if @loading-r-trains?
       [:p "Loading, please wait..."]
       (let [active-stations (set (map (comp :stationShortCode :current-position) @r-trains))
             inactive-color "#CACACA"
             active-color "#FF5E5E"]
         [:svg {:style {:height "100%"}
                :width "126px" :height "424px" :viewBox "0 0 146 424" :version "1.1"}
          [:g {:id "Page-1" :stroke "none" :stroke-width "1" :fill "none" :fill-rule "evenodd"}
           [:g {:id "stops" :fill-rule "nonzero"}
            [:circle {:id "riihimäki" :fill (if (active-stations "RI") active-color inactive-color) :cx "4" :cy "4" :r "4"}]
            [:circle {:id "hyvinkää" :fill (if (active-stations "HY")  active-color inactive-color) :cx "31" :cy "81" :r "4"}]
            [:circle {:id "jokela" :fill (if (active-stations "JK")  active-color inactive-color) :cx "107" :cy "188" :r "4"}]
            [:circle {:id "saunakallio" :fill (if (active-stations "SAU")  active-color inactive-color) :cx "113" :cy "193" :r "4"}]
            [:circle {:id "järvenpää" :fill (if (active-stations "JP") active-color inactive-color) :cx "116" :cy "198" :r "4"}]
            [:circle {:id "ainola" :fill (if (active-stations "AIN")  active-color inactive-color) :cx "120" :cy "210" :r "8"}]
            [:circle {:id "kerava" :fill (if (active-stations "KE")  active-color inactive-color) :cx "122" :cy "249" :r "4"}]
            [:circle {:id "tikkurila" :fill (if (active-stations "TKL")  active-color inactive-color) :cx "99" :cy "331" :r "4"}]
            [:circle {:id "pasila" :fill (if (active-stations "PSL")  active-color inactive-color) :cx "59" :cy "400" :r "4"}]
            [:circle {:id "helsinki" :fill (if (active-stations "HKI")  active-color inactive-color) :cx "62" :cy "420" :r "4"}]
            ]]]))]))


;;;; Start

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (re-frame/dispatch-sync [:initialize-db])
  (re-frame/dispatch [:load-r-trains])
  (.setInterval js/window #(re-frame/dispatch [:load-r-trains]) 30000)
  (mount-root))
