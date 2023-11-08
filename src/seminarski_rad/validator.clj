(ns seminarski-rad.validator 
  (:require [seminarski-rad.inputUtility :as util]))

(defn input-length-validator 
  "Checks if the user's input is longer than needed. Since the
    proper input is (ex. 'a1-b1') the proper length will always be 
   5"
  [input-str length]
  (if (not (= (count input-str) length))
     (do (println (str "Your input must contain "
                       length " characters!"))
         false)
     true))

(defn input-format-validator
  "Users input must strictly adhere to said form:
   'NL-NL' where N is a number 1-5 and L is a letter
   a-e or A-E."
  [input-str]
  (re-matches #"[1-5][a-eA-E]-[1-5][a-eA-E]" input-str))

(defn proper-piece-color-validator
  "Checks if the user is trying to move their own
    piece color or someone elses or a blank field."
  [input-str board]
  (= "B" (get-in board (util/get-move-start input-str)))
  )

(defn start-not-the-same-as-finish-validator 
  "Checks if the starting position of the move is 
   not the same as the finishing one."
  [input-str]
  (not (= (util/get-move-start input-str)
          (util/get-move-finish input-str))))

(defn validate-input
  [input-str board]
  (let [purified-input-str (util/purify-user-input input-str)] 
    (and (input-length-validator purified-input-str 5)
         (input-format-validator purified-input-str)
         (proper-piece-color-validator purified-input-str board)
         start-not-the-same-as-finish-validator purified-input-str)))
