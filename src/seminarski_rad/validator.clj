(ns seminarski-rad.validator 
  (:require [seminarski-rad.inputUtility :as util]
            [seminarski-rad.board :as board]))

(defn- quit?
  "Checks if the user is trying to quit. If so, then it ends game 
   process."
  [purified-input-str]
  (when (= purified-input-str "Q")
      (System/exit 0)))

(defn- input-length-validator 
  "Checks if the user's input is longer than needed. Since the
    proper input is (ex. 'a1-b1') the proper length will always be 
   5"
  [input-str length]
  (= (count input-str) length))
    
(defn- input-format-validator
  "Users input must strictly adhere to said form:
   'NL-NL' where N is a number 1-5 and L is a letter
   a-e or A-E."
  [input-str]
  (re-matches #"[1-5][a-eA-E]-[1-5][a-eA-E]" input-str))

(defn- proper-piece-color-validator
  "Checks if the user is trying to move their own
    piece color or someone elses or a blank field."
  [input-str board player-color]
  (= player-color (get-in board (conj (util/get-move-start input-str) :piece))))

(defn- start-not-the-same-as-finish-validator 
  "Checks if the starting position of the move is 
   not the same as the finishing one."
  [input-str]
  (not= (util/get-move-start input-str)
              (util/get-move-finish input-str)))

(defn- end-not-occupied-validator 
  "Checks if the end location of a move is blank or not.
   Returns true if it's open and false otherwise"
  [input-str board]
  (= (get-in board (conj (util/get-move-finish input-str) :piece)) " "))

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

(util/calculate-field-to-eat "3C-5E")
(defn- game-rule-validator 
  "Checks if the rules of the game are validated (ie. if the player is attempting to
   make a legal move that is drawn on the board). If the legal move is able to be made,
   AND the player does not \"eat\", true is returned. If \"eating\" occurs, \"eat\"
   is returned. False otherwise."
  [board purified-input-str player-color]
  (let [first-row-num (util/get-initial-row-as-num purified-input-str)
        first-col-str (util/get-initial-col-as-str purified-input-str)
        end-of-move (util/get-move-finish purified-input-str)
        first-row-keyword (util/num->keyword first-row-num)
        first-col-keyword (keyword first-col-str)
        opponents-color (util/opposite-player-color player-color)] 
      (if (some #(= end-of-move %) (get-in board [first-row-keyword
                                                   first-col-keyword :moves]))
         true
         (when (some #(= end-of-move %) (get-in board [first-row-keyword
                                                       first-col-keyword :eats]))
           (check-for-eating-new (util/calculate-field-to-eat
                                  purified-input-str) board opponents-color)))))

(def board-input (seminarski-rad.board/create-board))
(game-rule-validator board-input "1A-1C" "B")

(defn validate-input
  [input-str board player-color]
  (let [purified-input-str (util/purify-user-input input-str)] 
    (and 
     (quit? purified-input-str)
     (input-length-validator purified-input-str 5)
     (input-format-validator purified-input-str)
     (proper-piece-color-validator purified-input-str board player-color)
     (start-not-the-same-as-finish-validator purified-input-str)
     (end-not-occupied-validator purified-input-str board)
     (game-rule-validator board purified-input-str player-color))))

(validate-input "2c-2c" (board/create-board) "B")

(middle-keyword :A :C)

(defn user-color-input-validator
  [user-color-input]
  (let [purified-input-str (util/purify-user-input user-color-input)]
    (cond
      (> (count purified-input-str) 1) (println "Input only 1 character.") false
      ((and (not= purified-input-str "B")
            (not= purified-input-str "R"))) (println "Choose R or B.") false
      :else true)))

