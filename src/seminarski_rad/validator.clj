(ns seminarski-rad.validator 
  (:require [seminarski-rad.inputUtility :as util]))

(defn input-length-validator 
  "Checks if the user's input is longer than needed. Since the
    proper input is (ex. 'a1-b1') the proper length will always be 
   5"
  [input-str length]
  (if (not (= (count input-str) length))
     (do (println (str "Your input must contain "
                       length " characters!"))
         false)
     true))

(defn input-format-validator
  "Users input must strictly adhere to said form:
   'NL-NL' where N is a number 1-5 and L is a letter
   a-e or A-E."
  [input-str]
  (re-matches #"[1-5][a-eA-E]-[1-5][a-eA-E]" input-str))

(defn proper-piece-color-validator
  "Checks if the user is trying to move their own
    piece color or someone elses or a blank field."
  [input-str board]
  (= "B" (get-in board (util/get-move-start input-str))))

(defn start-not-the-same-as-finish-validator 
  "Checks if the starting position of the move is 
   not the same as the finishing one."
  [input-str]
  (not (= (util/get-move-start input-str)
          (util/get-move-finish input-str))))

(defn start-piece-present-validator 
  "Checks if there is a black piece at the starting position."
  [input-str board]
  (= "B" (get-in board (util/get-move-start input-str))))

(defn end-not-occupied-validator 
  "Checks if the end location of a move is blank or not.
   Returns true if it's open and false otherwise"
  [input-str board]
  (= (util/get-move-finish input-str) "*"))


(defn middle-keyword [kw1 kw2]
  (let [char1 (first (name kw1))
        char2 (first (name kw2))
        min-char (char (min (int char1) (int char2)))
        middle-char (char (inc (int min-char)))
        middle-keyword (keyword (str middle-char))]
    middle-keyword)) 


(defn length-of-jump-horizontal-vertical-validator
  "Checks if the move length is proper for x and y axis. 
   A proper move constitutes one that is at least one tile 
   across, and at most two. If it is the one that is two
   across, the middle tile must be \"eaten\" (removed from the board).
   The only valid tile to be removed is the opponents tile.
   Returns false if invalid, true if valid and no eat and 
   \"eat\" if the opponents piece is eaten."
  [input-str board] 
  (if (> 2 (abs (- (Integer/parseInt (name (first (util/get-move-start input-str))))
                   (Integer/parseInt (name (first (util/get-move-finish input-str)))))))
    false
    (if (> 2 (abs (- ((first (seq (name (first (rest (util/get-move-start input-str)))))))
                     ((first (seq (name (first (rest (util/get-move-finish input-str))))))))))
      false
      (if (not= (and (and (= 2 (abs (- (Integer/parseInt (name (first (util/get-move-start input-str))))
                                       (Integer/parseInt (name (first (util/get-move-finish input-str)))))))
                          (= (first (rest (util/get-move-start input-str))) 
                             (first (rest (util/get-move-finish input-str))))
                          (= "R" (get-in board [(keyword (abs (- (Integer/parseInt (name (first (util/get-move-start input-str))))
                                                                 (Integer/parseInt (name (first (util/get-move-finish input-str)))))))
                                                (first (rest (util/get-move-start input-str)))])))
                     (and (= 2 (abs (- ((first (seq (name (first (rest (util/get-move-start input-str)))))))
                                       ((first (seq (name (first (rest (util/get-move-finish input-str))))))))))
                          (= "R" (get-in board [(first (util/get-move-start input-str))
                                                (middle-keyword 
                                                 (first (rest (util/get-move-start input-str)))
                                                 (first (rest (util/get-move-finish input-str))))])))))
          "eat"
          (if (and (and (and (= 2 (abs (- (Integer/parseInt (name (first (util/get-move-start input-str))))
                                          (Integer/parseInt (name (first (util/get-move-finish input-str)))))))
                             (= (first (rest (util/get-move-start input-str)))
                                (first (rest (util/get-move-finish input-str))))
                             (= "R" (get-in board [(keyword (abs (- (Integer/parseInt (name (first (util/get-move-start input-str))))
                                                                    (Integer/parseInt (name (first (util/get-move-finish input-str)))))))
                                                   (first (rest (util/get-move-start input-str)))])))
                        (and (= 2 (abs (- ((first (seq (name (first (rest (util/get-move-start input-str)))))))
                                          ((first (seq (name (first (rest (util/get-move-finish input-str))))))))))
                             (= "R" (get-in board [(first (util/get-move-start input-str))
                                                   (middle-keyword
                                                    (first (rest (util/get-move-start input-str)))
                                                    (first (rest (util/get-move-finish input-str))))])))))
            "eat")
          ))))

(length-of-jump-horizontal-vertical-validator "3A:3C" {:1 {:A "B" :B "B" :C "B" :D "B" :E "B"}
                                                      :2 {:A "B" :B "B" :C "B" :D "B" :E "B"}
                                                      :3 {:A "B" :B "R" :C "*" :D "R" :E "R"}
                                                      :4 {:A "R" :B "R" :C "R" :D "R" :E "R"}
                                                      :5 {:A "R" :B "R" :C "R" :D "R" :E "R"}})

(defn validate-input
  [input-str board]
  (let [purified-input-str (util/purify-user-input input-str)] 
    (and (input-length-validator purified-input-str 5)
         (input-format-validator purified-input-str)
         (proper-piece-color-validator purified-input-str board)
         start-not-the-same-as-finish-validator purified-input-str)))

(middle-keyword :A :C)

(char (min (int \K) (int \k)))
;; (max-key "d" "a")