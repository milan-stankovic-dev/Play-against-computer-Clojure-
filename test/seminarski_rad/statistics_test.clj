(ns seminarski-rad.statistics-test
  (:require [seminarski-rad.statistics :refer :all]
            [midje.sweet :refer :all]))

(reset! game-sessions-info [{:GAME_SESSION/ID 1,
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

(fact "Count wins"
      (count (type?-wins "c")) => 4
      (count (type?-wins "C")) => 4
      (count (type?-wins "   c  ")) => 4
      (count (type?-wins "C    ")) => 4
      (count (type?-wins "h")) => 3
      (count (type?-wins "H")) => 3
      (count (type?-wins "    h ")) => 3
      (count (type?-wins "  H  ")) => 3
      (count (type?-wins "ILLEGAL")) => 0 
      (type?-wins 1) => '()
      (type?-wins nil) => '())

(fact "Wins for different board sizes and player types"
      (count (wins-board-size?-type? 5 "H")) => 2
      (count (wins-board-size?-type? 5 "C")) => 2
      (count (wins-board-size?-type? 7 "C")) => 1
      (count (wins-board-size?-type? 7 "H")) => 0
      (count (wins-board-size?-type? 9 "C")) => 0
      (count (wins-board-size?-type? 9 "H")) => 1
      (count (wins-board-size?-type? 3 "C")) => 1
      (count (wins-board-size?-type? 3 "H")) => 0)

(fact "Wins for different board sizes and player types edge cases"
      (count (wins-board-size?-type? 5 "")) => 0
      (count (wins-board-size?-type? nil "")) => 0
      (count (wins-board-size?-type? nil nil)) => 0
      (wins-board-size?-type? "foo" 5) => '()
      (wins-board-size?-type? Double/NaN Double/NaN) => '())

(fact "Score for specific player type (C or H) wins"
      (count (score-for-type?-wins "C")) => 4
      (count (score-for-type?-wins "H")) => 3
      (score-for-type?-wins "H") => (just '(#:GAME_SESSION{:HUMAN_SCORE 3, :COMPUTER_SCORE 0}
                                           #:GAME_SESSION{:HUMAN_SCORE 5, :COMPUTER_SCORE 0}
                                           #:GAME_SESSION{:HUMAN_SCORE 2, :COMPUTER_SCORE 0}))
      (score-for-type?-wins "C") => (just '(#:GAME_SESSION{:HUMAN_SCORE 18, :COMPUTER_SCORE 19}
                                            #:GAME_SESSION{:HUMAN_SCORE 0, :COMPUTER_SCORE 8}
                                            #:GAME_SESSION{:HUMAN_SCORE 2, :COMPUTER_SCORE 2}
                                            #:GAME_SESSION{:HUMAN_SCORE 0, :COMPUTER_SCORE 9}))
      (= (score-for-type?-wins "  h") (score-for-type?-wins "H")) => true
      (= (score-for-type?-wins "    c  ") (score-for-type?-wins "C")) => true)
(score-for-type?-wins "C")

(fact "Score for specified player type edge cases"
      (score-for-type?-wins "FOO BAR") => '()
      (score-for-type?-wins "") => '()
      (score-for-type?-wins nil) => '()
      (score-for-type?-wins 17) => '()
      (score-for-type?-wins [1 2 3]) => '())

(fact "Filtering sessions for user"
      (count (sessions-for-user "stanmil")) => 3
      (count (sessions-for-user "saraa")) => 2
      (count (sessions-for-user "ppetar")) => 2
      (count (sessions-for-user "FOO BAR")) => 0 
      (sessions-for-user 12) => '()
      (sessions-for-user nil) => '())

(fact "Aggregated map of human wins/losses."
      (get-map-human-?s-added "WINS") => (just 
                                          {:stanmil 2, :saraa 1})
      (get-map-human-?s-added "LOSSES") => (just
                                            {:ppetar 2, :stanmil 1, :saraa 1})
      (= (get-map-human-?s-added "  wInS ")
         (get-map-human-?s-added "WINS")) => true
      (= (get-map-human-?s-added "  loSSeS ")
         (get-map-human-?s-added "LOSSES")) => true
      (get-map-human-?s-added nil) => {})

(fact "Sort map by win count"
      (sort-by-win-count (get-map-human-?s-added "WINS")) 
      => (just '([:stanmil 2] [:saraa 1]))
      (sort-by-win-count (get-map-human-?s-added "LOSSES"))
      => (just '([:ppetar 2] [:stanmil 1] [:saraa 1]))
      (sort-by-win-count (get-map-human-?s-added nil))
      => '()
      (sort-by-win-count (get-map-human-?s-added 1))
      => '()
      (sort-by-win-count nil) => '()
       (sort-by-win-count []) => '())

(fact "Win ratio for humans by board size"
      (win-ratio-human-by-board-size)
      => (just {:5 28.57, :7 0.0, :9 14.29}))

(fact "Distinct users who played"
      (distinct-users-who-played)
      => (just '("stanmil" "ppetar" "saraa")))

(fact "Colors played by username map"
      (colors-played-by-username) => (just {:stanmil {:B 2, :R 1},
                                            :ppetar {:B 1, :R 1},
                                            :saraa {:B 2, :R 0}})
      (= (set (distinct-users-who-played))
         (set (map name (keys (colors-played-by-username))))))

(fact "All quits for users"
      (quits-by-users) 
      => (just {:ppetar 1, :saraa 1}))

