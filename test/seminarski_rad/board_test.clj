(ns seminarski-rad.board-test
  (:require [seminarski-rad.board :refer :all]
            [midje.sweet :refer :all]))

(fact "Game logic facts part 1 wrong input"
      (game-logic "") => falsey
      (game-logic nil) => falsey
      (game-logic 1) => falsey
      (game-logic "1A") => falsey
      (game-logic "") => falsey
      (game-logic "1A-2B-3C") => falsey
      (game-logic "101A-102A") => true)

(fact "Game logic facts part 2 non-eating"
      (game-logic "1A-2A") => true
      (game-logic "2A-1A") => true
      (game-logic "1A-1B") => true
      (game-logic "1B-1A") => true
      (game-logic "1A-2B") => true
      (game-logic "2B-1A") => true
      (game-logic "1C-2B") => true
      (game-logic "2B-1C") => true)

(fact "Game logic facts part 3 eating"
      (game-logic "1A-3A") => "eat"
      (game-logic "3A-1A") => "eat"
      (game-logic "1A-1C") => "eat"
      (game-logic "1C-1A") => "eat"
      (game-logic "1A-3C") => "eat"
      (game-logic "3C-1A") => "eat"
      (game-logic "1A-3A") => "eat"
      (game-logic "3A-1C") => "eat")

(fact "Game logic facts part 4 too long or 
       no line on board present"
      (game-logic "1B-2A") => false
      (game-logic "2A-1B") => false
      (game-logic "1B-3D") => false
      (game-logic "1B-1B") => falsey
      (game-logic "1A-1D") => false
      (game-logic "1D-1A") => false
      (game-logic "1A-4A") => false
      (game-logic "4A-1A") => false
      (game-logic "1A-4D") => false
      (game-logic "4D-1A") => false
      (game-logic "1B-4E") => false
      (game-logic "1D-4A") => false
      (game-logic "1A-4C") => false
      (game-logic "4C-1A") => false)

(fact "Game logic facts part 5 big board"
      (game-logic "9A-11A") => "eat"
      (game-logic "99A-101A") => "eat"
      (game-logic "9A-13A") => false
      (game-logic "45Y-46Y") => true)

(fact "Game logic facts part 6 weird edge cases"
      (game-logic "1A-2C") => false
      (game-logic "2C-1A") => false
      (game-logic "1A-3B") => false
      (game-logic "3B-1A") => false
      (game-logic "1C-2A") => false
      (game-logic "2A-1C") => false
      (game-logic "1B-3A") => false
      (game-logic "3A-1B") => false)

