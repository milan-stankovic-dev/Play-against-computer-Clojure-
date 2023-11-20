(ns seminarski-rad.gameplay
  (:require [seminarski-rad.input-utility :as utility]
            [seminarski-rad.validator :as val]
            [seminarski-rad.board :as board]
            [seminarski-rad.computer :as computer]))

(def board (board/create-board))


(defn print-the-board
  [board]
    (println )
    (println  "   A   B   C   D   E")
    (println )
    (println (str "1  " (get-in board [:1 :A :piece]) " ─ " (get-in board [:1 :B :piece]) " ─ "
                  (get-in board [:1 :C :piece]) " ─ " (get-in board [:1 :D :piece]) " ─ "
                  (get-in board [:1 :E :piece])))
    (println (str "   | \\ | / | \\ | / |"))
    (println (str "2  " (get-in board [:2 :A :piece]) " ─ " (get-in board [:2 :B :piece]) " ─ "
                  (get-in board [:2 :C :piece]) " ─ " (get-in board [:2 :D :piece]) " ─ "
                  (get-in board [:2 :E :piece])))
    (println (str "   | / | \\ | / | \\ |"))
    (println (str "3  " (get-in board [:3 :A :piece]) " ─ " (get-in board [:3 :B :piece]) " ─ "
                  (get-in board [:3 :C :piece]) " ─ " (get-in board [:3 :D :piece]) " ─ "
                  (get-in board [:3 :E :piece])))
    (println (str "   | \\ | / | \\ | / |"))
    (println (str "4  " (get-in board [:4 :A :piece]) " ─ " (get-in board [:4 :B :piece]) " ─ "
                  (get-in board [:4 :C :piece]) " ─ " (get-in board [:4 :D :piece]) " ─ "
                  (get-in board [:4 :E :piece])))
    (println (str "   | / | \\ | / | \\ |"))
    (println (str "5  " (get-in board [:5 :A :piece]) " ─ " (get-in board [:5 :B :piece]) " ─ "
                  (get-in board [:5 :C :piece]) " ─ " (get-in board [:5 :D :piece]) " ─ "
                  (get-in board [:5 :E :piece]))))

(print-the-board board)


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
      (computer/print-the-score )
      (println)))

(write-out-board-convo board)


(defn prompt-user-color
  []
    (println "Enter your color: [B/R]")
    (let [user-input (read-line)] 
      (if-not (val/user-color-input-validator user-input)
        (prompt-user-color)
        (utility/purify-user-input user-input))))

(defn take-turns
  [current-player board human-color computer-color]
  (print-the-board board) 
  (computer/print-the-score)
  (if (computer/check-for-win human-color computer-color board)
    (println "End of game.")
    
    (if (= current-player "HUMAN")
      (let [result-of-piece-move (computer/move-piece-eaten-indicator
                                  (utility/take-user-input-move)
                                             board human-color)]
        (if (vector? result-of-piece-move)
          (take-turns "HUMAN" (first result-of-piece-move)
                      human-color computer-color)
          (take-turns "COMPUTER" result-of-piece-move human-color computer-color)))
      (do
        (println "Computer's turn...")
        (Thread/sleep 2000)
           (let [[score best-move] (computer/find-best-move
                                  board 3 computer-color)]
          (println (str "Computer's move:" best-move))
          (println (str "Function returned score: " score))
          (let [result-of-piece-move (computer/move-piece-eaten-indicator best-move
                                                                   board computer-color)]
            (if (vector? result-of-piece-move)
              (take-turns "COMPUTER" (first result-of-piece-move)
                          human-color computer-color)
              (take-turns "HUMAN" result-of-piece-move human-color computer-color))))))))

(defn play-game
  [board]
   (write-out-board-convo board) 
      (let [human-color (prompt-user-color)
            computer-color (utility/opposite-player-color human-color)]
        (take-turns "HUMAN" board human-color computer-color)))

(play-game board)