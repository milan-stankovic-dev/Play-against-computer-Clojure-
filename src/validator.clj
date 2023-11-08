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
  (re-matches #"[1-5][a-eA-E]-[1-5][a-eA-E]" input))
  
(input-format-validator "1a-4E")
