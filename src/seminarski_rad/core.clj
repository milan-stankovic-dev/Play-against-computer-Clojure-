(ns seminarski-rad.core
  (:require [seminarski-rad.menu :as game])
  (:gen-class))

(defn -main
  "Runs the game"
  [& args]
  (game/manage-menus))