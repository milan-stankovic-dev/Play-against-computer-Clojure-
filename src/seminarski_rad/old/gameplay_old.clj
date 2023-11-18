(ns seminarski-rad.old.gameplay-old)

(def board
  {:1 {:A "B" :B "B" :C "B" :D "B" :E "B"}
   :2 {:A "B" :B "B" :C "B" :D "B" :E "B"}
   :3 {:A "B" :B "B" :C "*" :D "R" :E "R"}
   :4 {:A "R" :B "R" :C "R" :D "R" :E "R"}
   :5 {:A "R" :B "R" :C "R" :D "R" :E "R"}})

(def current-game-score (atom {"HUMAN" {:color ""
                                        :score 0}
                               "COMPUTER" {:color ""
                                           :score 0}}))