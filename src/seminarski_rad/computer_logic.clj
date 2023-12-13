(ns seminarski-rad.computer-logic
  (:require [seminarski-rad.validator :as val]
            [seminarski-rad.input-utility :as utility]
            [seminarski-rad.board :as board]
            [seminarski-rad.database :as db]
            [seminarski-rad.statistics :as stats]
            [clojure.string :as str]))

(defn- pieces-on-board-for?
  "Counts available pieces on given board for given player type."
  [player-color board]
  (count (filter #(= player-color (:piece %))
                 (for [row (vals board) col (vals row)] col))))

(def wins (atom {:human 0 :computer 0}))
(def pieces (atom {:human 0 :computer 0}))

(defn initiate-piece-count!
  [board human-color computer-color]
  (let [human-piece-count (pieces-on-board-for? human-color board)
        computer-piece-count (pieces-on-board-for? computer-color board)]
    (reset! pieces {:human human-piece-count 
                    :computer computer-piece-count} )))

(defn initiate-win-count!
  [username]
  (stats/repopulate-game-sessions!)
  (let [user-wins-map (stats/get-map-human-?s-added "WINS")
        computer-wins-map (stats/get-map-human-?s-added "LOSSES")
        username-wins (or ((keyword username)
                           user-wins-map) 0)
        username-losses (or ((keyword username)
                             computer-wins-map) 0)] 
    (reset! wins {:human username-wins
                  :computer username-losses})))

(defn print-the-score
  []
  (println "\nHuman Score: " (:human @wins))
  (println "Computer Score: " (:computer @wins))
  (println))

(defn print-the-pieces
  []
  (println "\nHuman pieces left: " (:human @pieces))
  (println "Computer pieces left: " (:computer @pieces))
  (println))

(defn- save-winner 
  "Saves winner in database."
  [winner winner-color board-size
   username computer-score human-score]
  (let [winner-initial (str/upper-case (subs (name winner) 0 1))
        human-color (if (= winner "HUMAN")
                      winner-color
                      (utility/opposite-player-color winner-color))] 
    (db/insert-game-session (db/get-connection)
                            board-size username
                            winner-initial human-score
                            computer-score human-color)
    "END"))

