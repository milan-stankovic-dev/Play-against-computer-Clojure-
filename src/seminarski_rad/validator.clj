(ns seminarski-rad.validator 
  (:require [seminarski-rad.inputUtility :as util]))

(defn- input-length-validator 
  "Checks if the user's input is longer than needed. Since the
    proper input is (ex. 'a1-b1') the proper length will always be 
   5"
  [input-str length]
  (if (not (= (count input-str) length))
     (do (println (str "Your input must contain "
                       length " characters!"))
         false)
     true))

(defn- input-format-validator
  "Users input must strictly adhere to said form:
   'NL-NL' where N is a number 1-5 and L is a letter
   a-e or A-E."
  [input-str]
  (if (re-matches #"[1-5][a-eA-E]-[1-5][a-eA-E]" input-str)
    true
    (do 
      (println "Your input does not adhere to proper input formatting.")
      false)))

(defn- proper-piece-color-validator
  "Checks if the user is trying to move their own
    piece color or someone elses or a blank field."
  [input-str board player-color]
  (if (= player-color (get-in board (util/get-move-start input-str)))
    true
    (do
      (println "You may not be trying to move your own piece.")
      false)))

(defn- start-not-the-same-as-finish-validator 
  "Checks if the starting position of the move is 
   not the same as the finishing one."
  [input-str]
  (if (not (= (util/get-move-start input-str)
              (util/get-move-finish input-str)))
    true
    (do
      (println "Move lengths of 0 are not allowed.")
      false)))

(defn- end-not-occupied-validator 
  "Checks if the end location of a move is blank or not.
   Returns true if it's open and false otherwise"
  [input-str board]
  (if (= (get-in board (util/get-move-finish input-str)) "*")
    true
    (do
      (println "You may not move your piece here. The
                 field is occupied.")
      false)))

(defn- middle-keyword [kw1 kw2]
  (let [char1 (first (name kw1))
        char2 (first (name kw2))
        min-char (char (min (int char1) (int char2)))
        middle-char (char (inc (int min-char)))
        middle-keyword (keyword (str middle-char))]
    middle-keyword)) 

(apply distinct? [1 2 2])

(defn- check-for-eating
  "Checks if there is a valid piece to be eaten by player. If true returns \"eat\",
    otherwise returns false."
  [row-to-check-keyword col-to-check-keyword board opposite-player-color]
  (if (= opposite-player-color (get-in board [ row-to-check-keyword
                                               col-to-check-keyword]))
    "eat"
    (do (println "You don't have an opponent's tile to eat.")
        false)))

(defn- game-logic-validator
  "Checks if the move length is proper for x and y axis. 
   A proper move constitutes one that is at least one tile 
   across, and at most two. If it is the one that is two
   across, the middle tile must be \"eaten\" (removed from the board).
   The only valid tile to be removed is the opponent's tile.
   Returns false if invalid, true if valid and no eat, and 
   \"eat\" if the opponent's piece is eaten."
  [input-str board player-color]
  (let [init-row-number (util/get-initial-row-as-num input-str)
        init-col-string (util/get-initial-col-as-str input-str)
        init-col-number (util/get-initial-col-as-num input-str)
        final-col-string (util/get-final-col-as-str input-str)
        final-col-number (util/get-final-col-as-num input-str)
        final-row-number (util/get-final-row-as-num input-str)
        middle-col-keyword (util/middle-keyword (keyword init-col-string)
                                                (keyword final-col-string))
        middle-row-keyword (keyword (str (util/middle-number init-row-number
                                                             final-row-number))) 
        opposite-player-color (util/opposite-player-color player-color)]
    ;; Checking if the user is trying to jump too far in any direction
    (if (or (> (Math/abs (- init-row-number final-row-number)) 2)
            (> (Math/abs (- init-col-number final-col-number)) 2))
      (do (println "Your move is too long. It must be at least 1 line long and at most 2 lines.")
          false)
      ;; Checking if the user is trying to jump diagonally 1 tile where there are no viable paths
      (if (and (= 1 (Math/abs (- init-row-number final-row-number)))
               (= 1 (Math/abs (- init-col-number final-col-number)))
               (apply distinct? [init-row-number init-col-string final-row-number final-col-string])
               (not= (odd? init-row-number) (odd? init-col-number)))
        (do (println "You may not move here since there is no line on the board.")
            false)
        ;; Checking if the user can eat horizontally, vertically and diagonally.
        (if (= 2 (Math/abs (- init-row-number final-row-number)))
          (if (= init-col-number final-col-number)
            
            (check-for-eating middle-row-keyword (keyword init-col-string) board opposite-player-color)

            (if (= 2 (Math/abs (- init-col-number final-col-number)))
              (if (not= (odd? init-row-number) (odd? init-col-number))
                (do (println "You may not jump to here since there is no line on the board.")
                    false)
                
                (check-for-eating middle-row-keyword middle-col-keyword board opposite-player-color))
              
              false))
          (if (and (= 2 (Math/abs (- init-col-number final-col-number)))
                   (= init-row-number final-row-number))
            
            (check-for-eating (keyword (str init-row-number)) middle-col-keyword board opposite-player-color)
            
            (if (or (and (= 2 (Math/abs (- init-col-number final-col-number)))
                         (= 1 (Math/abs (- init-row-number final-row-number))))
                    (and (= 1 (Math/abs (- init-col-number final-col-number)))
                         (= 2 (Math/abs (- init-row-number final-row-number)))))
              (do (println "You may not move here since there is no line on the board.")
                  false)
              true)))))))

  (game-logic-validator "1A-1C" {:1 {:A "B" :B "B" :C "B" :D "B" :E "B"}
                                 :2 {:A "R" :B "R" :C "B" :D "B" :E "B"}
                                 :3 {:A "B" :B "R" :C "*" :D "R" :E "R"}
                                 :4 {:A "R" :B "R" :C "R" :D "R" :E "R"}
                                 :5 {:A "R" :B "R" :C "R" :D "R" :E "R"}} "B")

(defn validate-input
  [input-str board player-color]
  (let [purified-input-str (util/purify-user-input input-str)] 
    (and (input-length-validator purified-input-str 5)
         (input-format-validator purified-input-str)
         (proper-piece-color-validator purified-input-str board player-color)
         (start-not-the-same-as-finish-validator purified-input-str)
         (end-not-occupied-validator purified-input-str board)
         (game-logic-validator purified-input-str board player-color))))

(middle-keyword :A :C)

(defn user-color-input-validator
  [user-color-input]
  (let [purified-input-str (util/purify-user-input user-color-input)]
    (cond
      (> (count purified-input-str) 1) (println "Input only 1 character.") false
      ((and (not= purified-input-str "B")
            (not= purified-input-str "R"))) (println "Choose R or B.") false
      :else true)))