(ns seminarski-rad.statistics
  (:require [seminarski-rad.input-utility :as util]
            [seminarski-rad.database :as db]))

(def game-sessions-info (atom []))

(defn repopulate-game-sessions!
  []
  (let [future-result (future (db/find-game-sessions-info
                               (db/get-connection)))]
    (reset! game-sessions-info @future-result))
  nil)

(defn type?-wins
  [player-type]
 (filter #(= player-type (get % :game_session/won))
               @game-sessions-info))

(defn wins-board-size?-type?
  [board-size player-type]
  (filter #(and (= player-type (get % :game_session/won))
                       (= board-size (get % :board/size)))
                 @game-sessions-info))

(defn score-for-type?-wins
  [player-type]
  (map #(select-keys % [:game_session/human_score
                        :game_session/computer_score])
       (filter #(= player-type (get % :game_session/won))
               @game-sessions-info)))

(defn sessions-for-user
  [username]
  (filter #(= username (get % :app_user/username))
          @game-sessions-info))

(defn- get-map-human-wins-helper
 [a-win human]
  (let [human-username-kw (keyword (str (:app_user/username
                                         human)))]
    (if (some #(= % human-username-kw) (keys a-win))
      (update a-win human-username-kw inc)
      (assoc a-win human-username-kw 1))))

(defn get-map-human-wins-added
  []
  (let [all-human-wins (type?-wins "H")]
    (reduce get-map-human-wins-helper {} all-human-wins)))

(defn sort-by-win-count
  [map-human-wins]
  (let [wins-vector (vec map-human-wins)]
    (sort-by (comp > last) wins-vector)))

(defn- calculate-percentage
  [fraction total]
  ;; fraction/total * 100
  (if (= 0 total)
    100.0
    (if (= 0 fraction)
      0.0
      (Double/parseDouble (format "%.2f" (* 100 (/ (float fraction)
                                                   total)))))))
(defn win-ratio-human-by-board-size
  []
  (let [human-wins-5 (count (wins-board-size?-type? 5 "H")) 
        human-wins-7 (count (wins-board-size?-type? 7 "H")) 
        human-wins-9 (count (wins-board-size?-type? 9 "H")) 
        total (count @game-sessions-info)]
    {:5 (calculate-percentage human-wins-5 total)
             :7 (calculate-percentage human-wins-7 total)
             :9 (calculate-percentage human-wins-9 total)}))

(defn- distinct-users-who-played
  []
  (distinct (reduce (fn 
                      [acc session]
                      (conj acc (:app_user/username session)))
                    [] @game-sessions-info)))

(defn- colors-helper
  [acc a-session]
  (let [username-kw (keyword (str (:app_user/username a-session)))
        human-color-kw (keyword (:game_session/human_color
                                 a-session))
        computer-color-kw (keyword (util/opposite-player-color
                                    (:game_session/human_color
                                     a-session)))]
    (if (some #(= % username-kw) (keys acc))
      (update-in acc [username-kw human-color-kw] inc) 
        (assoc acc username-kw {human-color-kw 1
                                computer-color-kw 0}))))

(defn colors-played-by-username
  []
  (reduce colors-helper {} @game-sessions-info))

(defn- quits-helper
  [acc a-session]
  (if (and (= (:game_session/won a-session) "C")
           (> (:game_session/human_score a-session)0))
    (let [username-kw (keyword (:app_user/username a-session))]
      (if (some #(= % username-kw) (keys acc))
        (update acc username-kw inc)
        (assoc acc username-kw 1)
        )) acc))

(defn quits-by-users
  []
  (reduce quits-helper {} @game-sessions-info))

(defn- resolve-leaderboard
  [leaderboard]
  (let [parsed (take 3 (for [[k v] leaderboard]
                       (str (name k) ": " v "\n")))]
    (apply str parsed)))

(defn- resolve-colors-map
  [colors-played-map]
  (let [parsed (for [[k v] colors-played-map]
                 (str (name k) ": " (:R v)
                      "[R] " (:B v) "[B]\n"))]
    (apply str parsed)))

(defn- resolve-quits-map
  [quits-map]
  (let [parsed (for [[k v] quits-map]
                 (str (name k) ": " v "\n"))]
    (apply str parsed)))

(defn spit-all-contents
  []
  (repopulate-game-sessions!)
  (let [
        human-wins (type?-wins "H")
        computer-wins (type?-wins "C")
        win-ratio-map (win-ratio-human-by-board-size )
        leaderboard (sort-by-win-count 
                     (get-map-human-wins-added))
        colors-played-map (colors-played-by-username)
        quits-map (quits-by-users)
        contents (str "******************ALL WINS******************\n\n"
                      "HUMAN: " (count human-wins) "\n"
                      "COMPUTER: " (count computer-wins) "\n\n"
                      "*************WINS BY BOARD SIZE*************\n\n"
                      "SIZE 5: HUMAN: " (:5 win-ratio-map) "% "
                      "COMPUTER: " (- 100 (:5 win-ratio-map)) "% \n"
                      "SIZE 7: HUMAN: " (:7 win-ratio-map) "% "
                      "COMPUTER: " (- 100 (:7 win-ratio-map)) "% \n"
                      "SIZE 9: HUMAN: " (:9 win-ratio-map) "% "
                      "COMPUTER: " (- 100 (:9 win-ratio-map)) "% \n\n"
                      "****************LEADERBOARD****************\n\n"
                      (resolve-leaderboard leaderboard) "\n"
                      "***************COLORS BY USER***************\n\n"
                      (resolve-colors-map colors-played-map) "\n"
                      "***************QUITS BY USER***************\n\n"
                      (resolve-quits-map quits-map) "\n")]
                      
                      (spit "statistics.txt" contents)))