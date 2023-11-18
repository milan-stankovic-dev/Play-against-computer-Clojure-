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

(defn generate-possible-moves
  [board player-color]
  (let [valid-inputs (for [start-row (keys board)
                           start-col (keys (board start-row))
                           :let [start-keyword (keyword start-row)
                                 end-keyword (keyword start-col)
                                 input-str (str start-keyword (name start-col) "-" end-keyword)]
                           :when (val/validate-input input-str board player-color)]
                       input-str)]
    (concat (filter #(= (val/validate-input % board player-color) "eat") valid-inputs)
            (filter #(not= (val/validate-input % board player-color) "eat") valid-inputs))))

(defn possible-moves-for-one-blank
  [[row-key col-key] board player-color]
  (let [moves (get-in board [row-key col-key :moves])]
    (filter (fn [[r c]](val/validate-input
                       (util/reverse-input (str (name row-key)
                                                (name col-key)
                                                "-"
                                                (name r)
                                                (name c)))
                       board player-color)) moves)))

(def res ( possible-moves-for-one-blank [:3 :C] (board/create-board) "R"))

(keys game/board)

(println (generate-possible-moves game/board "R"))


(conj [1 2 3] 0)
(let [possible-moves (generate-possible-moves
                      {:1 {:A "B" :B "B" :C "B" :D "B" :E "B"}
                       :2 {:A "B" :B "B" :C "*" :D "B" :E "B"}
                       :3 {:A "B" :B "B" :C "B" :D "R" :E "R"}
                       :4 {:A "R" :B "R" :C "R" :D "R" :E "R"}
                       :5 {:A "R" :B "R" :C "R" :D "R" :E "R"}}
                      "B")]
  (println "Possible Moves:" possible-moves))

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

