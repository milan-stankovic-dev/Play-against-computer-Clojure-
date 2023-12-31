(ns seminarski-rad.computer-logic-test
  (:require [seminarski-rad.computer-logic :refer :all]
            [midje.sweet :refer :all]
            [seminarski-rad.statistics :as stats]
            [seminarski-rad.board :as board]))

(let [board5 (board/create-board 5)]
  (initiate-piece-count! board5 "B" "R"))

(reset! stats/game-sessions-info [{:GAME_SESSION/ID 1,
                             :APP_USER/ID 6,
                             :GAME_SESSION/WON "H",
                             :GAME_SESSION/HUMAN_SCORE 3,
                             :GAME_SESSION/COMPUTER_SCORE 0,
                             :GAME_SESSION/HUMAN_COLOR "B",
                             :APP_USER/USERNAME "stanmil",
                             :BOARD/SIZE 5}
                            {:GAME_SESSION/ID 2,
                             :APP_USER/ID 6,
                             :GAME_SESSION/WON "H",
                             :GAME_SESSION/HUMAN_SCORE 5,
                             :GAME_SESSION/COMPUTER_SCORE 0,
                             :GAME_SESSION/HUMAN_COLOR "R",
                             :APP_USER/USERNAME "stanmil",
                             :BOARD/SIZE 5}
                            {:GAME_SESSION/ID 3,
                             :APP_USER/ID 7,
                             :GAME_SESSION/WON "C",
                             :GAME_SESSION/HUMAN_SCORE 18,
                             :GAME_SESSION/COMPUTER_SCORE 19,
                             :GAME_SESSION/HUMAN_COLOR "B",
                             :APP_USER/USERNAME "ppetar",
                             :BOARD/SIZE 7}
                            {:GAME_SESSION/ID 4,
                             :APP_USER/ID 6,
                             :GAME_SESSION/WON "C",
                             :GAME_SESSION/HUMAN_SCORE 0,
                             :GAME_SESSION/COMPUTER_SCORE 8,
                             :GAME_SESSION/HUMAN_COLOR "B",
                             :APP_USER/USERNAME "stanmil",
                             :BOARD/SIZE 5}
                            {:GAME_SESSION/ID 5,
                             :APP_USER/ID 8,
                             :GAME_SESSION/WON "H",
                             :GAME_SESSION/HUMAN_SCORE 2,
                             :GAME_SESSION/COMPUTER_SCORE 0,
                             :GAME_SESSION/HUMAN_COLOR "B",
                             :APP_USER/USERNAME "saraa",
                             :BOARD/SIZE 9}
                            {:GAME_SESSION/ID 6,
                             :APP_USER/ID 8,
                             :GAME_SESSION/WON "C",
                             :GAME_SESSION/HUMAN_SCORE 2,
                             :GAME_SESSION/COMPUTER_SCORE 2,
                             :GAME_SESSION/HUMAN_COLOR "B",
                             :APP_USER/USERNAME "saraa",
                             :BOARD/SIZE 3}
                            {:GAME_SESSION/ID 7,
                             :APP_USER/ID 7,
                             :GAME_SESSION/WON "C",
                             :GAME_SESSION/HUMAN_SCORE 0,
                             :GAME_SESSION/COMPUTER_SCORE 9,
                             :GAME_SESSION/HUMAN_COLOR "R",
                             :APP_USER/USERNAME "ppetar",
                             :BOARD/SIZE 5}])

(let [user-wins-map (stats/get-map-human-?s-added "WINS")
      computer-wins-map (stats/get-map-human-?s-added "LOSSES")
      username-wins (or ((keyword "stanmil")
                         user-wins-map) 0)
      username-losses (or ((keyword "stanmil")
                           computer-wins-map) 0)]
  (reset! wins {:human username-wins
                :computer username-losses}))

(fact "Checking for wins starting board."
      (check-for-win) => false)

