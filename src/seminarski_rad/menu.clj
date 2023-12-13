(ns seminarski-rad.menu
  (:require [seminarski-rad.input-utility :as utility]
            [seminarski-rad.validator :as val]
            [seminarski-rad.board :as board]
            [seminarski-rad.computer-logic :as comp]
            [seminarski-rad.database :as db]
            [seminarski-rad.statistics :as stats]))

(defn- prompt-login
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


(defn- write-main-menu
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
      5 -> just for fun
      
      Press any other key to quit.
            
  *********************************************************************\n")))

(defn- write-out-board-convo
  [board board-size]
   (println "\n**********************************************************************\n")
      (println "
    Welcome to alquerque, the board game. Here you play against the AI.
    Upon starting the game you were prompted to choose a color. Now you are meant to
    play a session against an AI with given piece color. To make a turn input
    the coordinates of the first field (Ex. 1A), followed by a '-' symbol and then the field
    you want your piece to go (Ex. 1C). Example: 1A-1B or 1A-1C. You may also use lowercase letters
    if that suits you better, but larger boards may have both uppercase and lowercase letters, 
    so watch out. If your input is invalid, you will get another chance at making a move! Upon 
    \"eating\" the opponents piece, you get another move. Same goes for the AI. If you want to 
    quit the game, press the Q key (gives the AI a victory).
    The player whose only pieces remain
    wins! Good luck!\n
    
    Here's your board:\n")
      (board/print-the-board board board-size)
      ;; (comp/print-the-score )
      ;; (computer/initiate-piece-count board-size)
      (println))


(defn- play-game
  [board board-size username]
  (write-out-board-convo board board-size) 
  (let [human-color (utility/purify-user-input 
                     (utility/prompt-info "user color [B] or [R]"
                                          val/user-color-input-validator))
        computer-color (utility/opposite-player-color human-color)]
    (comp/initiate-piece-count! board
                                   human-color computer-color) 
    (comp/initiate-win-count! username)
    (comp/take-turns! username "HUMAN" board human-color computer-color board-size)))

(defn- custom-board-menu
  [username]
  (let [fixed-user-input (utility/adjust-board-size 
                           (utility/purify-user-input
                            (utility/prompt-info
                             "board size"
                             val/not-empty?)))]
    (println (str "You inputted: " fixed-user-input))
    (play-game (board/create-board fixed-user-input)
               fixed-user-input username)))

(defn- access-main-menu-item
  [username]
  (let [user-choice (utility/purify-user-input
                     (utility/prompt-info "menu item number" val/not-empty?))]
    (case user-choice
      "1" (play-game (board/create-board 5) 5 username)
      "2" (play-game (board/create-board 7) 7 username)
      "3" (play-game (board/create-board 9) 9 username)
      "4" (do
            (println "Calculating statistics...")
            (stats/spit-all-contents)
            (println "Done. Check statistics.txt"))
      "5" (custom-board-menu username)
      "END")))

(defn manage-menus
  ([]
   (manage-menus (prompt-login)))
  ([logged-in-user]
   (write-main-menu logged-in-user)
   (when-not (= "END" (access-main-menu-item 
                     (:app_user/username logged-in-user)))
     (manage-menus logged-in-user))))
;(:app_user/username logged-in-user)