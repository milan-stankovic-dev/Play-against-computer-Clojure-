(ns seminarski-rad.board
  (:require [seminarski-rad.input-utility :as utility]))

(defn game-logic
  "Checks if the move length is proper for x and y axis. 
   A proper move constitutes one that is at least one tile 
   across, and at most two. If it is the one that is two
   across, the middle tile must be \"eaten\" (removed from the board).
   The only valid tile to be removed is the opponent's tile.
   Returns false if invalid, true if valid and no eat, and 
   \"eat\" if the opponent's piece is eaten."
  [input-str]
  (let [init-row-number (utility/get-initial-row-as-num input-str)
        init-col-string (utility/get-initial-col-as-str input-str)
        init-col-number (utility/get-initial-col-as-num input-str)
        final-col-string (utility/get-final-col-as-str input-str)
        final-col-number (utility/get-final-col-as-num input-str)
        final-row-number (utility/get-final-row-as-num input-str)]
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

(defn initialize-empty-board 
  [size] 
  (let [numeric-range (range 1 (inc size))
        letter-range (utility/numeric-seq->letter-seq
                      numeric-range)]
    (into {} (for [row (map keyword (map str numeric-range))]
               [row (into {} (for [col (map keyword letter-range)]
                               [col empty-node]))]))))

(def empty-board (initialize-empty-board 9))
empty-board

;; ex. col  =>   :A
;; ex. row  =>   :1
(defn assign-pieces
  [board size]
  (let [numeric-range (range 1 (inc size))
        letter-range (into [] (utility/numeric-seq->letter-seq numeric-range))
        letter-range-first-half
        (into [] (utility/?-half-of-seq letter-range 1))
        letter-range-second-half
        (into [] (utility/?-half-of-seq letter-range 2))
        middle-row-num (inc (quot size 2))]
    (reduce-kv
     (fn [acc row cols]
       (reduce-kv
        (fn [row-acc col node]
          (assoc-in row-acc [row col]
                    (if (or (< middle-row-num (utility/numeric-keyword->num row))
                            (and (= middle-row-num (utility/numeric-keyword->num row))
                                 (.contains letter-range-second-half (name col))))
                      (assoc node :piece "R")
                      (if (or (> middle-row-num (utility/numeric-keyword->num row))
                              (and (= middle-row-num (utility/numeric-keyword->num row))
                                   (.contains letter-range-first-half (name col))))
                        (assoc node :piece "B")
                        node))))
        acc cols))
     {} board)))


(def assigned-pieces-board (assign-pieces empty-board 9))
assigned-pieces-board
(defn assign-moves-to-node [board row col size]
  (let [validation-result #(game-logic (str(utility/numeric-keyword->num row)
                                                          (name col) "-"
                                                          (name %1)
                                                          (name %2)))
        numeric-range (range 1 (inc size))
        letter-range (utility/numeric-seq->letter-seq numeric-range)
        moves (for [r (map keyword (map str numeric-range))
                    c (map keyword letter-range)
                    :when (and (validation-result r c)
                               (not= (validation-result r c) "eat"))]
                [r c])
        eats (for [r (map keyword (map str numeric-range))
                   c (map keyword letter-range)
                   :when (= (validation-result r c) "eat")]
               [r c])]
    (update-in board [row col]
               (fn [node]
                 (assoc node :eats eats :moves moves)))))

(defn assign-all-moves [board size]
  (reduce-kv
   (fn [acc row cols]
     (reduce-kv
      (fn [row-acc col _]
        (assign-moves-to-node row-acc row col size))
      acc cols))
   board board))

(defn create-board
  [size]
  (assign-all-moves (assign-pieces (initialize-empty-board size) size) size))

(create-board 5)

(defn- print-slants
  [size pattern]
  (print "   ")
  (doseq [_ (range (/ (- size 1) 2))]
    (print pattern))
  (println "|"))

(print-slants 9 "| \\ | / ")

(defn- print-row
  [size row-keyword board]
  (let [numeric-sequence (range 1 size) 
        letter-range-keywords 
        (utility/numeric-seq->letter-keyword-seq 
         numeric-sequence)
        last-col-keyword (utility/num->letter-keyword
                          utility/conversion-map size)]
    (print (str (name row-keyword) "  "))
    (doseq [col letter-range-keywords] 
      (print (str (get-in board [row-keyword col :piece])) "─ "))
    (println (get-in board [row-keyword last-col-keyword :piece]))))

(apply keyword (utility/numeric-seq->letter-seq (range 1 (inc 5))))
(defn print-the-board
  [board size]
  (println)
 (print "   ")
  (doseq [num (utility/numeric-seq->letter-seq 
               (range 1 (inc size)))]
    (print (str num "   ")))
  (println)
  (println)
  (let [numeric-seq (range 1 size)
        row-keyword-seq (utility/seq->keyword-seq
                         numeric-seq)
        last-row-keyword (utility/num->keyword size)]
    (doseq [row-keyword row-keyword-seq]
      (print-row size row-keyword board)
      (if (odd? (utility/numeric-keyword->num row-keyword))
        (print-slants size "| \\ | / ")
        (print-slants size "| / | \\ ")))
    (print-row size last-row-keyword board)))

(print-the-board (create-board 9) 9)
(create-board 9)