(swap! pieces update :computer #(- % 12))

(fact "Checking for wins human"
      (check-for-win) => :human)

(swap! pieces update :computer #(+ % 12))
(swap! pieces update :human #(- % 12))

(fact "Checking for wins computer"
      (check-for-win) => :computer)

(swap! pieces update :human #(+ % 12))

(let [board5 (board/create-board 5) 
      board5-2C-3C (move-piece-computer 
                    "2C-3C" board5 "B" 5) 
      board5-2C-3C-4C-2C (move-piece-computer
                               "4C-2C" board5-2C-3C "R" 5) 
      board5-2C-3C-4C-2C-2C-3C (move-piece-computer
                                     "2C-3C" board5-2C-3C-4C-2C "R" 5) 
      board5-2C-3C-4C-2C-2C-3C-1C-2C (move-piece-computer
                                           "1C-2C"
                                           board5-2C-3C-4C-2C-2C-3C
                                           "B" 5)]
(fact "move-piece-computer Moving a piece (human, non eating)." 
      board5-2C-3C => (contains {:2
                         {:A {:piece "B", :moves '([:1 :A] [:2 :B] [:3 :A]), :eats '([:2 :C] [:4 :A])},
                          :B
                          {:piece "B",
                           :moves '([:1 :A] [:1 :B] [:1 :C] [:2 :A] [:2 :C] [:3 :A] [:3 :B] [:3 :C]),
                           :eats '([:2 :D] [:4 :B] [:4 :D])},
                          :C {:piece " ", :moves '([:1 :C] [:2 :B] [:2 :D] [:3 :C]), :eats '([:2 :A] [:2 :E] [:4 :C])},
                          :D
                          {:piece "B",
                           :moves '([:1 :C] [:1 :D] [:1 :E] [:2 :C] [:2 :E] [:3 :C] [:3 :D] [:3 :E]),
                           :eats '([:2 :B] [:4 :B] [:4 :D])},
                          :E {:piece "B", :moves '([:1 :E] [:2 :D] [:3 :E]), :eats '([:2 :C] [:4 :E])}}})
      board5-2C-3C => (contains {:3
                                  {:A {:piece "B", :moves '([:2 :A] [:2 :B] [:3 :B] [:4 :A] [:4 :B]), :eats '([:1 :A] [:1 :C] [:3 :C] [:5 :A] [:5 :C])},
                                   :B {:piece "B", :moves '([:2 :B] [:3 :A] [:3 :C] [:4 :B]), :eats '([:1 :B] [:3 :D] [:5 :B])},
                                   :C
                                   {:piece "B",
                                    :moves '([:2 :B] [:2 :C] [:2 :D] [:3 :B] [:3 :D] [:4 :B] [:4 :C] [:4 :D]),
                                    :eats '([:1 :A] [:1 :C] [:1 :E] [:3 :A] [:3 :E] [:5 :A] [:5 :C] [:5 :E])},
                                   :D {:piece "R", :moves '([:2 :D] [:3 :C] [:3 :E] [:4 :D]), :eats '([:1 :D] [:3 :B] [:5 :D])},
                                   :E {:piece "R", :moves '([:2 :D] [:2 :E] [:3 :D] [:4 :D] [:4 :E]), :eats '([:1 :C] [:1 :E] [:3 :C] [:5 :C] [:5 :E])}}}))

(fact "move-piece-computer Moving a piece (computer, eating)" 
      board5-2C-3C-4C-2C => (contains {:2
                                             {:A {:piece "B", :moves '([:1 :A] [:2 :B] [:3 :A]), :eats '([:2 :C] [:4 :A])},
                                              :B
                                              {:piece "B",
                                               :moves '([:1 :A] [:1 :B] [:1 :C] [:2 :A] [:2 :C] [:3 :A] [:3 :B] [:3 :C]),
                                               :eats '([:2 :D] [:4 :B] [:4 :D])},
                                              :C {:piece "R", :moves '([:1 :C] [:2 :B] [:2 :D] [:3 :C]), :eats '([:2 :A] [:2 :E] [:4 :C])},
                                              :D
                                              {:piece "B",
                                               :moves '([:1 :C] [:1 :D] [:1 :E] [:2 :C] [:2 :E] [:3 :C] [:3 :D] [:3 :E]),
                                               :eats '([:2 :B] [:4 :B] [:4 :D])},
                                              :E {:piece "B", :moves '([:1 :E] [:2 :D] [:3 :E]), :eats '([:2 :C] [:4 :E])}},
                                             :3
                                             {:A {:piece "B", :moves '([:2 :A] [:2 :B] [:3 :B] [:4 :A] [:4 :B]), :eats '([:1 :A] [:1 :C] [:3 :C] [:5 :A] [:5 :C])},
                                              :B {:piece "B", :moves '([:2 :B] [:3 :A] [:3 :C] [:4 :B]), :eats '([:1 :B] [:3 :D] [:5 :B])},
                                              :C
                                              {:piece " ",
                                               :moves '([:2 :B] [:2 :C] [:2 :D] [:3 :B] [:3 :D] [:4 :B] [:4 :C] [:4 :D]),
                                               :eats '([:1 :A] [:1 :C] [:1 :E] [:3 :A] [:3 :E] [:5 :A] [:5 :C] [:5 :E])},
                                              :D {:piece "R", :moves '([:2 :D] [:3 :C] [:3 :E] [:4 :D]), :eats '([:1 :D] [:3 :B] [:5 :D])},
                                              :E {:piece "R", :moves '([:2 :D] [:2 :E] [:3 :D] [:4 :D] [:4 :E]), :eats '([:1 :C] [:1 :E] [:3 :C] [:5 :C] [:5 :E])}},
                                             :4
                                             {:A {:piece "R", :moves '([:3 :A] [:4 :B] [:5 :A]), :eats '([:2 :A] [:4 :C])},
                                              :B
                                              {:piece "R",
                                               :moves '([:3 :A] [:3 :B] [:3 :C] [:4 :A] [:4 :C] [:5 :A] [:5 :B] [:5 :C]),
                                               :eats '([:2 :B] [:2 :D] [:4 :D])},
                                              :C {:piece " ", :moves '([:3 :C] [:4 :B] [:4 :D] [:5 :C]), :eats '([:2 :C] [:4 :A] [:4 :E])},
                                              :D
                                              {:piece "R",
                                               :moves '([:3 :C] [:3 :D] [:3 :E] [:4 :C] [:4 :E] [:5 :C] [:5 :D] [:5 :E]),
                                               :eats '([:2 :B] [:2 :D] [:4 :B])},
                                              :E {:piece "R", :moves '([:3 :E] [:4 :D] [:5 :E]), :eats '([:2 :E] [:4 :C])}}}))
(let [board5-2C-3C-indicator 
      (apply-move-indicator "2C-3C" board5 
                            "B" 5 "stanmil")]
  (fact "apply-move-indicator Moving a piece (human, not eating)"
        (vector? board5-2C-3C-indicator) => false
        (= board5-2C-3C board5-2C-3C-indicator) => true))

(let [board5-2C-3C-4C-2C-indicator
      (apply-move-indicator "4C-2C" board5-2C-3C
                            "R" 5 "stanmil")]
  (fact "apply-move-indicator Moving a piece (computer, eating)"
        (vector? board5-2C-3C-4C-2C-indicator) => true
        (last board5-2C-3C-4C-2C-indicator) => "eaten"
        (= board5-2C-3C-4C-2C (first board5-2C-3C-4C-2C-indicator
                                     ))=> true))

(fact "Find the best move"
      (find-best-move board5-2C-3C 
                      "R" 5) => {"4C-2C" 1.5}
      (find-best-move board5-2C-3C-4C-2C-2C-3C-1C-2C 
                      "R" 5) => {"3E-1C" 1.5}))