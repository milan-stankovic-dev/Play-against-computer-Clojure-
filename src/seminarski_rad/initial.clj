(ns seminarski-rad.initial)

(def board
  { :1 {:A "B" :B "B" :C "B" :D "B" :E "B"}
   :2 {:A "B" :B "B" :C "B" :D "B" :E "B"}
   :3 {:A "B" :B "B" :C " " :D "R" :E "R"}
   :4 {:A "R" :B "R" :C "R" :D "R" :E "R"}
   :5 {:A "R" :B "R" :C "R" :D "R" :E "R"}})


(defn print-the-board
  [board]
  (do 
    (println (str (get-in board [:1 :A]) " - " (get-in board [:1 :B]) " - " 
                  (get-in board [:1 :C]) " - " (get-in board [:1 :D]) " - "
                  (get-in board [:1 :E])))
    (println (str "  \\ | / | \\ | /"))
    (println (str (get-in board [:2 :A]) " - " (get-in board [:2 :B]) " - "
                  (get-in board [:2 :C]) " - " (get-in board [:2 :D]) " - "
                  (get-in board [:2 :E])))
    (println (str "  / | \\ | / | \\"))
    (println (str (get-in board [:3 :A]) " - " (get-in board [:3 :B]) " - "
                  (get-in board [:3 :C]) " - " (get-in board [:3 :D]) " - "
                  (get-in board [:3 :E])))
    (println (str "  \\ | / | \\ | /")) 
    (println (str (get-in board [:4 :A]) " - " (get-in board [:4 :B]) " - "
                  (get-in board [:4 :C]) " - " (get-in board [:4 :D]) " - "
                  (get-in board [:4 :E])))
    (println (str "  / | \\ | / | \\"))
    (println (str (get-in board [:5 :A]) " - " (get-in board [:5 :B]) " - " 
                  (get-in board [:5 :C]) " - " (get-in board [:5 :D]) " - "
                  (get-in board [:5 :E])))
    ))

(print-the-board board)


(defn is-valid
  [user-input]
  (if (not (= (count user-input) 5)) 
    (print "Your input must be 5 characters long!")
    ))

(defn take-user-input
  []
  (println "Your move:")
  (let [user-input (read-line)]
    (println "You entered: " user-input) 
    ))

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
    (println "************************************************************************")
    (print-the-board board)
    (println )
    (take-user-input )
    )
  )


(write-out-board-convo board)

(defn play-game
  [board]
  (do
    (write-out-board-convo board)
    )) 