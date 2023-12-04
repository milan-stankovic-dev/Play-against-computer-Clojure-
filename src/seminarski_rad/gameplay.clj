(ns seminarski-rad.gameplay
  (:require [seminarski-rad.input-utility :as utility]
            [seminarski-rad.validator :as val]
            [seminarski-rad.board :as board]
            [seminarski-rad.computer :as computer]
            [seminarski-rad.database :as db]))

(defn prompt-login
  []
  (let [username (utility/prompt-info "your username" val/not-empty?)
        password (utility/prompt-info "your password" val/not-empty?)
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

(defn write-out-board-convo
  [board board-size]
   (println "\n**********************************************************************\n")
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
      (board/print-the-board board board-size)
      (computer/print-the-score )
      (println))

(defn take-turns
  [current-player board human-color computer-color board-size]
  (board/print-the-board board board-size) 
  (computer/print-the-score)
  (if (computer/check-for-win human-color computer-color board)
    (println "End of game.")
    
    (if (= current-player "HUMAN")
      (let [result-of-piece-move (computer/apply-move-indicator
                                  (utility/take-user-input-move)
                                             board human-color board-size)]
        (if (vector? result-of-piece-move) 
          (if (= "quit" (last result-of-piece-move))
            nil 
            (take-turns "HUMAN" (first result-of-piece-move)
                        human-color computer-color board-size))
          (take-turns "COMPUTER" result-of-piece-move human-color computer-color board-size)))
      (do
        (println "Computer's turn...")
        (Thread/sleep 2000)
           (let [[score best-move] (computer/find-best-move
                                  board 5 computer-color board-size)]
          (println (str "Computer's move: " best-move))
          (println (str "Function returned score: " score))
          (let [result-of-piece-move (computer/apply-move-indicator best-move
                                                                   board computer-color board-size)]
            (if (vector? result-of-piece-move)
              ;; Here we do not check for quits because a computer may not quit!
              (take-turns "COMPUTER" (first result-of-piece-move)
                          human-color computer-color board-size)
              (take-turns "HUMAN" result-of-piece-move human-color computer-color board-size))))))))

(defn play-game
  [board board-size]
  (write-out-board-convo board board-size) 
  (let [human-color (utility/purify-user-input 
                     (utility/prompt-info "user color [B] or [R]"
                                          val/user-color-input-validator))
        computer-color (utility/opposite-player-color human-color)]
    (take-turns "HUMAN" board human-color computer-color board-size)))

(defn access-main-menu-item
  []
  (let [user-choice (utility/purify-user-input
                     (utility/prompt-info "a number" val/not-empty?))]
    (case user-choice
      "1" (play-game (board/create-board 3) 3)
      "2" (play-game (board/create-board 7) 7)
      "3" (play-game (board/create-board 9) 9)
      "END")))

(defn manage-menus
  ([]
   (manage-menus (prompt-login)))
  ([logged-in-user]
   (write-main-menu logged-in-user)
   (when-not (= "END" (access-main-menu-item))
     (manage-menus logged-in-user))))

(manage-menus)