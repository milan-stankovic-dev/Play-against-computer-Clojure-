(ns seminarski-rad.board
  (:require [seminarski-rad.inputUtility :as util]))

(defn game-logic
  "Checks if the move length is proper for x and y axis. 
   A proper move constitutes one that is at least one tile 
   across, and at most two. If it is the one that is two
   across, the middle tile must be \"eaten\" (removed from the board).
   The only valid tile to be removed is the opponent's tile.
   Returns false if invalid, true if valid and no eat, and 
   \"eat\" if the opponent's piece is eaten."
  [input-str]
  (let [init-row-number (util/get-initial-row-as-num input-str)
        init-col-string (util/get-initial-col-as-str input-str)
        init-col-number (util/get-initial-col-as-num input-str)
        final-col-string (util/get-final-col-as-str input-str)
        final-col-number (util/get-final-col-as-num input-str)
        final-row-number (util/get-final-row-as-num input-str)
        middle-col-keyword (util/middle-keyword (keyword init-col-string)
                                                (keyword final-col-string))
        middle-row-keyword (keyword (str (util/middle-number init-row-number
                                                             final-row-number)))]
    ;; Checking if the user is trying to jump too far in any direction
    (if (or (> (Math/abs (- init-row-number final-row-number)) 2)
            (> (Math/abs (- init-col-number final-col-number)) 2))
      false
      ;; Checking if the user is trying to jump diagonally 1 tile where there are no viable paths
      (if (and (= 1 (Math/abs (- init-row-number final-row-number)))
               (= 1 (Math/abs (- init-col-number final-col-number)))
               (apply distinct? [init-row-number init-col-string final-row-number final-col-string])
               (not= (odd? init-row-number) (odd? init-col-number)))
        false
        ;; Checking if the user can eat horizontally, vertically and diagonally.
        (if (= 2 (Math/abs (- init-row-number final-row-number)))
          (if (= init-col-number final-col-number)

            "eat";; (check-for-eating-new middle-row-keyword (keyword init-col-string) board opposite-player-color)

            (if (= 2 (Math/abs (- init-col-number final-col-number)))
              (if (not= (odd? init-row-number) (odd? init-col-number))
                false

                "eat";; (check-for-eating-new middle-row-keyword middle-col-keyword board opposite-player-color))
                )
              false))
          (if (and (= 2 (Math/abs (- init-col-number final-col-number)))
                   (= init-row-number final-row-number))

            "eat";; (check-for-eating-new (keyword (str init-row-number)) middle-col-keyword board opposite-player-color)

            (if (or (and (= 2 (Math/abs (- init-col-number final-col-number)))
                         (= 1 (Math/abs (- init-row-number final-row-number))))
                    (and (= 1 (Math/abs (- init-col-number final-col-number)))
                         (= 2 (Math/abs (- init-row-number final-row-number)))))
              false
              true)))))))

(def empty-node {:piece " " :moves '() :eats '()})

(defn initialize-empty-board [] 
    (into {} (for [row (map keyword (map str (range 1 6)))]
               [row (into {} (for [col (map keyword '("A" "B" "C" "D" "E"))]
                               [col empty-node]))])))

(def empty-board (initialize-empty-board))
empty-board

;; ex. col  =>   :A
;; ex. row  =>   :1
(defn assign-pieces [board]
  (reduce-kv 
    (fn [acc row cols]
        (reduce-kv
         (fn [row-acc col node]
           (assoc-in row-acc [row col]
                     (if (or (< 3 (util/numeric-keyword->num row))
                              (and (= 3 (util/numeric-keyword->num row))
                                   (.contains ["D" "E"] (name col))))
                       (assoc node :piece "R")
                       (if (or (> 3 (util/numeric-keyword->num row))
                               (and (= 3 (util/numeric-keyword->num row))
                                    (.contains ["A" "B"] (name col))))
                         (assoc node :piece "B")
                         node))))
         acc cols))
      {} board))

(def assigned-pieces-board (assign-pieces empty-board))
assigned-pieces-board
(defn assign-moves-to-node [board row col]
  (let [validation-result #(game-logic (str(util/numeric-keyword->num row)
                                                          (name col) "-"
                                                          (name %1)
                                                          (name %2)))
        moves (for [r (map keyword (map str (range 1 6)))
                    c (map keyword '("A" "B" "C" "D" "E"))
                    :when (and (validation-result r c)
                               (not= (validation-result r c) "eat"))]
                [r c])
        eats (for [r (map keyword (map str (range 1 6)))
                   c (map keyword '("A" "B" "C" "D" "E"))
                   :when (= (validation-result r c) "eat")]
               [r c])]
    (update-in board [row col]
               (fn [node]
                 (assoc node :eats eats :moves moves)))))

(defn assign-all-moves [board ]
  (reduce-kv
   (fn [acc row cols]
     (reduce-kv
      (fn [row-acc col _]
        (assign-moves-to-node row-acc row col))
      acc cols))
   board board))

(defn create-board
  []
  (assign-all-moves (assign-pieces (initialize-empty-board))))

(create-board)