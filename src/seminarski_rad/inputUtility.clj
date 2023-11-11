(ns seminarski-rad.inputUtility
  (:require [clojure.string :as str]))

(defn purify-user-input
  "Removes unnecessary blank characters and capitalizes 
   all letters in input."
  [input]
  (str/upper-case (str/trim input)))

(defn extract-keys-from-user-input
  [input]
  (conj (conj (conj (vector (keyword (subs input 0 1)))
                    (keyword (subs input 1 2)))
              (keyword (subs input 3 4)))
        (keyword (subs input 4))))

(defn get-move-start
  [input]
  (let [user-keys (extract-keys-from-user-input input)]
    (vector (first user-keys) (first (rest user-keys)))))

(get-move-start "1a-2b")

(defn get-move-finish
  [input]
  (let [user-keys (extract-keys-from-user-input input)]
    (vector (first (rest (rest user-keys)))
            (first (rest (rest (rest user-keys)))))))

(defn get-initial-row-as-num
  [input]
  (Integer/parseInt (subs input 0 1)))

(defn get-final-row-as-num
  [input]
  (Integer/parseInt (subs input 3 4)))

(defn get-initial-col-as-str 
  [input]
  (subs input 1 2))

(defn get-final-col-as-str
  [input]
  (subs input 4 5))

(def conversion-map {:A 1 :B 2 :C 3 :D 4 :E 5
                     :a 1 :b 2 :c 3 :d 4 :e 5} )

(defn get-initial-col-as-num
 [input] 
   (get conversion-map (keyword (get-initial-col-as-str input))))

(defn get-final-col-as-num
  [input]
  (get conversion-map (keyword (get-final-col-as-str input))))

(defn middle-keyword [kw1 kw2]
  (let [char1 (first (name kw1))
        char2 (first (name kw2))
        min-char (char (min (int char1) (int char2)))
        middle-char (char (inc (int min-char)))
        middle-keyword (keyword (str middle-char))]
    middle-keyword)) 

(defn middle-number [num1 num2]
  (let [bigger-number (max num1 num2)]
    (dec bigger-number)))
