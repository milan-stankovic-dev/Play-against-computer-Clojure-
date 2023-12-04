(ns seminarski-rad.validator 
  (:require [seminarski-rad.input-utility :as utility]
            [clojure.string :as str]))

(defn confirm-validator-Y-N
  "Checks if the confirmation input of user is contained
   in [\"Y\" \"N\"]."
  [input-str]
  (let [purified-input-str (utility/purify-user-input
                            input-str)]
    (some #(= % purified-input-str) ["Y" "N"])))



(defn- input-length-validator 
  "Checks if the user's input is longer or shorter than needed.
   Returns false if it is, true otherwise."
  [input-str length]
  (= (count input-str) length))
    
(defn- input-format-validator
  "Users input must strictly adhere to said form:
   'NL-NL' where N is a number 1-5 and L is a letter
   a-e or A-E."
  [input-str board-size] 
  (let [num-limit board-size
        str-limit-uc (name (utility/num->letter-keyword num-limit))
        str-limit-lc (str/lower-case str-limit-uc)]
    ;; (re-matches #"[1-5][a-eA-E]-[1-5][a-eA-E]" input-str) 
    (re-matches (re-pattern (format "[1-%d][a-%sA-%s]-[1-%d][a-%sA-%s]"
                                    num-limit str-limit-lc str-limit-uc
                                    num-limit str-limit-lc str-limit-uc)) input-str)))

(defn- proper-piece-color-validator
  "Checks if the user is trying to move their own
    piece color or someone elses or a blank field."
  [input-str board player-color]
  (= player-color (get-in board (conj (utility/get-move-start input-str) :piece))))

(defn- start-not-the-same-as-finish-validator 
  "Checks if the starting position of the move is 
   not the same as the finishing one."
  [input-str]
  (not= (utility/get-move-start input-str)
              (utility/get-move-finish input-str)))

(defn- end-not-occupied-validator 
  "Checks if the end location of a move is blank or not.
   Returns true if it's open and false otherwise"
  [input-str board]
  (= (get-in board (conj (utility/get-move-finish input-str) :piece)) " "))

(defn- middle-keyword [kw1 kw2]
  (let [char1 (first (name kw1))
        char2 (first (name kw2))
        min-char (char (min (int char1) (int char2)))
        middle-char (char (inc (int min-char)))
        middle-keyword (keyword (str middle-char))]
    middle-keyword)) 

(defn- check-for-eating-new
  "Checks if there is a valid piece to be eaten by player. If true returns \"eat\",
    otherwise returns false. Updated to work with new board."
  [[row-to-check-keyword col-to-check-keyword] new-board opposite-player-color]
  (if (= opposite-player-color (get-in new-board (conj [row-to-check-keyword
                                                        col-to-check-keyword]
                                                       :piece)))
    "eat" 
        false))

(defn- game-rule-validator 
  "Checks if the rules of the game are validated (ie. if the player is attempting to
   make a legal move that is drawn on the board). If the legal move is able to be made,
   AND the player does not \"eat\", true is returned. If \"eating\" occurs, \"eat\"
   is returned. False otherwise."
  [board purified-input-str player-color]
  (let [first-row-num (utility/get-initial-row-as-num purified-input-str)
        first-col-str (utility/get-initial-col-as-str purified-input-str)
        end-of-move (utility/get-move-finish purified-input-str)
        first-row-keyword (utility/num->keyword first-row-num)
        first-col-keyword (keyword first-col-str)
        opponents-color (utility/opposite-player-color player-color)] 
      (if (some #(= end-of-move %) (get-in board [first-row-keyword
                                                   first-col-keyword :moves]))
         true
         (when (some #(= end-of-move %) (get-in board [first-row-keyword
                                                       first-col-keyword :eats]))
           (check-for-eating-new (utility/calculate-field-to-eat
                                  purified-input-str) board opponents-color)))))

;; (def board-input (seminarski-rad.board/create-board))
;; (game-rule-validator board-input "1A-1C" "B")

(defn validate-input
  [input-str board player-color board-size]
  (let [purified-input-str (utility/purify-user-input input-str)] 
    (and 
     (input-length-validator purified-input-str 5)
     (input-format-validator purified-input-str board-size)
     (proper-piece-color-validator purified-input-str board player-color)
     (start-not-the-same-as-finish-validator purified-input-str)
     (end-not-occupied-validator purified-input-str board)
     (game-rule-validator board purified-input-str player-color))))

;; (validate-input "2c-2c" (board/create-board) "B")

(middle-keyword :A :C)

(defn not-empty?
  [input]
  (not (empty? input)))

(defn user-color-input-validator
  [user-color-input] 
  (let [purified-input-str (utility/purify-user-input user-color-input)]
    (cond
      (empty? purified-input-str) false
      (> (count purified-input-str) 1) false
      (not (some #(= % purified-input-str) ["R" "B"])) false
      :else true)))

(defn number-in-range?
  "Checks if a number is in range of given bounds.
    Both lower and upper bound inclusive."
  [a-number ]
  (if-not (number? a-number)
    nil  
    (<= a-number 5)))




