(ns seminarski-rad.computer
  (:require [seminarski-rad.validator :as val]
            [seminarski-rad.input-utility :as utility]
            [seminarski-rad.board :as board]
            [clojure.string :as string]
            [clojure.set :as set]))

;; **************** MOVED FROM GAMEPLAY.CLJ ***************

(def wins (atom {"HUMAN" 0 "COMPUTER" 0}))
@wins

(defn print-the-score
  []
  (println "\nHuman Score: " (get @wins "HUMAN"))
  (println "Computer Score: " (get @wins "COMPUTER"))
  (println))

(defn check-for-win
  "Returns \"HUMAN\" if human won, \"COMPUTER\" if computer
   won, false otherwise. Note: both \"HUMAN\" and \"COMPUTER\" are truthy:
   can be checked for truthiness if only win state is of concern."
  [human-color computer-color board]
  (let [human-piece-fields (filter #(= human-color (:piece %))
                                   (for [row (vals board) col (vals row)] col))
        computer-piece-fields (filter #(= computer-color (:piece %))
                                      (for [row (vals board) col (vals row)] col))]
    (if (and (empty? human-piece-fields) (seq computer-piece-fields))
      (do
        (println (str "Computer wins!" "[" computer-color "]"))
        (swap! wins #(update-in % ["COMPUTER"] (fnil inc 0)))
        (print-the-score)
        "COMPUTER")
      (if (and (empty? computer-piece-fields) (seq human-piece-fields))
        (do
          (println (str "Human wins!" "[" human-color "]"))
          (swap! wins #(update-in % ["HUMAN"] (fnil inc 0)))
          (print-the-score)
          "HUMAN")
        false))))

(check-for-win "B" "R" (board/create-board))
(board/create-board)
(filter #(= " " (:piece %))
        (for [row (vals (board/create-board))
              col (vals row)]
          col))
(defn move-piece-eaten-indicator
  "Returns board with moved piece if the piece was moved 1 tile or
   a vector with edited board and the word \"eaten\" inside it 
   if the user has eaten a piece."
  [user-input board user-color]
  (let [validation-result (val/validate-input user-input board user-color)]
    (if-not validation-result
      (move-piece-eaten-indicator (utility/take-user-input-move) board user-color)
      (let [purified-input-str (utility/purify-user-input user-input)
            move-start (utility/get-move-start purified-input-str)
            move-finish (utility/get-move-finish purified-input-str)
            move-done-board (assoc-in (assoc-in board (conj move-finish :piece) user-color)
                                      (conj move-start :piece) " ")]
        (if (= validation-result "eat")
          (let [move-done-eaten (assoc-in move-done-board
                                          (conj (utility/calculate-field-to-eat
                                                 purified-input-str) :piece) " ")]
            (do
                        ;; (swap! current-game-score
                        ;;        #(update-in % [player :score] inc))
              [move-done-eaten "eaten"]))
          move-done-board)))))

(defn move-piece-clean
  "Returns board after move, no caveats or indications of eating or not.
    If move is improper, returns board as is."
  [user-input board user-color]
  (if-not (val/validate-input user-input board user-color)
    board
    (let [move-piece-res (move-piece-eaten-indicator
                          user-input board user-color)]
      (if (vector? move-piece-res)
        (first move-piece-res)
        move-piece-res))))
;; ********************************************************

(defn- win-numeric
  [board computer-color]
  (let [human-color (utility/opposite-player-color computer-color)
        win-check-result (check-for-win human-color computer-color board)]
    (case win-check-result
      "HUMAN" -1
      "COMPUTER" 1
      false 0
      :default 0)))

(defn- add-suffix-eats-to-eating-keyword
  "Takes in a vector of row and column keywords and transforms it into a vector
   input which has '-EATS' appended to the col keyword."
  [[row col]]
  (vec [row (keyword (str (name col) "-EAT"))]))

(add-suffix-eats-to-eating-keyword [:3 :C])

(defn- possible-moves-for-one-blank
  "For inputted coords of one blank field it returns a vector of strings of possible
   moves for that given blank"
  [[blank-r-key blank-c-key] board player-color]
  (let [moves (get-in board [blank-r-key blank-c-key :moves])
        eats (get-in board [blank-r-key blank-c-key :eats])
        blank-str (str (name blank-r-key) (name blank-c-key))
        solution-vector (filter
                         (fn [[r c]]
                           (val/validate-input
                            (utility/reverse-input (str (name blank-r-key)
                                                     (name blank-c-key)
                                                     "-"
                                                     (name r)
                                                     (name c)))
                            board player-color)) 
                         (into moves (map add-suffix-eats-to-eating-keyword eats)))]
    (set (map (fn [[r c]] (str (str (name r) (name c)) "-" blank-str))
              solution-vector))))

(defn- find-all-possible-moves [board player-color]
  (set (apply concat
              (for [row-key (keys board)
                    col-key (keys (board row-key))
                    :when (= (get-in board [row-key col-key :piece]) " ")]
                (possible-moves-for-one-blank [row-key col-key] board player-color)))))

(defn- find-best-scenario-min-max
  "Helper function that uses the minimax algorithm to minimize/maximize the best/worst score 
   a human/computer can get as defined by the win-numeric function, that calculates win scenarios."
  [moves board computer-color depth maximizing? min-or-max placeholder-val]
  (reduce min-or-max placeholder-val 
          (for [move moves] 
            #_{:clj-kondo/ignore [:unresolved-symbol]}
            (minimax (move-piece-clean move board computer-color) 
                     (dec depth) maximizing? computer-color))))

(defn- minimax
  "Minimax algorithm helps us determine the move computer should make against
    the player. It is a heuristic and as such may not always give the best
    answer. We minimize for the human and maximize for the computer."
  [board depth maximizing? computer-color]
  (let [human-color (utility/opposite-player-color computer-color)]
    (if (or (= depth 0) (check-for-win human-color computer-color board))
      (win-numeric board computer-color)
      (if maximizing?
        ;; Maximizing algorithm for the computer
        (let [all-moves (find-all-possible-moves board computer-color)
              moves-with-eat (filter #(string/includes? "EAT" %) all-moves)
              moves-without-eat (into '() (set/difference (set all-moves) (set moves-with-eat)))]
          (if (seq moves-with-eat)
            (reduce max Double/NEGATIVE_INFINITY (for [move moves-with-eat]
                                                   (minimax (move-piece-clean move board computer-color)
                                                            (dec depth) true computer-color)))
            (reduce max Double/NEGATIVE_INFINITY (for [move moves-without-eat]
                                                   (minimax (move-piece-clean move board computer-color)
                                                            (dec depth) false computer-color)))))
        ;; Minimizing algorithm for the player
        (let [all-moves (find-all-possible-moves board human-color)
              moves-with-eat (filter #(string/includes? "EAT" %) all-moves)
              moves-without-eat (into '() (set/difference (set all-moves) (set moves-with-eat)))]
          (if (seq moves-with-eat)
            (reduce min Double/POSITIVE_INFINITY (for [move moves-with-eat]
                                                   (minimax (move-piece-clean move board human-color)
                                                            (dec depth) false computer-color)))
            (reduce min Double/POSITIVE_INFINITY (for [move moves-without-eat]
                                                   (minimax (move-piece-clean move board human-color)
                                                            (dec depth) true computer-color)))))))))

(def all-moves (find-all-possible-moves (board/create-board) "R"))
all-moves

(defn find-best-move
  "Find the best move for the computer using the minimax algorithm."
  [board depth computer-color]
  (let [all-moves (find-all-possible-moves board computer-color)
        moves-with-eat (take-while #(string/includes? "EAT" %) all-moves)
        moves-without-eat (into '() (set/difference (set all-moves) (set moves-with-eat)))
        moves (into moves-without-eat moves-with-eat)]
    (if (seq moves)
      (reduce (fn [best-move current-move]
                (let [score (minimax (move-piece-clean current-move board computer-color)
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
