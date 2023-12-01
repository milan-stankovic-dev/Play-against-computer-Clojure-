(ns seminarski-rad.gameplay
  (:require [seminarski-rad.input-utility :as utility]
            [seminarski-rad.validator :as val]
            [seminarski-rad.board :as board]
            [seminarski-rad.computer :as computer]
            [seminarski-rad.database :as db]
            [clojure.string :as str]))

(def board (board/create-board 5))

(board/print-the-board board 5)

(defn prompt-info
  [what-to-prompt]
  (println (str "Please enter your " what-to-prompt ":"))
  (let [info (read-line)]
    (if (empty? info)
      (do
        (println (str (str/capitalize what-to-prompt) " invalid."))
        (prompt-info what-to-prompt))
      info)))

(defn prompt-login
  []
  (let [username (prompt-info "username")
        password (prompt-info "password")
        user-db (db/login-user (db/get-connection)
                               username password)] 
      (if-not user-db
        (do
          (println "\nWrong credentials. Try again.\n")
          (prompt-login))
        user-db)))

(defn write-main-menu
  [logged-in-user] 
  (println (str "
  *********************************************************************
            
      Welcome " (:app_user/username logged-in-user) " to \"Play against computer- the app\"! This
      interactive game will have you competing against your family and
      friends in no time. But first, you need to beat the computer in
      a one-to-one matchup. Can you do it?

      To get started you must choose one of the options below:

      1 -> play alquerque (easy)
      2 -> play alquerque (medium)
      3 -> play alquerque (hard)
      4 -> game statistics
            
      Press any other key to quit.
            
  *********************************************************************\n")))

(write-main-menu #:app_user{:id 6,
                            :username "stanmil",
                            :password
                            "bcrypt+sha512$d0ef5d08ea0ca8d37b5ed7707a2e9d0b$12$c7da695cfd64c5343f0c88cc673e369be54f678177ad1ddc"})

(defn write-out-board-convo
  [board]
  (do (println "\n**********************************************************************\n")
      (println
       "Welcome to alquerque, the board game. Here you play against the computer.
    Upon starting the game you are prompted to choose player color. Simply input
    [B] for blue or [R] for red. To make a turn input the name of the first field
    (Ex. 1A), followed by a '-' symbol and then the field you want your piece to go
    (Ex. 1C). You may also include the word \"EAT\" in the middle (Ex. 1A-EAT-1C),
    if you are skipping a field, but this is not necessary. If your input is invalid,
    you will get another chance at making a move! The player whose onlypieces remain
    wins! Good luck!\n
    
    Here's your board:\n")
      (board/print-the-board board 5)
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
  (board/print-the-board board 5) 
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
                                  board 5 computer-color)]
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

;; (play-game board)