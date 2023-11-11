(ns seminarski-rad.core
  (:require [seminarski-rad.gameplay :as game])
  (:gen-class))

(defn -main
  "Runs the game"
  [& args]
  (game/play-game game/board))
