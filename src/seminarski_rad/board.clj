(ns seminarski-rad.board
  (:require [seminarski-rad.input-utility :as utility]
            [seminarski-rad.validator :as val]))

(defn game-logic
  "Checks if the move length is proper for x and y axis. 
   A proper move constitutes one that is at least one tile 
   across, and at most two. If it is the one that is two
   across, the middle tile must be \"eaten\" (removed from the board).
   The only valid tile to be removed is the opponent's tile.
   Returns false if invalid, true if valid and no eat, and 
   \"eat\" if the opponent's piece is eaten."
  [purified-input-str]
  (when (and (string? purified-input-str)
             (>= (count purified-input-str) 5)
             (= 4 (count (utility/extract-keys-from-user-input
                          purified-input-str))))
    (let [init-row-number (utility/get-?-row-as-num purified-input-str 1)
          init-col-char (utility/get-?-col-as-char purified-input-str 1)
          init-col-number (utility/get-?-col-as-num purified-input-str 1)
          final-col-char (utility/get-?-col-as-char purified-input-str 2)
          final-col-number (utility/get-?-col-as-num purified-input-str 2)
          final-row-number (utility/get-?-row-as-num purified-input-str 2)]
    ;;Checking if start is not the same as finish
      (when (val/start-not-the-same-as-finish-validator purified-input-str)
    ;; Checking if the user is trying to jump too far in any direction
        (if (or (> (Math/abs (- init-row-number final-row-number)) 2)
                (> (Math/abs (- init-col-number final-col-number)) 2))
          false
      ;; Checking if the user is trying to jump diagonally 1 tile where there are no viable paths
          (if (and (= 1 (Math/abs (- init-row-number final-row-number)))
                   (= 1 (Math/abs (- init-col-number final-col-number)))
                   (apply distinct? [init-row-number init-col-char final-row-number final-col-char])
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
                  true)))))))))

(def empty-node {:piece " " :moves '() :eats '()})

(defn- initialize-empty-board
  "Creates board with empty nodes of given size."
  [board-size]
  (let [numeric-range (range 1 (inc board-size))
        letter-range (utility/numeric-seq->letter-seq
                      numeric-range)]
    (into {} (for [row (map keyword (map str numeric-range))]
               [row (into {} (for [col (map keyword (map str letter-range))]
                               [col empty-node]))]))))
                               
;; ex. col  =>   :A
;; ex. row  =>   :1
(defn- assign-pieces
  "Dynamically assigns pieces to empty board of given size. Upper half
   are blue pieces, bottom half red, and the very center always blank."
  [board board-size]
  (let [numeric-range (range 1 (inc board-size))
        letter-range (into [] (utility/numeric-seq->letter-seq numeric-range))
        letter-range-first-half 
        (utility/?-half-of-vec letter-range 1)
        letter-range-second-half
        (utility/?-half-of-vec letter-range 2)
        middle-row-num (inc (quot board-size 2))]
    (reduce-kv
     (fn [acc row cols]
       (reduce-kv
        (fn [row-acc col node]
          (assoc-in row-acc [row col]
                    (if (or (< middle-row-num (utility/numeric-keyword->num row))
                            (and (= middle-row-num (utility/numeric-keyword->num row))
                                 (.contains letter-range-second-half (first (name col)))))
                      (assoc node :piece "R")
                      (if (or (> middle-row-num (utility/numeric-keyword->num row))
                              (and (= middle-row-num (utility/numeric-keyword->num row))
                                   (.contains letter-range-first-half (first (name col)))))
                        (assoc node :piece "B")
                        node))))
        acc cols))
     {} board)))

(defn- board-hotspot-range
  "Function used for optimizing range checks for valid moves.
   For given coordinate, it returns range in which a check is 
   sensible to be made."
  [coord board-size]
  (let [start (if (< (- coord 2) 1) 1 (- coord 2))
        finish (inc (if (> (+ coord 2) board-size)
                      board-size (+ coord 2)))]
    (range start finish)))

(defn- assign-moves-to-node
  "Assigns moves to one node of given board."
  [board row-kw col-kw board-size]
  (let [row-numeric (utility/numeric-keyword->num row-kw)
        col-str (name col-kw)
        col-numeric (utility/char->number (first col-str))
        validation-result #(game-logic (str row-numeric col-str "-"
                                            (name %1)
                                            (name %2)))
        row-range-num (board-hotspot-range row-numeric board-size)
        col-range-num (board-hotspot-range col-numeric board-size)
        row-range-final (vec (utility/numeric-seq->numeric-keyword-seq
                              row-range-num))
        col-range-final (vec (utility/numeric-seq->letter-keyword-seq
                              col-range-num))

        moves (for [r row-range-final
                    c col-range-final
                    :when (and (validation-result r c)
                               (not= (validation-result r c) "eat"))]
                [r c])
        eats (for [r row-range-final
                   c col-range-final
                   :when (= (validation-result r c) "eat")]
               [r c])]
    (update-in board [row-kw col-kw]
               (fn [node]
                 (assoc node :eats eats :moves moves)))))

(defn- assign-all-moves
  "Utilizes the 'assign-moves-to-node' function to assign moves
   to all nodes of given board and board-size."
  [board board-size]
  (reduce-kv
   (fn [acc row cols]
     (reduce-kv
      (fn [row-acc col _]
        (assign-moves-to-node row-acc row col board-size))
      acc cols))
   board board))

(defn create-board
  "Puts together a fully fleshed out board with proper pieces, moves and 
   eats assigned."
  [board-size]
  (let [adjusted-size (utility/adjust-board-size board-size)]
    (assign-all-moves (assign-pieces (initialize-empty-board adjusted-size)
                                     adjusted-size) adjusted-size)))

(defn- print-slants
  "Used for displaying available moves to user as ASCII art."
  [board-size pattern]
  (print "   ")
  (doseq [_ (range (/ (- board-size 1) 2))]
    (print pattern))
  (println "|"))

(defn- print-row
  "Prints one row for given board of given size."
  [board-size row-keyword board]
  (let [numeric-sequence (range 1 board-size)
        letter-range-keywords
        (utility/numeric-seq->letter-keyword-seq
         numeric-sequence)
        last-col-keyword (utility/num->letter-keyword board-size)]
    (print (str (name row-keyword) "  "))
    (doseq [col letter-range-keywords]
      (print (str (get-in board [row-keyword col :piece])) "─ "))
    (println (get-in board [row-keyword last-col-keyword :piece]))))

(defn print-the-board
  "Prints the entire board to user with available moves displayed as ASCII art."
  [board board-size]
  (println)
  (print "   ")
  (doseq [row-num (utility/numeric-seq->letter-seq
                   (range 1 (inc board-size)))]
    (print (str row-num "   ")))
  (println)
  (println)
  (let [numeric-seq (range 1 board-size)
        row-keyword-seq (utility/seq->keyword-seq
                         numeric-seq)
        last-row-keyword (utility/num->keyword board-size)]
    (doseq [row-keyword row-keyword-seq]
      (print-row board-size row-keyword board)
      (if (odd? (utility/numeric-keyword->num row-keyword))
        (print-slants board-size "| \\ | / ")
        (print-slants board-size "| / | \\ ")))
    (print-row board-size last-row-keyword board)))

;; (print-the-board (create-board 13) 13)
;; (create-board 7)
