(ns seminarski-rad.database
  (:require [next.jdbc :as jdbc]
            [buddy.hashers :as hashing]
            [seminarski-rad.validator :as val]
            [seminarski-rad.input-utility :as utility]))

(def ^:private db
  {:dbtype "mysql"
   :dbname "seminarski-rad"
   :user "root"
   :password ""
   :host "localhost"
   :port 3306})

(def ^:private datasource (jdbc/get-datasource db))

(defn get-connection 
  []
  (jdbc/get-connection datasource))

(defn- hash-a-password
  [password]
  (hashing/derive password))

(defn hashed-password-correct?
  [password hashed-pass]
  (hashing/check password hashed-pass))

(defn find-user-by-username
  [conn username]
  (let [db-result (jdbc/execute! 
                   conn
                   ["SELECT * FROM app_user
                     WHERE USERNAME = (?)" username])]
    (when db-result
      (first db-result))))

;; ((find-user-by-username (get-connection) "stanmil") :app_user/password)

(defn register-user
  [conn username password]
  (if-not (= [] (find-user-by-username conn username)) 
    (do (println "User exists. Try again.")
        false)
    (do
      (jdbc/execute!
       conn ["INSERT INTO app_user (username, password) VALUES (?, ?)"
             username (hash-a-password password)])
      (.close conn)
      true)))

;; (register-user (get-connection) "stanmil" "123abc")
;; (register-user (get-connection) "ppetar" "abc123")
;; (register-user (get-connection) "saraa" "a1b2c3")

(defn login-user 
  [conn username password]
  (let [db-user (find-user-by-username conn username)]
     (when (hashed-password-correct? password 
                                     (:app_user/password db-user))
       db-user)))

;; (login-user (get-connection) "STANMIL" "123abc")

(defn- find-board-by-size
  [conn size]
  (when (number? size)
    (let [db-result (jdbc/execute!
                     conn
                     ["SELECT * FROM board WHERE size = (?)" size])]
      (when db-result
        (first db-result)))))

;; (find-board-by-size (get-connection) 5)

(defn- insert-board
  [conn size]
  (when (and (number? size)
             (not (find-board-by-size conn size)))
        (jdbc/execute!
         conn
         ["INSERT INTO board (size) VALUES (?)" size])))

;; (insert-board (get-connection) 3)
;; (insert-board (get-connection) 5)
;; (insert-board (get-connection) 7)

(defn- insert-game-session-clean
  [conn board-size username
   who-won human-score
   computer-score human-color] 
    (let [app-user-id (:app_user/id
                       (find-user-by-username conn username))
          board-id (:board/id
                    (find-board-by-size conn board-size))]
      (jdbc/execute!
       conn
       ["INSERT INTO game_session (app_user_id,
       board_id, won, human_score, computer_score, human_color)
       VALUES (?, ?, ?, ?, ?, ?)"
        app-user-id board-id who-won
        human-score computer-score
        human-color])))

(defn insert-game-session
  [conn board-size username
   who-won human-score
   computer-score human-color]
  (try
    (insert-game-session-clean conn
                               board-size
                               username
                               who-won
                               human-score
                               computer-score
                               human-color)
    (println "Game session saved.")
    (catch Exception ex
      (if (= "Column 'board_id' cannot be null"
             (.getMessage ex))
        (do
          (println "You seem to be the first ever player to
                    play this board size. Would you like to save
                    it?" ["Y"] ["N"])
          (let [subseq-response (utility/purify-user-input
                                 (utility/prompt-info "your choice"
                                                      val/confirm-validator-Y-N))]
            (when (= "Y" subseq-response)
              (insert-board (get-connection) board-size)
              (println "Board inserted.")
              (insert-game-session-clean conn
                                         board-size
                                         username
                                         who-won
                                         human-score
                                         computer-score
                                         human-color))))
        (throw ex)))))

(defn find-game-sessions-info
  [conn]
  (jdbc/execute!
   conn
   ["SELECT s.id, u.id, s.won, s.human_score,
     s.computer_score, s.human_color, u.username,
     b.size
     FROM game_session s JOIN board b
     ON (s.board_id = b.id) JOIN app_user u
     ON (s.app_user_id = u.id)"]))

;; (find-all-game-sessions-with-board-size (get-connection)3)
;; (find-all-game-sessions-for-user (get-connection) "stanmil")
;; (find-all-game-sessions-stated-won (get-connection) \H)
;; (insert-game-session (get-connection) 3 "stanmil" "H" 3 0 "R")