;;(provided (include? (plain "Prefix Content Suffix")))
(let [board5 (create-board 5)]
  (fact "Default size (5) board creation"
        board5 =not=> nil
        board5 => (has every? map?)
        board5 => (contains {:1 {:A {:eats '([:1 :C] [:3 :A] [:3 :C])
                                     :moves '([:1 :B] [:2 :A] [:2 :B])
                                     :piece "B"}
                                 :B {:eats '([:1 :D] [:3 :B]) :moves '([:1 :A] [:1 :C] [:2 :B]) :piece "B"}
                                 :C {:eats '([:1 :A] [:1 :E] [:3 :A] [:3 :C] [:3 :E])
                                     :moves '([:1 :B] [:1 :D] [:2 :B] [:2 :C] [:2 :D])
                                     :piece "B"}
                                 :D {:eats '([:1 :B] [:3 :D]) :moves '([:1 :C] [:1 :E] [:2 :D]) :piece "B"}
                                 :E {:eats '([:1 :C] [:3 :C] [:3 :E])
                                     :moves '([:1 :D] [:2 :D] [:2 :E])
                                     :piece "B"}}})
        
       board5 => (contains {:3 {:A {:eats '([:1 :A] [:1 :C] [:3 :C] [:5 :A] [:5 :C])
                              :moves '([:2 :A] [:2 :B] [:3 :B] [:4 :A] [:4 :B])
                              :piece "B"}
                          :B {:eats '([:1 :B] [:3 :D] [:5 :B])
                              :moves '([:2 :B] [:3 :A] [:3 :C] [:4 :B])
                              :piece "B"}
                          :C {:eats '([:1 :A] [:1 :C] [:1 :E] [:3 :A] [:3 :E] [:5 :A] [:5 :C] [:5 :E])
                              :moves '([:2 :B] [:2 :C] [:2 :D] [:3 :B] [:3 :D] [:4 :B] [:4 :C] [:4 :D])
                              :piece " "}
                          :D {:eats '([:1 :D] [:3 :B] [:5 :D])
                              :moves '([:2 :D] [:3 :C] [:3 :E] [:4 :D])
                              :piece "R"}
                          :E {:eats '([:1 :C] [:1 :E] [:3 :C] [:5 :C] [:5 :E])
                              :moves '([:2 :D] [:2 :E] [:3 :D] [:4 :D] [:4 :E])
                              :piece "R"}}})
        
        board5 => (contains {:5 {:A {:eats '([:3 :A] [:3 :C] [:5 :C])
                                               :moves '([:4 :A] [:4 :B] [:5 :B])
                                               :piece "R"}
                                           :B {:eats '([:3 :B] [:5 :D]) :moves '([:4 :B] [:5 :A] [:5 :C]) :piece "R"}
                                           :C {:eats '([:3 :A] [:3 :C] [:3 :E] [:5 :A] [:5 :E])
                                               :moves '([:4 :B] [:4 :C] [:4 :D] [:5 :B] [:5 :D])
                                               :piece "R"}
                                           :D {:eats '([:3 :D] [:5 :B]) :moves '([:4 :D] [:5 :C] [:5 :E]) :piece "R"}
                                           :E {:eats '([:3 :C] [:3 :E] [:5 :C])
                                               :moves '([:4 :D] [:4 :E] [:5 :D])
                                               :piece "R"}}})))

