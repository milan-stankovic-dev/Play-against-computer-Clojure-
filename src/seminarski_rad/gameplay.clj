(ns seminarski-rad.gameplay
  (:require [seminarski-rad.inputUtility :as util]
            [seminarski-rad.validator :as val]))

(def board
  {:1 {:A "B" :B "B" :C "B" :D "B" :E "B"}
   :2 {:A "B" :B "B" :C "B" :D "B" :E "B"}
   :3 {:A "B" :B "B" :C "*" :D "R" :E "R"}
   :4 {:A "R" :B "R" :C "R" :D "R" :E "R"}
   :5 {:A "R" :B "R" :C "R" :D "R" :E "R"}})

;; (def board
;;   {:1 {:A "R" :B "B" :C "*" :D "*" :E "*"}
;;    :2 {:A "*" :B "*" :C "*" :D "*" :E "*"}
;;    :3 {:A "*" :B "*" :C "*" :D "*" :E "*"}
;;    :4 {:A "*" :B "*" :C "*" :D "*" :E "*"}
;;    :5 {:A "*" :B "*" :C "*" :D "*" :E "*"}})

(def current-game-score (atom {"HUMAN" {:color ""
                                       :score 0}
                               "COMPUTER" {:color ""
                                          :score 0}}))
(def wins (atom {"HUMAN" 0 "COMPUTER" 0}))
@wins

(defn print-the-score
  []
  (println "\nHuman Score: " (get @wins "HUMAN"))
  (println "Computer Score: " (get @wins "COMPUTER"))
  (println))

(defn print-the-board
  [board]
    (println )
    (println  "   A   B   C   D   E")
    (println )
    (println (str "1  " (get-in board [:1 :A]) " - " (get-in board [:1 :B]) " - "
                  (get-in board [:1 :C]) " - " (get-in board [:1 :D]) " - "
                  (get-in board [:1 :E])))
    (println (str "   | \\ | / | \\ | / |"))
    (println (str "2  " (get-in board [:2 :A]) " - " (get-in board [:2 :B]) " - "
                  (get-in board [:2 :C]) " - " (get-in board [:2 :D]) " - "
                  (get-in board [:2 :E])))
    (println (str "   | / | \\ | / | \\ |"))
    (println (str "3  " (get-in board [:3 :A]) " - " (get-in board [:3 :B]) " - "
                  (get-in board [:3 :C]) " - " (get-in board [:3 :D]) " - "
                  (get-in board [:3 :E])))
    (println (str "   | \\ | / | \\ | / |"))
    (println (str "4  " (get-in board [:4 :A]) " - " (get-in board [:4 :B]) " - "
                  (get-in board [:4 :C]) " - " (get-in board [:4 :D]) " - "
                  (get-in board [:4 :E])))
    (println (str "   | / | \\ | / | \\ |"))
    (println (str "5  " (get-in board [:5 :A]) " - " (get-in board [:5 :B]) " - "
                  (get-in board [:5 :C]) " - " (get-in board [:5 :D]) " - "
                  (get-in board [:5 :E]))))

;; (print-the-board board)

(defn take-user-input-move
  []
  (println "Your move:")
  (let [user-input (read-line)]
    (println "You entered: " user-input)
    user-input))

;; (take-user-input-move)

(defn move-piece
  "Returns board with moved piece if the piece was moved 1 tile or
   a vector with edited board and the word \"eaten\" inside it 
   if the user has eaten a piece."
  [user-input board player user-color]
  (let [validation-result (val/validate-input user-input board user-color)]
    (if (not validation-result)
      (move-piece (take-user-input-move) board player user-color) 
      (let [purified-input-str (util/purify-user-input user-input)
            move-start (util/get-move-start purified-input-str)
            move-finish (util/get-move-finish purified-input-str)
            move-done-board (assoc-in (assoc-in board move-finish user-color)
                  move-start "*")
            move-done-eaten (assoc-in move-done-board
                                      (util/calculate-field-to-eat purified-input-str) "*")]
        (if (= validation-result "eat")
          (do
            ;; (swap! current-game-score
            ;;        #(update-in % [player :score] inc))
            [move-done-eaten "eaten"])
          move-done-board)))))

(move-piece "4C-2C" {:1 {:A "B" :B "B" :C "B" :D "B" :E "B"}
                     :2 {:A "B" :B "B" :C "*" :D "B" :E "B"}
                     :3 {:A "B" :B "B" :C "B" :D "R" :E "R"}
                     :4 {:A "R" :B "R" :C "R" :D "R" :E "R"}
                     :5 {:A "R" :B "R" :C "R" :D "R" :E "R"}} "HUMAN" "R")

(defn write-out-board-convo
  [board]
  (do (println "\n**********************************************************************\n")
      (println
       "Welcome to alquerque, the board game. Here you play against the computer.
    You control the black pieces and the computer takes hold of the black.
    To make a turn input the name of the first field (Ex. 1A)
    and then the field you want your piece to go (Ex. 1C).
    If your input is invalid, you will get another chance at making a move!
    So let's begin!\n
    Here's your board:\n")
      (print-the-board board)
      (print-the-score )
      (println)))

;; (write-out-board-convo board)


(defn prompt-user-color
  []
    (println "Enter your color: [B/R]")
    (let [user-input (read-line)] 
      (if-not (val/user-color-input-validator user-input)
        (prompt-user-color)
        (util/purify-user-input user-input))))

(defn check-for-win
  [human-color computer-color board]
  (if-not
   (some (fn [row] (some #(= human-color %) (vals row)))
         (vals board))
    (do
      (println (str "Computer wins!"
                    "[" computer-color "]"))
      (swap! wins #(update-in % ["COMPUTER"] (fnil inc 0)))
      (print-the-score)
      true)
    (if-not
     (some (fn [row] (some #(= computer-color %) (vals row)))
           (vals board))
      (do
        (println (str "Human wins!"
                      "[" human-color "]"))
        (swap! wins #(update-in % ["HUMAN"] (fnil inc 0)))
        (print-the-score)
        true)
      false)))


(check-for-win "R" "B" {:1 {:A "R" :B "B" :C "*" :D "*" :E "*"}
                        :2 {:A "*" :B "*" :C "*" :D "*" :E "*"}
                        :3 {:A "*" :B "*" :C "*" :D "*" :E "*"}
                        :4 {:A "*" :B "*" :C "*" :D "*" :E "*"}
                        :5 {:A "*" :B "*" :C "*" :D "*" :E "*"}})

(defn take-turns
  [current-player board human-color computer-color]
  (print-the-board board) 
  (print-the-score)
  (if (check-for-win human-color computer-color board)
    (println "End of game.")
    
    (if (= current-player "HUMAN")
      (let [result-of-piece-move (move-piece (take-user-input-move)
                                             board "HUMAN" human-color)]
        (if (vector? result-of-piece-move)
          (take-turns "HUMAN" (first result-of-piece-move)
                      human-color computer-color)
          (take-turns "COMPUTER" result-of-piece-move human-color computer-color)))
      (let [edited-board board] 
        (println "Computer's turn...") 
        (Thread/sleep 2000) 
        (take-turns "HUMAN" edited-board computer-color human-color)))))

(defn play-game
  [board]
   (write-out-board-convo board) 
      (let [human-color (prompt-user-color)
            computer-color (util/opposite-player-color human-color)]
        (take-turns "HUMAN" board human-color computer-color)))

(play-game board)