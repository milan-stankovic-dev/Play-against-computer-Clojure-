(ns seminarski-rad.computer 
  (:require [seminarski-rad.gameplay :as game]
            [seminarski-rad.validator :as val]
            [seminarski-rad.inputUtility :as util]
            [seminarski-rad.board :as board]))

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

(defn possible-moves-for-one-blank
  "For inputted coords of one blank field it returns a vector of strings of possible
   moves for that given blank"
  [[blank-r-key blank-c-key] board player-color]
  (let [moves (get-in board [blank-r-key blank-c-key :moves])
        eats (get-in board [blank-r-key blank-c-key :eats])
        blank-str (str (name blank-r-key) (name blank-c-key))
        solution-vector (filter 
                         (fn [[r c]]
                           (val/validate-input 
                            (util/reverse-input (str (name blank-r-key) 
                                                     (name blank-c-key)
                                                     "-"
                                                     (name r)
                                                     (name c)))
                            board player-color)) (into (vec eats) (vec moves)))]
    (vec (map (fn [[r c]] (str (str (name r) (name c)) "-" blank-str)) 
              solution-vector))))

(def res ( possible-moves-for-one-blank [:3 :C] (board/create-board) "R"))
res

(defn find-all-possible-moves [board player-color]
  (vec (apply concat
              (for [row-key (keys board)
                    col-key (keys (board row-key))
                    :when (= (get-in board [row-key col-key :piece]) " ")]
                (possible-moves-for-one-blank [row-key col-key] board player-color)))))

(find-all-possible-moves (board/create-board) "B")


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
                (let [moves (find-all-possible-moves board k)]
                  (apply min
                         (for [move moves]
                           (minimax (make-move board k move)
                                    (dec depth) false))))))
    ;; else "branch". Here we minimize.
      (reduce min 
              (for [[k v] board
                    :when (= v "R")]
                (let [moves (find-all-possible-moves board k)]
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
       (let [moves (find-all-possible-moves board k)]
         {:position k
          :move (first (filter #(= (minimax (make-move board k %) (dec depth) false)
                                   (minimax board depth true))
                               moves))})))))

(defn computer-move
  [board]
  (let [{:keys [position move]} (find-best-move board)]
    (assoc-in board [position] move)))

