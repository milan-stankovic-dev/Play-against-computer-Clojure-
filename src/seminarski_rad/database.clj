(ns seminarski-rad.database
  (:require [next.jdbc :as jdbc]
            [buddy.hashers :as hashing]))

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

(defn- initiate-table-user 
  []
  (jdbc/execute!
   datasource
   ["CREATE TABLE IF NOT EXISTS app_user 
     (id SERIAL PRIMARY KEY,
     username VARCHAR(255) NOT NULL,
     password VARCHAR(255) NOT NULL)"]))

(defn- insert-user
  [conn username password]
  (jdbc/execute!
   conn
   ["INSERT INTO app_user (username, password)
     VALUES (?, ?)" username password]))

;; (def ^:private conn (get-connection))
;; (initiate-table-user)
;; (insert-user conn "stanmil" "123abc")

(defn- hash-a-password
  [password]
  (hashing/derive password))
(hash-a-password "123abc")

(defn hashed-password-correct?
  [password hashed-pass]
  (hashing/check password hashed-pass))

(defn- find-user-by-username
  [conn username]
  (let [db-result (jdbc/execute! 
                   conn
                   ["SELECT * FROM app_user
                     WHERE USERNAME = (?)" username])]
    (when db-result
      (first db-result))))

;; ((find-user-by-username (get-connection) "stanmil") :app_user/password)

(defn- register-user
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

;; (defn- nth-of-dbres
;;   [nth-place db-res]
;;   (when db-res
;;     (nth (vals db-res) nth-place)))

;; (nth-of-dbres 0 (find-board-by-size (get-connection) 3))
;; (insert-board (get-connection) 3)
;; (insert-board (get-connection) 5)
;; (insert-board (get-connection) 7)

(defn- insert-game-session
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

(defn find-all-?
  [conn what-to-find]
  (jdbc/execute!
   conn
   [(str "SELECT * FROM " what-to-find)]))

(find-all-? (get-connection) "app_user")

;; (find-all-game-sessions (get-connection))

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

(find-game-sessions-info (get-connection) )

(defn find-all-game-sessions-for-user
  [conn username]
  (let [u-id (:app_user/id (find-user-by-username conn username))]
    (when u-id
      (jdbc/execute!
       conn
       ["SELECT
         s.computer_score, s.human_color,
         u.username, b.size
         FROM game_session s
         JOIN board b
         ON (s.board_id = b.id)
         JOIN app_user u
         ON (s.app_user_id = u.id)
         WHERE (u.id = (?))" u-id]))))

(defn find-all-game-sessions-stated-won
  "Finds relevant information for all game sessions for stated
   user type (human [H] or computer [C])"
  [conn stated-player-type]
  (when (some #(= % stated-player-type) [\H \h \C \c])
    (jdbc/execute!
     conn
     ["SELECT 
     s.computer_score, s.human_score,
     s.human_color, u.username, b.size
     FROM game_session s
     JOIN board b
     ON (s.board_id = b.id)
     JOIN app_user u
     ON (s.app_user_id = u.id)
     WHERE (s.won = ?)" (str stated-player-type)])))

;; (find-all-game-sessions-with-board-size (get-connection)3)
;; (find-all-game-sessions-for-user (get-connection) "stanmil")
;; (find-all-game-sessions-stated-won (get-connection) \H)
;; (insert-game-session (get-connection) 3 "stanmil" "H" 3 0 "R")