(defn assign-victory!
  "Updates atom to reflect victory status of user who won,
   prints winner and score. Returns winner as string." 
  [winner winner-color board-size
   username computer-score human-score] 
   (when winner 
       (println (str winner " wins!" "[" winner-color "]"))
       (swap! wins #(update-in % [winner] (fnil inc 0)))
       (save-winner winner winner-color board-size
                    username computer-score human-score)))

(defn check-for-win 
  []
  (let [
        human-piece-count (:human @pieces)
        computer-piece-count (:computer @pieces)]
    (if (= 0 human-piece-count)
      :computer
      (if (= 0 computer-piece-count)
        :human
        false))))

(defn- quit?
  "Checks if the user is trying to quit. If so, then it ends game 
   process. Gives win to opponent"
  [purified-input-str opposite-player-color
   board-size username]
  (when (= purified-input-str "Q")

     (println "Are you sure you want to quit? 
                Quitting gives a victory to the computer.
                [Y] or [N]")
        (let [subseq-response (utility/purify-move-input
                               (utility/prompt-info "your choice" 
                                                    val/confirm-validator-Y-N)
                               board-size)]
          (println "Your choice: " subseq-response)
          (when (= "Y" subseq-response)
              (println "\nUSER QUIT. COMPUTER WINS!")
              (assign-victory! "COMPUTER" opposite-player-color
                               board-size username 
                               (:computer @pieces)
                               (:human @pieces))
              (print-the-score)
              true)))) 

(defn apply-move-indicator
  "Returns board with moved piece if the piece was moved 1 tile or
   a vector with edited board and the word \"eaten\" inside it 
   if the user has eaten a piece. If the user quits during move 
   making, the word \"quit\" is sent inside vector with board."
  [user-input board 
   user-color board-size
   username]
  (let [purified-input-str (utility/purify-move-input user-input
                                                      board-size)
        computer-color (utility/opposite-player-color user-color)]
    (if (quit? purified-input-str computer-color 
               board-size username)
      [board "quit"] 
      (let [validation-result (val/validate-input user-input board user-color board-size)]
        (if-not validation-result
          (do
            (println "Input invalid. Try again.")
            (apply-move-indicator (utility/take-user-input-move) board user-color 
                                  board-size username))
          (let [move-start (utility/move-?-coordinate purified-input-str 1)
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
                          user-input board user-color 
                          board-size "")]
      (if (vector? move-piece-res)
        ;; Here we do not check for quits because a computer may not quit!
        (first move-piece-res)
        move-piece-res))))

(defn- possible-?s-for-one-blank
  "For inputted coords of one blank field it returns a vector of strings of possible
   moves or eats for that given blank"
  [[blank-r-key blank-c-key] board player-color
   board-size moves-or-eats-kw]
  
  (let [solution-unvetted (get-in board [blank-r-key
                                         blank-c-key 
                                         moves-or-eats-kw])
        solution-vetted (filter
                         (fn [[r c]]
                           (val/validate-input
                            (utility/reverse-extraction-of-keys
                             [r c blank-r-key blank-c-key]) 
                            board player-color board-size)) solution-unvetted)]
     (into #{} (map #(utility/reverse-extraction-of-keys
                      (into  % [blank-r-key blank-c-key])) solution-vetted))))

(defn- find-all-blanks
  [board board-size] 
    (for [row (utility/numeric-seq->numeric-keyword-seq 
               (range 1 (inc board-size)))
          col (utility/numeric-seq->letter-keyword-seq
               (range 1 (inc board-size)))
          :when (= " " (get-in board [row col :piece]))] 
       (assoc (assoc (get-in board [row col])
                :row row) :col col)))

(defn- find-all-possible-?s
  [board player-color
   board-size moves-or-eats-kw]
  (let [all-blanks (find-all-blanks board board-size)]
    (reduce (fn 
              [acc blank]
              (into acc (possible-?s-for-one-blank
                         [(:row blank) (:col blank)]
                         board player-color board-size moves-or-eats-kw)))
            #{} all-blanks)))


(defn- evaluate-minimax-candidate
  [difficulty-factor
   human-score
   computer-score]
  (let [computer-weight (- 0.5 (double (/ difficulty-factor 2)))
        human-weight (- 1 computer-weight) 
        human-weighted-score (* human-weight human-score)
        computer-weighted-score (* computer-weight computer-score)]
    (- computer-weighted-score human-weighted-score)))

(defn- reverse-evaluate-minimax-candidate
  [difficulty-factor
   human-score
   computer-score]
  (evaluate-minimax-candidate
   (- 1 difficulty-factor) human-score computer-score))

(defn- ?st-candidate
  [candidates
   best-or-worst]
  (let [symbol (if (= "WORST" best-or-worst)
                 <
                 >) 
        solution (first (reduce (fn
                                    [acc eval]
                                    (if (symbol (first (vals eval))
                                                (first (vals acc)))
                                      eval
                                      acc))
                                  (first candidates) (rest candidates)))]
      {(first solution) (last solution)}))

(def -∞ Double/NEGATIVE_INFINITY)
(def ∞ Double/POSITIVE_INFINITY)

(declare minimax)

(def memoized-minimax
  "Memoized version of minimax"
  (memoize
   (fn [board depth maximizing? playing-color
        board-size root-move h-score c-score
        difficulty-factor alpha beta]
     (minimax board depth maximizing? playing-color
              board-size root-move h-score c-score
              difficulty-factor alpha beta))))

(defn- minimax
  "Minimax algorithm helps us determine the move computer should make against
    the player. It is a heuristic and as such may not always give the best
    answer. We minimize for the human and maximize for the computer."
  [board depth maximizing?
   playing-color board-size
   root-move h-score
   c-score difficulty-factor
   alpha beta]
  
  (let [opponent (utility/opposite-player-color playing-color)
        eval-fn (if maximizing?
                  evaluate-minimax-candidate
                  reverse-evaluate-minimax-candidate)
        eating-moves (find-all-possible-?s
                      board playing-color board-size
                      :eats)
        normal-moves (if (empty? eating-moves)
                       (find-all-possible-?s
                        board playing-color board-size
                        :moves)
                       #{})
        next-player (if (empty? eating-moves)
                      opponent
                      playing-color)
        moves (if (empty? eating-moves)
                normal-moves
                eating-moves)]
    
    (if (= depth 0)
        {root-move (eval-fn difficulty-factor
                            h-score (if maximizing?
                                      (inc c-score) c-score))}
        
      (if maximizing?
        (let [next-maximizing (seq eating-moves)
              next-c-score (if (empty? eating-moves)
                             c-score
                             (inc c-score))]
          (loop [moves-to-check moves
                 best-move nil
                 curr-alpha alpha]
            (if (or (empty? moves-to-check)
                    (and (<= curr-alpha beta)
                         (not= beta ∞)
                         (not= alpha -∞)))
              {root-move curr-alpha}
              (let [move (first moves-to-check)
                    result (memoized-minimax (move-piece-computer
                                     move 
                                     board
                                     playing-color
                                     board-size)
                                    (dec depth)
                                    next-maximizing
                                    next-player
                                    board-size move
                                    h-score next-c-score
                                    difficulty-factor
                                    curr-alpha beta)
                    score (get result move)]
                (if (> score curr-alpha)
                  (recur (rest moves-to-check) move score)
                  (recur (rest moves-to-check) best-move curr-alpha))))))
        
        (let [next-maximizing (seq normal-moves)
              next-h-score (if (empty? eating-moves)
                             h-score
                             (inc h-score))]
         (loop [moves-to-check moves
                best-move nil
                curr-beta beta]
           (if (or (empty? moves-to-check)
                   (and (<= alpha curr-beta)
                        (not= curr-beta ∞)
                        (not= alpha -∞)))
             {root-move curr-beta}
             (let [move (first moves-to-check)
                   result (memoized-minimax (move-piece-computer
                                    move
                                    board
                                    playing-color
                                    board-size)
                                   (dec depth)
                                   next-maximizing
                                   next-player
                                   board-size move
                                   next-h-score c-score
                                   difficulty-factor 
                                   alpha curr-beta)
                   score (get result move)]
               (if (< score curr-beta)
                  (recur (rest moves-to-check) move score) 
                  (recur (rest moves-to-check) best-move curr-beta))))))))))

(defn- rand-difficulty 
  []
  (-> (rand)
      (* 2)
      (- 1)))

(defn- resolve-difficulty-factor 
  [board-size]
  (cond 
    (= board-size 5) -0.5
    (= board-size 7) 0.0
    (= board-size 9) 0.5
    :else (rand-difficulty)))

(defn- resolve-depth
  [board-size]
  (cond
    (= board-size 5) 4
    :else 3))

(defn find-best-move
  "Find the best move for the computer using the minimax algorithm."
  [board computer-color board-size]
  (let [difficulty-factor (resolve-difficulty-factor board-size)
        depth (resolve-depth board-size)
        human-color (utility/opposite-player-color computer-color)
        initial-eating-moves (find-all-possible-?s board computer-color
                                                                 board-size :eats)
        initial-normal-moves (find-all-possible-?s board computer-color
                                                           board-size :moves) 
        initial-minimaxed (if (seq initial-eating-moves) 
                               (map #(memoized-minimax (move-piece-computer
                                                      % board computer-color 
                                                      board-size)
                                                     (dec depth)
                                                     true computer-color
                                                     board-size % 0 0 difficulty-factor
                                                     -∞ ∞)
                                           initial-eating-moves)
                               (map #(memoized-minimax (move-piece-computer
                                                      % board computer-color
                                                      board-size)
                                                     (dec depth)
                                                     false human-color
                                                     board-size % 0 0 difficulty-factor
                                                     -∞ ∞)
                                           initial-normal-moves))]
      (?st-candidate initial-minimaxed "BEST")))

(defn- eat-side-effects!
  "Applies side effects to atoms and potential save to db
    for each turn taken."
  [turntaking-player-color
   affected-player-kw 
   board-size username]
  (swap! pieces update affected-player-kw dec)

  (assign-victory! (check-for-win)
                   turntaking-player-color
                   board-size
                   username
                   (:computer @pieces)
                   (:human @pieces)))

(defn take-turns!
  "Core function of gameplay management. Encodes turn based mechanics as well 
   as providing side effects for win and quit conditions."
  [username current-player board human-color computer-color board-size]
  (board/print-the-board board board-size)
  (print-the-score)
  (print-the-pieces) 
    (if (= current-player "HUMAN")
      (let [result-of-piece-move (apply-move-indicator
                                  (utility/take-user-input-move)
                                  board human-color board-size 
                                  username)]
        (if (vector? result-of-piece-move)
          (when-not (= "quit" (last result-of-piece-move)) 
           ;; We check if the side effects function returned "END" which would
            ;; be the case if the win condition is satisfied.
            (when-not (eat-side-effects! human-color :computer 
                                         board-size username)
              
              (take-turns! username "HUMAN" (first result-of-piece-move)
                           human-color computer-color board-size)))
          (take-turns! username
                      "COMPUTER" result-of-piece-move
                      human-color computer-color board-size)))
      (do
        (println "Computer's turn...")
        (let [res-map (find-best-move
                board computer-color board-size)
              best-move (first (keys res-map)) 
              score (get res-map best-move)
              ]
          (println (str "Computer's move: " best-move))
          (println (str "Function returned score: " score))
          (let [result-of-piece-move (apply-move-indicator best-move
                                                           board computer-color
                                                           board-size
                                                           username)]
            (if (vector? result-of-piece-move)
              ;; Here we do not check for quits because a computer may not quit!
              ;; But we do check if the side effects function returned "END" in case
              ;; the win condition is satisfied.
                (when-not (eat-side-effects! computer-color :human
                                             board-size username)

                  (take-turns! username "COMPUTER" (first result-of-piece-move)
                               human-color computer-color board-size))
              (take-turns! username
                           "HUMAN" result-of-piece-move
                           human-color computer-color board-size)))))))

(def board5 (board/create-board 5))
(def board11 (board/create-board 11))
(def board7 (board/create-board 7))
(def board5-2C-3C (move-piece-computer
                   "2C-3C" board5 "B" 5))
(def board5-2C-3C-4C-2C (move-piece-computer
                         "4C-2C" board5-2C-3C "R" 5))
(def board5-2C-3C-4C-2C-2C-3C (move-piece-computer
                               "2C-3C" board5-2C-3C-4C-2C "R" 5))
(def board5-2C-3C-4C-2C-2C-3C-1C-2C (move-piece-computer
                                     "1C-2C"
                                     board5-2C-3C-4C-2C-2C-3C
                                     "B" 5))
(def board5-4B-3C (move-piece-computer
                   "4B-3C" board5 "R" 5))