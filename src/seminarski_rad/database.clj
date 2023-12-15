(ns seminarski-rad.database
  (:require [next.jdbc :as jdbc]
            [buddy.hashers :as hashing]
            [seminarski-rad.validator :as val]
            [seminarski-rad.input-utility :as utility]))

(def ^:private db
  {:dbtype "h2"
   :dbname "mem:seminarski-rad"
   :user "sa"
   :password ""
   :subprotocol "h2:tcp"
   :subname "//localhost:9092/mem:seminarski-rad;webAllowOthers=true;
             AUTO_SERVER=TRUE"
   })

(def ^:private datasource (jdbc/get-datasource db))

(defn get-connection 
  []
  (jdbc/get-connection datasource))

(defn execute-sql-script
  []
  (let [script (slurp "resources/seminarski-rad.sql")]
     (jdbc/with-transaction [tx (get-connection)]
       (try
           (jdbc/execute! tx [script])
         (catch Exception ex
           (println (.getMessage ex)))))))

;; Execute it once to get started with your database
(execute-sql-script)

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

(defn login-user 
  [conn username password]
  (let [db-user (find-user-by-username conn username)]
     (when (hashed-password-correct? password 
                                     (:APP_USER/PASSWORD db-user))
       db-user)))

(defn- find-board-by-size
  [conn size]
  (when (number? size)
    (let [db-result (jdbc/execute!
                     conn
                     ["SELECT * FROM board WHERE size = (?)" size])]
      (when db-result
        (first db-result)))))

(defn- insert-board
  [conn size]
  (when (and (number? size)
             (not (find-board-by-size conn size)))
        (jdbc/execute!
         conn
         ["INSERT INTO board (size) VALUES (?)" size])))

(defn- insert-game-session-clean
  [conn board-size username
   who-won human-score
   computer-score human-color] 
    (let [app-user-id (:APP_USER/ID
                       (find-user-by-username conn username))
          board-id (:BOARD/ID
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

