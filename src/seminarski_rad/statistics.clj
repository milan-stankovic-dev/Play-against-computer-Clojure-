(ns seminarski-rad.statistics
  (:require [seminarski-rad.input-utility :as util]
            [seminarski-rad.database :as db]))

(def game-sessions-info (atom []))

(defn repopulate-game-sessions!
  "Resets the 'game-sessions-info' atom with new data from the database."
  []
  (let [future-result (future (db/find-game-sessions-info
                               (db/get-connection)))]
    (reset! game-sessions-info @future-result))
  nil)

(defn type?-wins
  "Filters all wins for specified player type (C or H)."
  [player-type]
  (if-not (string? player-type)
    '()
    (let [type-fixed (util/purify-user-input player-type)]
      (filter #(= type-fixed (get % :GAME_SESSION/WON))
              @game-sessions-info))))

(defn wins-board-size?-type?
  "Filters all wins for specified player type (C or H)
   and board size."
  [board-size player-type]
  (filter #(and (= player-type (get % :GAME_SESSION/WON))
                       (= board-size (get % :BOARD/SIZE)))
                 @game-sessions-info))

(defn score-for-type?-wins
  "For all wins of specified type, provides information of 
   human score and computer score."
  [player-type]
  (if-not (string? player-type)
    '()
    (let [type-fixed (util/purify-user-input player-type)]
      (map #(select-keys % [:GAME_SESSION/HUMAN_SCORE
                            :GAME_SESSION/COMPUTER_SCORE])
           (filter #(= type-fixed (get % :GAME_SESSION/WON))
                   @game-sessions-info)))))

(defn sessions-for-user
  "Filters all sessions for specified user."
  [username]
  (filter #(= username (get % :APP_USER/USERNAME))
          @game-sessions-info))

