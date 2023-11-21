(ns seminarski-rad.database
  (:require [next.jdbc :as jdbc]))

(def ^:private db
  {:dbtype "mysql"
   :dbname "seminarski-rad"
   :user "root"
   :password ""
   :host "localhost"
   :port 3306})

(def ^:private datasource (jdbc/get-datasource db))

(defn get-connection []
  (jdbc/get-connection datasource))

(defn- initiate-table-user []
  (jdbc/execute!
   datasource
   ["CREATE TABLE IF NOT EXISTS app_user 
     (id SERIAL PRIMARY KEY,
     username VARCHAR(255) NOT NULL,
     password VARCHAR(255) NOT NULL)"]))

(defn- insert-user [conn username password]
  (jdbc/execute!
   conn
   ["INSERT INTO app_user (username, password)
     VALUES (?, ?)" username password]))

;; (def ^:private conn (get-connection))
;; (initiate-table-user)
;; (insert-user conn "stanmil" "123abc")