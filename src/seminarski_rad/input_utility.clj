(ns seminarski-rad.input-utility
  (:require [clojure.set :as set]
            [clojure.string :as str]))

(defn purify-user-input
  "Removes unnecessary blank characters and capitalizes 
   all letters in input. If input contains \"EAT\" in the
   middle of it,function removes it."
  [input]
  (let [first-step (str/upper-case (str/trim input))] 
      (if (and (= (count first-step) 9)
               (clojure.string/includes? first-step "EAT"))
        (str (subs first-step 0 3) (subs first-step 7))
        first-step)))

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

(def conversion-map {:A 1 :B 2 :C 3 :D 4 :E 5})

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

(defn opposite-player-color
  [color]
  (if (= color "R")
    "B"
    "R"))


(defn midvalue-num
  "Returns the middle value of two numbers as a keyword.
   Best used with integers with odd sized gaps between the values.
   E.g. 3->5 is good because there is 1 (odd) number between
        3->6 is not so good because there are two numbers 
   between them (4 and 5) so the result will be 9/2"
  [num1 num2]
  (/ (+ num1 num2) 2))

(defn num->keyword
  [num]
  (keyword (str num)))

(defn midvalue-str->keyword
  [str1 str2]
  (let [num1 (conversion-map (keyword str1))
        num2 (conversion-map (keyword str2))
        midvalue-num (midvalue-num num1 num2)]
     ((set/map-invert conversion-map)
          midvalue-num)))

(midvalue-str->keyword "A" "C")

(defn keyword->str
  [keyword]
  (name keyword))

(defn numeric-keyword->num
  [a-keyword]
  (Integer/parseInt (name a-keyword)))

(defn calculate-field-to-eat
  [purified-input-str] ;e.g. "1A-3C"
  (let [init-row-num (get-initial-row-as-num purified-input-str)
        init-col-str (get-initial-col-as-str purified-input-str)
        final-row-num (get-final-row-as-num purified-input-str)
        final-col-str (get-final-col-as-str purified-input-str)]
    (if (distinct? init-row-num
                   init-col-str
                   final-row-num
                   final-col-str)
      (vector (num->keyword (midvalue-num init-row-num
                                          final-row-num))
              (midvalue-str->keyword init-col-str
                                                   final-col-str))
      (if (= init-row-num final-row-num)
        (vector (num->keyword init-row-num)
                (midvalue-str->keyword init-col-str
                                                     final-col-str))
        (vector (num->keyword (midvalue-num init-row-num final-row-num))
                (keyword init-col-str))))))

(calculate-field-to-eat "3C-3A")

(defn reverse-input
  "Does a semantic reverse, where the end of a move goes first, then the 
   beginning. DOES NOT: do a normal string reversed"
  [purified-input-str]
  (let [[f l] (.split purified-input-str "-")]
    (str l "-" f)))

(defn take-user-input-move
  []
  (println "Your move:")
  (let [user-input (read-line)]
    (println "You entered: " user-input)
    user-input))