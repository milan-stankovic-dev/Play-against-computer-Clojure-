(ns seminarski-rad.computer 
  (:require [seminarski-rad.gameplay :as game]))

(defn game-over?
  [board]
  (or (game/check-for-win "R" "B" board)
      (game/check-for-win "B" "R" board)))

(defn win-numeric
  [board]
  (if (game/check-for-win "R" "B" board)
    ;; Here B wins
    -1
    (if (game/check-for-win "B" "R" board)
    ;; Here R wins
      1
      0)))

(defn generate-possible-moves
  [board position]
;; TODO create this.
  )

(defn make-move
  [board position move]
;; TODO create this too.
  )

(defn minimax
  "Minimax algorithm for determining the best move. 
   Depending on whether we are minimizing or maximizing,
   "
  [board depth maximizing?]
  (if (or (= depth 0) (game-over? board))
    (win-numeric board)
    (if maximizing?
    ;; searching for maximization purposes
      (reduce max
              (for [[k v] board
                    :when (= v "B")]
                (let [moves (generate-possible-moves board k)]
                  (apply min
                         (for [move moves]
                           (minimax (make-move board k move)
                                    (dec depth) false))))))
    ;; else "branch". Here we minimize.
      (reduce min 
              (for [[k v] board
                    :when (= v "R")]
                (let [moves (generate-possible-moves board k)]
                  (apply max
                         (for [move moves]
                           (minimax (make-move board k move)
                                    (dec depth) true)))))))))

(defn find-best-move
  [board]
  (let [depth 3]
    (first
     (for [[k v] board
           :when (= v "B")]
       (let [moves (generate-possible-moves board k)]
         {:position k
          :move (first (filter #(= (minimax (make-move board k %) (dec depth) false)
                                   (minimax board depth true))
                               moves))})))))

(defn computer-move
  [board]
  (let [{:keys [position move]} (find-best-move board)]
    (assoc-in board [position] move)))

