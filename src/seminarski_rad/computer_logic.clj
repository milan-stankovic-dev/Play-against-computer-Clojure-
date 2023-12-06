(ns seminarski-rad.computer-logic
  (:require [seminarski-rad.validator :as val]
            [seminarski-rad.input-utility :as utility]
            [seminarski-rad.board :as board]
            [clojure.string :as string]
            [clojure.set :as set]))

(defn- pieces-on-board-for?
  [player-color board]
  (count (filter #(= player-color (:piece %))
                 (for [row (vals board) col (vals row)] col))))

(def wins (atom {"HUMAN" 0 "COMPUTER" 0}))
(def pieces (atom {"HUMAN" 0 "COMPUTER" 0}))
@wins

(defn initiate-piece-count
  [board human-color computer-color]
  (let [human-piece-count (pieces-on-board-for? human-color board)
        computer-piece-count (pieces-on-board-for? computer-color board)]
    (reset! pieces {"HUMAN" human-piece-count 
                    "COMPUTER" computer-piece-count} )))

(defn print-the-score
  []
  (println "\nHuman Score: " (get @wins "HUMAN"))
  (println "Computer Score: " (get @wins "COMPUTER"))
  (println))

(defn print-the-pieces
  []
  (println "\nHuman pieces left: " (get @pieces "HUMAN"))
  (println "Computer pieces left: " (get @pieces "COMPUTER"))
  (println))

