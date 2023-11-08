(ns seminarski-rad.validator
  (:require [clojure.string :as str])
  (:require [seminarski-rad.inputUtility :as util]))

(defn input-length-validator 
  "Checks if the user's input is longer than needed. Since the
    proper input is (ex. 'a1-b1') the proper length will always be 
   5"
  [input length]
  (if (not (= (count input) length))
     (do (println (str "Your input must contain "
                       length " characters!"))
         false)
     true))

(defn input-format-validator
  "Users input must strictly adhere to said form:
   'NL-NL' where N is a number 1-5 and L is a letter
   a-e or A-E."
  [input]
  (re-matches #"[1-5][a-eA-E]-[1-5][a-eA-E]" input))



(defn proper-piece-color-validator
  "Checks if the user is trying to move their own
    piece color or someone elses or a blank field."
  [input board]
  (= "B" (get-in board (util/get-move-start input)))
  )

(proper-piece-color-validator "5E:2B"
                              {:1 {:A "B" :B "B" :C "B" :D "B" :E "B"}
                               :2 {:A "B" :B "B" :C "B" :D "B" :E "B"}
                               :3 {:A "B" :B "B" :C "*" :D "R" :E "R"}
                               :4 {:A "R" :B "R" :C "R" :D "R" :E "R"}
                               :5 {:A "R" :B "R" :C "R" :D "R" :E "R"}})





