(ns guestbook.core
  (:require
    [reagent.core :as r]
    [reagent.dom :as dom]
    [ajax.core :refer [GET POST]]))

(defn send-message! [fields]
  (POST "/message"
        {:format        :json
         :headers       {"Accept"       "application/transit+json"
                         "x-csrf-token" (.-value (.getElementById js/document "token"))}
         :params        @fields
         :handler       #(.log js/console (str "response:" %))
         :error-handler #(.error js/console (str "error:" %))}))

(defn message-form []
  (let [fields (r/atom {})]
    (fn []
      [:div
       [:div.field
        [:label.label {:for :name} "Name"]
        [:input.input
         {:type      :text
          :name      :name
          :on-change #(swap! fields assoc :name (-> % .-target .-value))
          :value     (:name @fields)}]]
       [:div.field
        [:label.label {:for :message} "Message"]
        [:textarea.textarea
         {:name      :message
          :value     (:message @fields)
          :on-change #(swap! fields assoc :message (-> % .-target .-value))}]]
       [:input.button.is-primary
        {:type     :submit
         :on-click #(send-message! fields)
         :value    "comment"}]
       [:hr]
       [:p "Name: " (:name @fields)]
       [:p "Message: " (:message @fields)]])))

(defn home []
  [:div.content>div.columns.is-centered>div.column.is-two-thirds
   [:div.columns>div.column
    [message-form]]])

(dom/render
  [home]
  (.getElementById js/document "content"))