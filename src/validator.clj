(ns validator)

(defn input-length-validator 
  [input length]
  (if (not (= (count input) length))
     (do (println (str "Your input must contain "
                       length " characters!"))
         false)
     true))

(input-length-validator "test" 4)

(defn input-format-validator
  [input]
  (re-matches #"[a-eA-E][1-5]-[a-eA-E][1-5]" input))
  
(input-format-validator "a-E3")
