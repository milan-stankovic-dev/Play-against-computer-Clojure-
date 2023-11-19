(ns seminarski-rad.computer
  (:require [seminarski-rad.gameplay :as game]
            [seminarski-rad.validator :as val]
            [seminarski-rad.inputUtility :as util]
            [seminarski-rad.board :as board]
            [clojure.string :as string]
            [clojure.set :as set]))

(defn win-numeric
  [board computer-color]
  (let [human-color (util/opposite-player-color computer-color)
        win-check-result (game/check-for-win human-color computer-color board)]
    (case win-check-result
      "HUMAN" -1
      "COMPUTER" 1
      false 0
      :default 0)))

(win-numeric (board/create-board) "R")

(board/create-board)

(defn add-suffix-eats-to-eating-keyword
  "Takes in a vector of row and column keywords and transforms it into a vector
   input which has '-EATS' appended to the col keyword."
  [[row col]]
  (vec [row (keyword (str (name col) "-EAT"))]))

(add-suffix-eats-to-eating-keyword [:3 :C])

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
                            board player-color)) 
                         (into moves (map add-suffix-eats-to-eating-keyword eats)))]
    (vec (map (fn [[r c]] (str (str (name r) (name c)) "-" blank-str))
              solution-vector))))

(defn find-all-possible-moves [board player-color]
  (vec (apply concat
              (for [row-key (keys board)
                    col-key (keys (board row-key))
                    :when (= (get-in board [row-key col-key :piece]) " ")]
                (possible-moves-for-one-blank [row-key col-key] board player-color)))))

(find-all-possible-moves (board/create-board) "B")
 

(defn minimax
  "Minimax algorithm helps us determine the move computer should make against
    the player. It is a heuristic and as such may not always give the best
    answer. We minimize for the human and maximize for the computer."
  [board depth maximizing? computer-color]
  (let [human-color (util/opposite-player-color computer-color)]
    (if (or (= depth 0) (game/check-for-win human-color computer-color board))
      (win-numeric board computer-color)
      (if maximizing?
        ;; Maximizing algorithm for the computer
        (let [all-moves (find-all-possible-moves board computer-color)
              moves-with-eat (take-while #(string/includes? "EAT" %)
                                         all-moves)
              moves-without-eat (into '() (set/difference (set all-moves) (set moves-with-eat)))]
          (if (seq moves-with-eat)
            (reduce max (for [move moves-with-eat]
                          (minimax (game/move-piece-clean move board computer-color)
                                   (dec depth) false computer-color )))
            (reduce max (for [move moves-without-eat]
                          (minimax (game/move-piece-clean move board computer-color)
                                   (dec depth) false computer-color)))))
        ;; Minimizing algorithm for the player
        (let [all-moves (find-all-possible-moves board human-color)
              moves-with-eat (take-while #(string/includes? "EAT" %)
                                         all-moves)
              moves-without-eat (into '() (set/difference (set all-moves) (set moves-with-eat)))]
          (if (seq moves-with-eat)
            (reduce min (for [move moves-with-eat]
                          (minimax (game/move-piece-clean move board human-color)
                                   (dec depth) true computer-color)))
            (reduce min (for [move moves-without-eat]
                          (minimax (game/move-piece-clean move board human-color)
                                   (dec depth) true computer-color)))))))))

(minimax (board/create-board) 3 true "R")

(defn find-best-move
  "Find the best move for the computer using the minimax algorithm."
  [board depth computer-color]
  (let [all-moves (find-all-possible-moves board computer-color)
        moves-with-eat (take-while #(string/includes? "EAT" %) all-moves)
        moves-without-eat (into '() (set/difference (set all-moves) (set moves-with-eat)))
        moves (into moves-without-eat moves-with-eat)]
    (if (seq moves)
      (reduce (fn [best-move current-move]
                (let [score (minimax (game/move-piece-clean current-move board computer-color)
                                     (dec depth) false computer-color)]
                  (if (or (nil? best-move) (and (nil? (best-move 1)) (< (score 0) (best-move 0))))
                    [score current-move]
                    best-move)))
              nil
              moves)
      nil)))

(let [initial-board (board/create-board)
      best-move (find-best-move initial-board 3 "R")]
  (println "Best Move:" best-move))