(defn assign-victory
  "Updates atom to reflect victory status of user who won,
   prints winner and score. Returns winner as string." 
  [winner winner-color] 
  (if-not winner
    nil
    (do
      (println (str winner " wins!" "[" winner-color "]"))
      (swap! wins #(update-in % [winner] (fnil inc 0))))))

(defn check-for-win 
  [
  ;;  human-color computer-color board
   ]
  (let [
        ;; human-piece-count (pieces-on-board-for? human-color board)
        ;; computer-piece-count (pieces-on-board-for? computer-color board)
        human-piece-count (get @pieces "HUMAN")
        computer-piece-count (get @pieces "COMPUTER")]
    (if (= 0 human-piece-count)
      "COMPUTER"
      (if (= 0 computer-piece-count)
        "HUMAN"
        false))))


(defn- quit?
  "Checks if the user is trying to quit. If so, then it ends game 
   process. Gives win to opponent"
  [purified-input-str opposite-player opposite-player-color]
  (if (= purified-input-str "Q")

    (do (println "Are you sure you want to quit? 
                Quitting gives a victory to the computer.
                [Y] or [N]")
        (let [subseq-response (utility/purify-user-input
                               (utility/prompt-info "your choice" 
                                                    val/confirm-validator-Y-N))]
          (println "Your choice: " subseq-response)
          (if (= "Y" subseq-response)
            (do
              (println "\nUSER QUIT. COMPUTER WINS!")
              (assign-victory opposite-player opposite-player-color)
              (print-the-score)
              true) false))) false))


(defn apply-move-indicator
  "Returns board with moved piece if the piece was moved 1 tile or
   a vector with edited board and the word \"eaten\" inside it 
   if the user has eaten a piece. If the user quits during move 
   making, the word \"quit\" is sent inside vector with board."
  [user-input board user-color board-size]
  (let [purified-input-str (utility/purify-user-input user-input)
        computer-color (utility/opposite-player-color user-color)]
    (if (quit? purified-input-str "COMPUTER" computer-color)
      [board "quit"] 
      (let [validation-result (val/validate-input user-input board user-color board-size)]
        (if-not validation-result
          (apply-move-indicator (utility/take-user-input-move) board user-color board-size)
          (let [purified-input-str (utility/purify-user-input user-input)
                move-start (utility/move-?-coordinate purified-input-str 1)
                move-finish (utility/move-?-coordinate purified-input-str 2)
                move-done-board (assoc-in (assoc-in board (conj move-finish :piece) user-color)
                                          (conj move-start :piece) " ")]
            (if (= validation-result "eat")
              (let [move-done-eaten (assoc-in move-done-board
                                              (conj (utility/calculate-field-to-eat
                                                     purified-input-str) :piece) " ")] 
                  [move-done-eaten "eaten"])
              move-done-board)))))))

(defn move-piece-computer
  "Returns board after move, no caveats or indications of eating or not.
    If move is improper, returns board as is."
  [user-input board user-color board-size]
  (if-not (val/validate-input user-input board user-color board-size)
    board
    (let [move-piece-res (apply-move-indicator
                          user-input board user-color board-size)]
      (if (vector? move-piece-res)
        ;; Here we do not check for quits because a computer may not quit!
        (first move-piece-res)
        move-piece-res))))

(defn- win-numeric
  [board computer-color]
  (let [human-color (utility/opposite-player-color computer-color)
        win-check-result (check-for-win
                          ;; human-color computer-color board
                          )]
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
  [[blank-r-key blank-c-key] board player-color board-size]
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
                            board player-color board-size)) 
                         (into moves (map add-suffix-eats-to-eating-keyword eats)))]
    (set (map (fn [[r c]] (str (str (name r) (name c)) "-" blank-str))
              solution-vector))))

(defn- find-all-possible-moves [board player-color board-size]
  (set (apply concat
              (for [row-key (keys board)
                    col-key (keys (board row-key))
                    :when (= (get-in board [row-key col-key :piece]) " ")]
                (possible-moves-for-one-blank [row-key col-key] board player-color board-size)))))

(defn- minimax
  "Minimax algorithm helps us determine the move computer should make against
    the player. It is a heuristic and as such may not always give the best
    answer. We minimize for the human and maximize for the computer."
  [board depth maximizing? computer-color board-size]
  (let [human-color (utility/opposite-player-color computer-color)]
    (if (or (= depth 0) (check-for-win 
                        ;;  human-color computer-color board
                         ))
      (win-numeric board computer-color)
      (if maximizing?
        ;; Maximizing algorithm for the computer
        (let [all-moves (find-all-possible-moves board computer-color board-size)
              moves-with-eat (filter #(string/includes? "EAT" %) all-moves)
              moves-without-eat (into '() (set/difference (set all-moves) (set moves-with-eat)))]
          (if (seq moves-with-eat)
            (reduce max Double/NEGATIVE_INFINITY (for [move moves-with-eat]
                                                   (minimax (move-piece-computer move board computer-color board-size)
                                                            (dec depth) true computer-color board-size)))
            (reduce max Double/NEGATIVE_INFINITY (for [move moves-without-eat]
                                                   (minimax (move-piece-computer move board computer-color board-size)
                                                            (dec depth) false computer-color board-size)))))
        ;; Minimizing algorithm for the player
        (let [all-moves (find-all-possible-moves board human-color board-size )
              moves-with-eat (filter #(string/includes? "EAT" %) all-moves)
              moves-without-eat (into '() (set/difference (set all-moves) (set moves-with-eat)))]
          (if (seq moves-with-eat)
            (reduce min Double/POSITIVE_INFINITY (for [move moves-with-eat]
                                                   (minimax (move-piece-computer move board human-color board-size)
                                                            (dec depth) false computer-color board-size)))
            (reduce min Double/POSITIVE_INFINITY (for [move moves-without-eat]
                                                   (minimax (move-piece-computer move board human-color board-size)
                                                            (dec depth) true computer-color board-size)))))))))

(def all-moves (find-all-possible-moves (board/create-board 5) "R" 5))
all-moves

(defn find-best-move
  "Find the best move for the computer using the minimax algorithm."
  [board depth computer-color board-size]
  (let [all-moves (find-all-possible-moves board computer-color board-size)
        moves-with-eat (take-while #(string/includes? "EAT" %) all-moves)
        moves-without-eat (into '() (set/difference (set all-moves) (set moves-with-eat)))
        moves (into moves-without-eat moves-with-eat)]
    (if (seq moves)
      (reduce (fn [best-move current-move]
                (let [score (minimax (move-piece-computer current-move board computer-color board-size)
                                     (dec depth) false computer-color board-size)]
                  (if (or (nil? best-move) (and (nil? (best-move 1)) (< (score 0) (best-move 0))))
                    [score current-move]
                    best-move)))
              nil
              moves)
      nil)))

(defn take-turns
  [current-player board human-color computer-color board-size]
  (board/print-the-board board board-size)
  (print-the-score)
  (print-the-pieces)
  (if (= current-player "HUMAN")
    (let [result-of-piece-move (apply-move-indicator
                                (utility/take-user-input-move)
                                board human-color board-size)]
      (if (vector? result-of-piece-move)
        (if (= "quit" (last result-of-piece-move))
          nil
          (do
            (assign-victory (check-for-win
                                        ;; human-color computer-color board
                                      )
                                     human-color
                            )
            (swap! pieces update "COMPUTER" dec)
            (take-turns "HUMAN" (first result-of-piece-move)
                        human-color computer-color board-size)))
        (take-turns "COMPUTER" result-of-piece-move human-color computer-color board-size)))
    (do
      (println "Computer's turn...")
      (Thread/sleep 2000)
      (let [[score best-move] (find-best-move
                               board 5 computer-color board-size)]
        (println (str "Computer's move: " best-move))
        (println (str "Function returned score: " score))
        (let [result-of-piece-move (apply-move-indicator best-move
                                                                  board computer-color board-size)]
          (if (vector? result-of-piece-move)
              ;; Here we do not check for quits because a computer may not quit!
            (do
              (assign-victory (check-for-win
                                          ;; human-color computer-color board
                                        )
                                       computer-color)
              (swap! pieces update "HUMAN" dec)
              (take-turns "COMPUTER" (first result-of-piece-move)
                          human-color computer-color board-size))
            (take-turns "HUMAN" result-of-piece-move human-color computer-color board-size)))))))