(let [board7 (create-board 7)]
(fact "Standard size (7) board creation" 
            board7 =not=> nil
            board7 => (has every? map?)
            board7 => (contains {:1 {:A {:piece "B", :moves '([:1 :B] [:2 :A] [:2 :B]) 
                                         :eats '([:1 :C] [:3 :A] [:3 :C])} 
                                     :B {:piece "B", :moves '([:1 :A] [:1 :C] [:2 :B]) 
                                         :eats '([:1 :D] [:3 :B])} 
                                     :C {:piece "B", :moves '([:1 :B] [:1 :D] [:2 :B] [:2 :C] [:2 :D]) 
                                         :eats '([:1 :A] [:1 :E] [:3 :A] [:3 :C] [:3 :E])} 
                                     :D {:piece "B", :moves '([:1 :C] [:1 :E] [:2 :D]) 
                                         :eats '([:1 :B] [:1 :F] [:3 :D])} 
                                     :E {:piece "B", :moves '([:1 :D] [:1 :F] [:2 :D] [:2 :E] [:2 :F]) 
                                         :eats '([:1 :C] [:1 :G] [:3 :C] [:3 :E] [:3 :G])} 
                                     :F {:piece "B", :moves '([:1 :E] [:1 :G] [:2 :F]),
                                         :eats '([:1 :D] [:3 :F])} 
                                     :G {:piece "B", :moves '([:1 :F] [:2 :F] [:2 :G]) 
                                         :eats '([:1 :E] [:3 :E] [:3 :G])}}})
            board7 => (contains {:4 {:A {:piece "B", :moves '([:3 :A] [:4 :B] [:5 :A]), :eats '([:2 :A] [:4 :C] [:6 :A])} 
                                     :B 
                                     {:piece "B", 
                                      :moves '([:3 :A] [:3 :B] [:3 :C] [:4 :A] [:4 :C] [:5 :A] [:5 :B] [:5 :C]), 
                                      :eats '([:2 :B] [:2 :D] [:4 :D] [:6 :B] [:6 :D])}, 
                                     :C {:piece "B", :moves '([:3 :C] [:4 :B] [:4 :D] [:5 :C]), :eats '([:2 :C] [:4 :A] [:4 :E] [:6 :C])}, 
                                     :D 
                                     {:piece " ", 
                                      :moves '([:3 :C] [:3 :D] [:3 :E] [:4 :C] [:4 :E] [:5 :C] [:5 :D] [:5 :E]),
                                      :eats '([:2 :B] [:2 :D] [:2 :F] [:4 :B] [:4 :F] [:6 :B] [:6 :D] [:6 :F])}, 
                                     :E {:piece "R", :moves '([:3 :E] [:4 :D] [:4 :F] [:5 :E]), :eats '([:2 :E] [:4 :C] [:4 :G] [:6 :E])}, 
                                     :F 
                                     {:piece "R", 
                                      :moves '([:3 :E] [:3 :F] [:3 :G] [:4 :E] [:4 :G] [:5 :E] [:5 :F] [:5 :G]), 
                                      :eats '([:2 :D] [:2 :F] [:4 :D] [:6 :D] [:6 :F])},
                                            :G {:piece "R", :moves '([:3 :G] [:4 :F] [:5 :G]), :eats '([:2 :G] [:4 :E] [:6 :G])}}})
            board7 => (contains {:7 
                                 {:A {:piece "R", :moves '([:6 :A] [:6 :B] [:7 :B]), 
                                      :eats '([:5 :A] [:5 :C] [:7 :C])}, 
                                  :B {:piece "R", :moves '([:6 :B] [:7 :A] [:7 :C]), 
                                      :eats '([:5 :B] [:7 :D])}, 
                                  :C {:piece "R", :moves '([:6 :B] [:6 :C] [:6 :D] [:7 :B] [:7 :D]), 
                                      :eats '([:5 :A] [:5 :C] [:5 :E] [:7 :A] [:7 :E])}, 
                                  :D {:piece "R", :moves '([:6 :D] [:7 :C] [:7 :E]), 
                                      :eats '([:5 :D] [:7 :B] [:7 :F])}, 
                                  :E {:piece "R", :moves '([:6 :D] [:6 :E] [:6 :F] [:7 :D] [:7 :F]), 
                                      :eats '([:5 :C] [:5 :E] [:5 :G] [:7 :C] [:7 :G])}, 
                                  :F {:piece "R", :moves '([:6 :F] [:7 :E] [:7 :G]), 
                                      :eats '([:5 :F] [:7 :D])}, 
                                  :G {:piece "R", :moves '([:6 :F] [:6 :G] [:7 :F]),  
                                      :eats '([:5 :E] [:5 :G] [:7 :E])}}})))
(let [board9 (create-board 9)]
        (fact "Standard size (7) board creation" 
              board9 =not=> nil
              board9 => (has every? map?)
              board9 => (contains {:1
                                   {:I {:piece "B", :moves '([:1 :H] [:2 :H] [:2 :I]),
                                        :eats '([:1 :G] [:3 :G] [:3 :I])},
                                    :A {:piece "B", :moves '([:1 :B] [:2 :A] [:2 :B]),
                                        :eats '([:1 :C] [:3 :A] [:3 :C])},
                                    :F {:piece "B", :moves '([:1 :E] [:1 :G] [:2 :F]),
                                        :eats '([:1 :D] [:1 :H] [:3 :F])},
                                    :D {:piece "B", :moves '([:1 :C] [:1 :E] [:2 :D]),
                                        :eats '([:1 :B] [:1 :F] [:3 :D])},
                                    :B {:piece "B", :moves '([:1 :A] [:1 :C] [:2 :B]),
                                        :eats '([:1 :D] [:3 :B])},
                                    :C {:piece "B", :moves '([:1 :B] [:1 :D] [:2 :B] [:2 :C] [:2 :D]), 
                                        :eats '([:1 :A] [:1 :E] [:3 :A] [:3 :C] [:3 :E])},
                                    :E {:piece "B", :moves '([:1 :D] [:1 :F] [:2 :D] [:2 :E] [:2 :F]),
                                        :eats '([:1 :C] [:1 :G] [:3 :C] [:3 :E] [:3 :G])},
                                    :G {:piece "B", :moves '([:1 :F] [:1 :H] [:2 :F] [:2 :G] [:2 :H]),
                                        :eats '([:1 :E] [:1 :I] [:3 :E] [:3 :G] [:3 :I])},
                                    :H {:piece "B", :moves '([:1 :G] [:1 :I] [:2 :H]),
                                        :eats '([:1 :F] [:3 :H])}}})
              board9 => (contains {:5
                                   {:I {:piece "R", :moves '([:4 :H] [:4 :I] [:5 :H] [:6 :H] [:6 :I]), :eats '([:3 :G] [:3 :I] [:5 :G] [:7 :G] [:7 :I])},
                                    :A {:piece "B", :moves '([:4 :A] [:4 :B] [:5 :B] [:6 :A] [:6 :B]), :eats '([:3 :A] [:3 :C] [:5 :C] [:7 :A] [:7 :C])},
                                    :F {:piece "R", :moves '([:4 :F] [:5 :E] [:5 :G] [:6 :F]), :eats '([:3 :F] [:5 :D] [:5 :H] [:7 :F])},
                                    :D {:piece "B", :moves '([:4 :D] [:5 :C] [:5 :E] [:6 :D]), :eats '([:3 :D] [:5 :B] [:5 :F] [:7 :D])},
                                    :B {:piece "B", :moves '([:4 :B] [:5 :A] [:5 :C] [:6 :B]), :eats '([:3 :B] [:5 :D] [:7 :B])},
                                    :C
                                    {:piece "B",
                                     :moves '([:4 :B] [:4 :C] [:4 :D] [:5 :B] [:5 :D] [:6 :B] [:6 :C] [:6 :D]),
                                     :eats '([:3 :A] [:3 :C] [:3 :E] [:5 :A] [:5 :E] [:7 :A] [:7 :C] [:7 :E])},
                                    :E
                                    {:piece " ",
                                     :moves '([:4 :D] [:4 :E] [:4 :F] [:5 :D] [:5 :F] [:6 :D] [:6 :E] [:6 :F]),
                                     :eats '([:3 :C] [:3 :E] [:3 :G] [:5 :C] [:5 :G] [:7 :C] [:7 :E] [:7 :G])},
                                    :G
                                    {:piece "R",
                                     :moves '([:4 :F] [:4 :G] [:4 :H] [:5 :F] [:5 :H] [:6 :F] [:6 :G] [:6 :H]),
                                     :eats '([:3 :E] [:3 :G] [:3 :I] [:5 :E] [:5 :I] [:7 :E] [:7 :G] [:7 :I])},
                                    :H {:piece "R", :moves '([:4 :H] [:5 :G] [:5 :I] [:6 :H]), :eats '([:3 :H] [:5 :F] [:7 :H])}}})
              board9 => (contains {:9
                                   {:I {:piece "R", :moves '([:8 :H] [:8 :I] [:9 :H]), :eats '([:7 :G] [:7 :I] [:9 :G])},
                                    :A {:piece "R", :moves '([:8 :A] [:8 :B] [:9 :B]), :eats '([:7 :A] [:7 :C] [:9 :C])},
                                    :F {:piece "R", :moves '([:8 :F] [:9 :E] [:9 :G]), :eats '([:7 :F] [:9 :D] [:9 :H])},
                                    :D {:piece "R", :moves '([:8 :D] [:9 :C] [:9 :E]), :eats '([:7 :D] [:9 :B] [:9 :F])},
                                    :B {:piece "R", :moves '([:8 :B] [:9 :A] [:9 :C]), :eats '([:7 :B] [:9 :D])},
                                    :C {:piece "R", :moves '([:8 :B] [:8 :C] [:8 :D] [:9 :B] [:9 :D]), :eats '([:7 :A] [:7 :C] [:7 :E] [:9 :A] [:9 :E])},
                                    :E {:piece "R", :moves '([:8 :D] [:8 :E] [:8 :F] [:9 :D] [:9 :F]), :eats '([:7 :C] [:7 :E] [:7 :G] [:9 :C] [:9 :G])},
                                    :G {:piece "R", :moves '([:8 :F] [:8 :G] [:8 :H] [:9 :F] [:9 :H]), :eats '([:7 :E] [:7 :G] [:7 :I] [:9 :E] [:9 :I])},
                                    :H {:piece "R", :moves '([:8 :H] [:9 :G] [:9 :I]), :eats '([:7 :H] [:9 :F])}}})))
