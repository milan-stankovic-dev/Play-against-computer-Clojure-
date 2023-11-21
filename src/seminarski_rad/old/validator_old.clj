#_{:clj-kondo/ignore [:namespace-name-mismatch]}
(ns seminarski-rad.old.validator-old
  (:require [seminarski-rad.input-utility :as util]))

(defn- check-for-eating-old
  "Checks if there is a valid piece to be eaten by player. If true returns \"eat\",
    otherwise returns false."
  [row-to-check-keyword col-to-check-keyword board opposite-player-color]
  (if (= opposite-player-color (get-in board [row-to-check-keyword
                                              col-to-check-keyword]))
    "eat"
    (do (println "You don't have an opponent's tile to eat.")
        false)))

(defn game-logic-validator-old
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
        (do (println "You may not move here since is no line on the board. there")
            false)
        ;; Checking if the user can eat horizontally, vertically and diagonally.
        (if (= 2 (Math/abs (- init-row-number final-row-number)))
          (if (= init-col-number final-col-number)

            (check-for-eating-old middle-row-keyword (keyword init-col-string) board opposite-player-color)

            (if (= 2 (Math/abs (- init-col-number final-col-number)))
              (if (not= (odd? init-row-number) (odd? init-col-number))
                (do (println "You may not jump to here since there is no line on the board.")
                    false)

                (check-for-eating-old middle-row-keyword middle-col-keyword board opposite-player-color))

              false))
          (if (and (= 2 (Math/abs (- init-col-number final-col-number)))
                   (= init-row-number final-row-number))

            (check-for-eating-old (keyword (str init-row-number)) middle-col-keyword board opposite-player-color)

            (if (or (and (= 2 (Math/abs (- init-col-number final-col-number)))
                         (= 1 (Math/abs (- init-row-number final-row-number))))
                    (and (= 1 (Math/abs (- init-col-number final-col-number)))
                         (= 2 (Math/abs (- init-row-number final-row-number)))))
              (do (println "You may not move here since there is no line on the board.")
                  false)
              true)))))))