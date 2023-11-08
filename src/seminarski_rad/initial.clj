(ns seminarski-rad.initial
  (:require [clojure.string :as str]))

(def board
  { :1 {:A "B" :B "B" :C "B" :D "B" :E "B"}
   :2 {:A "B" :B "B" :C "B" :D "B" :E "B"}
   :3 {:A "B" :B "B" :C "*" :D "R" :E "R"}
   :4 {:A "R" :B "R" :C "R" :D "R" :E "R"}
   :5 {:A "R" :B "R" :C "R" :D "R" :E "R"}})


(defn print-the-board
  [board]
  (do
    (println  "   A   B   C   D   E")
    (println )
    (println (str "1  " (get-in board [:1 :A]) " - " (get-in board [:1 :B]) " - "
                  (get-in board [:1 :C]) " - " (get-in board [:1 :D]) " - "
                  (get-in board [:1 :E])))
    (println (str "     \\ | / | \\ | /"))
    (println (str "2  " (get-in board [:2 :A]) " - " (get-in board [:2 :B]) " - "
                  (get-in board [:2 :C]) " - " (get-in board [:2 :D]) " - "
                  (get-in board [:2 :E])))
    (println (str "     / | \\ | / | \\"))
    (println (str "3  " (get-in board [:3 :A]) " - " (get-in board [:3 :B]) " - "
                  (get-in board [:3 :C]) " - " (get-in board [:3 :D]) " - "
                  (get-in board [:3 :E])))
    (println (str "     \\ | / | \\ | /"))
    (println (str "4  " (get-in board [:4 :A]) " - " (get-in board [:4 :B]) " - "
                  (get-in board [:4 :C]) " - " (get-in board [:4 :D]) " - "
                  (get-in board [:4 :E])))
    (println (str "     / | \\ | / | \\"))
    (println (str "5  " (get-in board [:5 :A]) " - " (get-in board [:5 :B]) " - "
                  (get-in board [:5 :C]) " - " (get-in board [:5 :D]) " - "
                  (get-in board [:5 :E])))))

(print-the-board board)

(defn extract-keys-from-user-input 
  [input]
  (conj (conj (conj (vector (keyword (subs input 0 1)))
                    (keyword (subs input 1 2)))
              (keyword (subs input 3 4)))
        (keyword (subs input 4))))

(extract-keys-from-user-input "1e-2e")

(defn take-user-input
  []
  (println "Your move:")
  (let [user-input (read-line)]
    (println "You entered: " user-input)
    user-input
    ))

(take-user-input)

(defn move-piece
  [user-input]
  (let [user-keys (extract-keys-from-user-input user-input)]
    ( let [user-color (get-in board [(first user-keys)
                                     (first (rest user-keys))])]
       (assoc-in (assoc-in board [(first (rest (rest user-keys)))
                                  (first (rest (rest (rest user-keys))))] user-color) [(first user-keys)
                           (first (rest user-keys))] "*") 
          
      )))

(move-piece "1A-3C")

(assoc-in board [(first (extract-keys-from-user-input "1A-3C"))
                 (first (rest (extract-keys-from-user-input "1A-3C")))] " ")
(defn write-out-board-convo
  [board]
  (do
    (println )
    (println "************************************************************************")
    (println )
    (println "Welcome to alquerque, the board game. Here you play against the computer.
              You control the black pieces and the computer takes hold of the black.")
    (println "To make a turn input the name of the first field (Ex. 1A)
               and then the field you want your piece to go (Ex. 1C).")
    (println "If your input is invalid, you will get another chance at making a move!")
    (println "So let's begin!")
    (println )
    (println "Here's your board:")
    (println ) 
    (print-the-board board)
    (println )
    (print-the-board (move-piece (take-user-input)))
    )
  )


(write-out-board-convo board)

(defn play-game
  [board]
  (do
    (write-out-board-convo board) 
    ))