(defn- get-map-human-?s-helper 
  "Helper function for get-map-human-?s-added's reduce
   function."
  [a-win human]
  (let [human-username-kw (keyword (str (:APP_USER/USERNAME
                                         human)))]
    (if (some #(= % human-username-kw) (keys a-win))
      (update a-win human-username-kw inc)
      (assoc a-win human-username-kw 1))))

(defn get-map-human-?s-added
  "Returns a map with aggregated win/loss count for all
   human players."
  [wins-or-losses]
  (if-not (string? wins-or-losses)
    {}
    (let [fixed-input (util/purify-user-input 
                       wins-or-losses)
          player-type (cond 
                        (= fixed-input "WINS") "H"
                        (= fixed-input "LOSSES") "C"
                        :else nil)
          all-?-wins (type?-wins player-type)]
      (reduce get-map-human-?s-helper {} all-?-wins))))

(defn sort-by-win-count
  "Takes a map of all human wins and returns a sorted list
   of all aggregated wins, as vector entries within that list.
   If the argument is not a map, returns an empty list."
  [map-human-wins]
  (if-not (map? map-human-wins)
    '()
    (sort-by (comp > last) map-human-wins)))

(defn- calculate-percentage
  "Takes in a fraction and a total and converts it into a percentage.
   Rounds to two digits."
  [fraction total]
  ;; fraction/total * 100
  (if (= 0 total)
    100.0
    (if (= 0 fraction)
      0.0
      (Double/parseDouble (format "%.2f" (* 100 (/ (float fraction)
                                                   total)))))))
(defn win-ratio-human-by-board-size
  "Calculates the win/loss ratio for human players by board size.
   Only calculates the values for regular board sizes (5, 7 and 9)."
  []
  (let [human-wins-5 (count (wins-board-size?-type? 5 "H")) 
        human-wins-7 (count (wins-board-size?-type? 7 "H")) 
        human-wins-9 (count (wins-board-size?-type? 9 "H")) 
        total (count @game-sessions-info)]
    {:5 (calculate-percentage human-wins-5 total)
             :7 (calculate-percentage human-wins-7 total)
             :9 (calculate-percentage human-wins-9 total)}))

(defn distinct-users-who-played
  "Returns a list of all distinct users who have played at 
   least 1 game session."
  []
  (distinct (reduce (fn 
                      [acc session]
                      (conj acc (:APP_USER/USERNAME session)))
                    [] @game-sessions-info)))

(defn- colors-helper
  "Helper function for 'colors-played-by-username''s reduce
   function."
  [acc a-session]
  (let [username-kw (keyword (str (:APP_USER/USERNAME a-session)))
        human-color-kw (keyword (:GAME_SESSION/HUMAN_COLOR
                                 a-session))
        computer-color-kw (keyword (util/opposite-player-color
                                    (:GAME_SESSION/HUMAN_COLOR
                                     a-session)))]
    (if (some #(= % username-kw) (keys acc))
      (update-in acc [username-kw human-color-kw] inc) 
        (assoc acc username-kw {human-color-kw 1
                                computer-color-kw 0}))))

(defn colors-played-by-username
  "Returns a hash map with all distinct users who have played
   the game at least once as keys, with values as hash maps with
   keys for all the possible piece colors (B and R), with the 
   number of times the user has played as that color."
  []
  (reduce colors-helper {} @game-sessions-info))

(defn- quits-helper
  "Helper function for 'quits-by-users''s reduce function."
  [acc a-session]
  (if (and (= (:GAME_SESSION/WON a-session) "C")
           (> (:GAME_SESSION/HUMAN_SCORE a-session)0))
    (let [username-kw (keyword (:APP_USER/USERNAME a-session))]
      (if (some #(= % username-kw) (keys acc))
        (update acc username-kw inc)
        (assoc acc username-kw 1)
        )) acc))

(defn quits-by-users
  "Returns a hash map with all user quits for players that have
   quit at least once."
  []
  (reduce quits-helper {} @game-sessions-info))

(defn- resolve-leaderboard
  "Returns parsed leaderboard string for user display."
  [leaderboard]
  (let [parsed (take 3 (for [[k v] leaderboard]
                       (str (name k) ": " v "\n")))]
    (apply str parsed)))

(defn- resolve-colors-map
  "Returns parsed colors played string for user display."
  [colors-played-map]
  (let [parsed (for [[k v] colors-played-map]
                 (str (name k) ": " (:R v)
                      "[R] " (:B v) "[B]\n"))]
    (apply str parsed)))

(defn- resolve-quits-map
  "Returns parsed quits string for user display."
  [quits-map]
  (let [parsed (for [[k v] quits-map]
                 (str (name k) ": " v "\n"))]
    (apply str parsed)))

(defn- resolve-wins-ratio-map
  "Returns parsed win ratio string for user display."
  [wins-ratio-map]
  (let [parsed (for [[k v] wins-ratio-map]
                 (str "SIZE " (name k) " HUMAN: "
                      v "%\nCOMPUTER: " (- 100 v)
                      "%" "\n"))]
    (apply str parsed)))

(defn spit-all-contents
  "Saves all statistics to file called 'statistics.txt'."
  []
  (repopulate-game-sessions!)
  (let [human-wins (type?-wins "H")
        computer-wins (type?-wins "C")
        win-ratio-map (win-ratio-human-by-board-size )
        leaderboard (sort-by-win-count 
                     (get-map-human-?s-added "WINS"))
        colors-played-map (colors-played-by-username)
        quits-map (quits-by-users)
        contents (str "******************ALL WINS******************\n\n"
                      "HUMAN: " (count human-wins) "\n"
                      "COMPUTER: " (count computer-wins) "\n\n"
                      "*************WINS BY BOARD SIZE*************\n\n"
                      (resolve-wins-ratio-map win-ratio-map) "\n"
                      "****************LEADERBOARD****************\n\n"
                      (resolve-leaderboard leaderboard) "\n"
                      "***************COLORS BY USER***************\n\n"
                      (resolve-colors-map colors-played-map) "\n"
                      "***************QUITS BY USER***************\n\n"
                      (resolve-quits-map quits-map) "\n")]
                      
                      (spit "statistics.txt" contents)))