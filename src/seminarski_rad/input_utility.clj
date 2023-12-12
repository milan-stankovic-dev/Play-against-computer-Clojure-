(ns seminarski-rad.input-utility
  (:require [clojure.string :as str]))

(defn purify-user-input
  "Removes blank characters and capitalizes all letters
   in input."
  [input-str]
  (when (string? input-str)
    (str/upper-case (str/trim input-str))))

(defn purify-move-input
  "Applies purify-user-input to specified input if board size is less
   than 33. Removes blank characters otherwise. Lowercase input is required
   for bigger boards that may have columns that are lowercase."
  [input-str adj-board-size]
  (when (string? input-str) 
    (if (< adj-board-size 33)
      (purify-user-input input-str)
      (str/trim input-str))))

(defn extract-keys-from-user-input
  "Takes in a string representing user input (ex. 1A-2A or 111D-111E)
   and returns a vector of every number or letter as a keyword."
  [input-str]
  (vec (first (reduce (fn
                        [[acc num-acc] a-char]
                        (if (= \- a-char)
                          [acc ""]
                          (if (empty? num-acc)
                            (if (not (Character/isDigit a-char))
                              [(conj acc
                                     (keyword (str a-char))) ""]
                              [acc (str num-acc a-char)])
                            (if (Character/isDigit a-char)
                              [acc (str num-acc a-char)]
                              [(conj (conj acc (keyword num-acc))
                                     (keyword (str a-char))) ""]))
                          ))[[] ""] input-str))))
(defn- valid-?half?
  [half]
  (or (= 1 half)
      (= 2 half)))

(defn move-?-coordinate
  "Takes in a string representing user input and returns
       a vector of specified (first or second) coordinates
   as keywords."
  [input-str which-half]
  (when (valid-?half? which-half)
    (let [input-arr (.split input-str "-")]
      (extract-keys-from-user-input
       (nth input-arr (dec which-half))))))

(defn get-?-row-as-num
  "Takes in a string representing user input and returns
    the numeric value of the specified coordinate's row."
  [input-str which-row] 
  (when (and (valid-?half? which-row)
             (string? input-str)
             (seq input-str)) 
    (let [extracted (extract-keys-from-user-input input-str)] 
      (Integer/parseInt (name 
                         (nth extracted (- (* 2 which-row) 2)))))))

(defn get-?-col-as-char
  "Takes in a string representing user input and returns
    the character value of the specified coordinate's 
   column."
  [input-str which-col]
  (when (and (valid-?half? which-col)
             (string? input-str)
             (seq input-str))
      (let [extracted (extract-keys-from-user-input input-str)]
        (first (name (nth extracted (- (* 2 which-col) 1)))))))

(defn number->char
  "Converts number to its character in the alphabet. May behave funky
   for numbers less than 1 and greater than 26."
  [a-number]
  (if-not (number? a-number)
    nil
    (char (+ 64 a-number))))

(defn char->number 
  "Converts single char to the position in the alphabet for that letter.
   May behave funky for non letters or lowercase letters."
  [a-char]
  (if-not (char? a-char)
    nil
    (- (int a-char) 64)))

(defn get-?-col-as-num
  "Takes in a string representing user input and returns
            the numeric value of the specified coordinate's column."
 [input-str which-col] 
  (when (valid-?half? which-col)
    (char->number (get-?-col-as-char input-str which-col))))

(defn middle-number
  "Takes in two numbers, returns the one that's in the middle
   between them. Rounds down."
  [num1 num2] 
  (quot (+ num1 num2) 2))

(defn opposite-player-color
  "For given user color as single char string, returns opposite 
   user color (for \"R\" returns \"B\" and vice versa)."
  [color]
  (cond 
    (= "R" color) "B"
    (= "B" color) "R"
    :else nil))

(defn num->keyword
  [num]
  (keyword (str num)))

(defn midvalue-char->keyword
  "Takes in two characters and returns the midvalue
   between those characters, as a keyword. Rounds down."
  [char1 char2]
  (when-not (or (nil? char1)
                (nil? char2))
   (let [num1 (char->number char1)
         num2 (char->number char2)
         midvalue-num (middle-number num1 num2)]
     (keyword (str (number->char midvalue-num))))))

(defn numeric-keyword->num
  "Takes in a keyword that is numeric and returns the number
   represented by it."
  [a-keyword]
  (Integer/parseInt (name a-keyword)))

(defn calculate-field-to-eat
  "For a string given as representation of a user's move (supposedly
   validated) returns vector of keyworded coordinates of field that
   is to be eaten by user's move."
  [purified-input-str] ;e.g. "1A-3C"
  (let [init-row-num (get-?-row-as-num purified-input-str 1)
        init-col-char (get-?-col-as-char purified-input-str 1)
        final-row-num (get-?-row-as-num purified-input-str 2)
        final-col-char (get-?-col-as-char purified-input-str 2)]
    (if (distinct? init-row-num
                   init-col-char
                   final-row-num
                   final-col-char)
      (vector (num->keyword (middle-number init-row-num
                                          final-row-num))
              (midvalue-char->keyword init-col-char 
                                      final-col-char))
      (if (= init-row-num final-row-num)
        (vector (num->keyword init-row-num)
                (midvalue-char->keyword init-col-char 
                                        final-col-char))
        (vector (num->keyword (middle-number init-row-num final-row-num))
                (keyword (str init-col-char)))))))

