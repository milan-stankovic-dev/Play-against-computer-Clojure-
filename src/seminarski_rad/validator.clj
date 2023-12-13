(ns seminarski-rad.validator 
  (:require [seminarski-rad.input-utility :as utility]))

(defn confirm-validator-Y-N
  "Checks if the confirmation input of user is contained
   in [\"Y\" \"N\"]."
  [input-str]
  (when (string? input-str)
    (let [purified-input-str (utility/purify-user-input
                              input-str)]
      (some #(= % purified-input-str) ["Y" "N"]))))

(defn allow-?-extras
  "Takes in adjusted board size and returns how many 
   extra digits are allowed to be in input (if the 
   board size is less than 10, it allows no extras,
   10 and more it allows 1 extra etc.)."
  [adj-board-size]
  (dec (count (str adj-board-size))))

(defn- input-length-validator 
  "Checks if the user's input is longer or shorter than needed.
   Returns false if it is, true otherwise."
  [input-str adj-board-size]
  (let [input-len (count input-str)]
    (and (>= input-len 5)
         (<= input-len (+ 5 (* 2 
                               (allow-?-extras
                                adj-board-size)))))))

(defn- input-format-validator
  "Users input must strictly adhere to said form:
   'NL-NL' where N is a number 1-(board size) and L is a letter
   a-(board size as lowercase alpha) or A-(board size as uppercase
   alpha)."
  [input-str board-size] 
        (let [input-keys (utility/extract-keys-from-user-input input-str)] 
          (when (and (= 4 (count input-keys))
                     (re-matches #".*-.+" input-str))
               (let [first-row-num (utility/get-?-row-as-num input-str 1)
                     second-row-num (utility/get-?-row-as-num input-str 2)
                     first-col-char (utility/get-?-col-as-char input-str 1)
                     second-col-char (utility/get-?-col-as-char input-str 2)
                     num-range (range 1 board-size)
                     char-range (utility/numeric-seq->letter-seq 
                                 num-range)]
                 (utility/numeric-string? (name (first input-keys)))
                 (utility/numeric-string? (name (nth input-keys 2)))
                 (and
                  (some #(= % first-row-num) num-range)
                  (some #(= % second-row-num) num-range)
                  (some #(= % first-col-char) char-range)
                  (some #(= % second-col-char) char-range))))))

  (defn- proper-piece-color-validator
    "Checks if the user is trying to move their own
    piece color or someone elses or a blank field."
    [input-str board player-color]
    (= player-color (get-in board (conj (utility/move-?-coordinate input-str 1) 
                                        :piece))))

(defn start-not-the-same-as-finish-validator 
  "Checks if the starting position of the move is 
   not the same as the finishing one."
  [input-str]
  (not= (utility/move-?-coordinate input-str 1)
              (utility/move-?-coordinate input-str 2)))

(defn- end-not-occupied-validator 
  "Checks if the end location of a move is blank or not.
   Returns true if it's open and false otherwise"
  [input-str board]
  (= (get-in board (conj (utility/move-?-coordinate input-str 2) :piece)) " "))

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
  (let [first-row-num (utility/get-?-row-as-num purified-input-str 1)
        first-col-str (str (utility/get-?-col-as-char purified-input-str 1))
        end-of-move (utility/move-?-coordinate purified-input-str 2)
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

(defn validate-input
  [input-str board player-color board-size]
  (let [purified-input-str (utility/purify-move-input input-str
                                                      board-size)]
    (and 
     (input-length-validator purified-input-str board-size)
     (input-format-validator purified-input-str board-size)
     (proper-piece-color-validator purified-input-str board player-color)
     (start-not-the-same-as-finish-validator purified-input-str)
     (end-not-occupied-validator purified-input-str board)
     (game-rule-validator board purified-input-str player-color))))

(defn not-empty?
  [input]
  #_{:clj-kondo/ignore [:not-empty?]}
  (not (empty? input)))

(defn user-color-input-validator
  [user-color-input] 
  (let [purified-input-str (utility/purify-user-input user-color-input)]
    (cond
      (not (string? purified-input-str)) false
      (empty? purified-input-str) false
      (> (count purified-input-str) 1) false
      (not (some #(= % purified-input-str) ["R" "B"])) false
      :else true)))
