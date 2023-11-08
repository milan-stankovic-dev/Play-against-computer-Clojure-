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

(get-move-finish "1a-2b")