(defn reverse-input
  "Does a semantic reverse, where the end of a move goes first, then the 
   beginning. DOES NOT: do a normal string reversed"
  [purified-input-str]
  (let [[f l] (.split purified-input-str "-")]
    (str l "-" f)))

(defn take-user-input-move
  []
  (println "Your move:")
  (let [user-input (read-line)]
    (println "You entered: " user-input)
    user-input))

(defn num->letter-keyword
  "Returns key under which a value is stored
   in given map, nil otherwise."
  [a-number]
  (keyword (str (number->char a-number))))


(defn middle-keyword
  "Takes in two keywords that are single char and alphabetic,
   returns the midvalue between those characters, as a keyword."
  [kw1 kw2]
  (let [char1 (first (name kw1))
        char2 (first (name kw2))
        num1 (char->number char1)
        num2 (char->number char2)
        mid-num (middle-number num1 num2)]
    (num->letter-keyword mid-num)))

(defn seq->keyword-seq
  "Returns list of keyword values for given collection 
   of members given as sequence."
  [a-seq]
  (map #(keyword (str %)) a-seq))

(defn numeric-seq->letter-seq
  "Returns list of appropriate character values for given collection 
   of numbers given as sequence. Uses conversion map for converting
   values."
  [numeric-seq]
   (map #(first (name (num->letter-keyword %)))
                numeric-seq))

(defn numeric-seq->letter-keyword-seq
  "Returns list of appropriate keyword char values for given collection 
   of numbers given as sequence. Uses conversion map for converting
   values."
  [numeric-seq]
  (let [letter-seq (numeric-seq->letter-seq numeric-seq)]
    (map #(keyword (str %)) letter-seq)))

(defn numeric-seq->numeric-keyword-seq
  "Returns list of appropriate character values for given collection 
   of numbers given as sequence. Uses conversion map for converting
   values."
  [numeric-seq]
  (let [str-seq (map str numeric-seq)]
    (map keyword str-seq)))

(defn- half-validator 
  [a-half]
  (or (= 1 a-half) (= 2 a-half)))

(defn ?-half-of-vec
  "Returns specified half of sequence. If sequence has odd
   number of elements, middle is excluded from both halves."
  [a-seq which-half]
  (when (half-validator which-half)
   (let [len (count a-seq)
         cutoff-index (quot (inc len) 2)]
     (if (= 1 which-half)
       (subvec a-seq 0
               (if (odd? len)
                 (dec cutoff-index)
                 cutoff-index))
       (subvec a-seq cutoff-index)))))
0
(defn reverse-extraction-of-keys
  [keys]
  (let [first-half (?-half-of-vec keys 1)
        second-half (?-half-of-vec keys 2)
        first-half-mid (for [k first-half] (name k))
        second-half-mid (for [k second-half] (name k))
        first-half-str (apply str first-half-mid)
        second-half-str (apply str second-half-mid)]
    (str first-half-str "-" second-half-str)))

(defn prompt-info
  [what-to-prompt validator-function]
  (println (str "Please enter " what-to-prompt ":"))
  (let [info (read-line)]
    (if-not (validator-function info)
      (do
        (println (str (str/capitalize what-to-prompt) " invalid."))
        (prompt-info what-to-prompt validator-function))
      info)))

(defn adjust-board-size
  "Takes in a desired number of board size and converts it to a 
   reasonable value. If input is invalid (non-numeric, less than 0 etc.)
   defaults to 5. If even, adds one. The only fair boards are the odd x odd
   boards."
  [inputted-size]
  (if-not (number? inputted-size)
    (do
      (println "Not a number. Defaulting to 5.")
      5) 
    (if (< inputted-size 2)
      (do 
        (println "Too small. Defaulting to 5.")
        5) 
        (if (>= inputted-size 200)
          (do
            (println "You must be stopped, you animal!
                      That board size is IMMENSE!!!
                      Defaulting to 5.")
            5)
          (if (even? inputted-size)
            (do
              (println "Size cannot be even. Adding one to it.")
              (inc inputted-size))
          inputted-size))))) 
