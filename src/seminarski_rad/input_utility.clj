(ns seminarski-rad.input-utility
  (:require [clojure.string :as str]
            [seminarski-rad.input-utility :as utility]))

(defn purify-user-input
  "Removes unnecessary blank characters and capitalizes 
   all letters in input. If input contains \"EAT\" in the
   middle of it,function removes it."
  [input]
  (let [first-step (str/upper-case (str/trim input))] 
      (if (and (= (count first-step) 9)
               (clojure.string/includes? first-step "EAT"))
        (str (subs first-step 0 3) (subs first-step 7))
        first-step)))

(defn extract-keys-from-user-input
  "Takes in a string representing user input and returns
   a vector of every character (except '-') as a keyword."
  [input]
  (conj (conj (conj (vector (keyword (subs input 0 1)))
                    (keyword (subs input 1 2)))
              (keyword (subs input 3 4)))
        (keyword (subs input 4))))

(defn get-move-start
  "Takes in a string representing user input and returns
     a vector of starting coordinates as keywords."
  [input]
  (let [user-keys (extract-keys-from-user-input input)]
    (vector (first user-keys) (first (rest user-keys)))))

(get-move-start "1a-2b")

(defn get-move-finish
  "Takes in a string representing user input and returns
       a vector of ending coordinates as keywords."
  [input]
  (let [user-keys (extract-keys-from-user-input input)]
    (vector (first (rest (rest user-keys)))
            (first (rest (rest (rest user-keys)))))))

(defn get-initial-row-as-num
  "Takes in a string representing user input and returns
    the numeric value of the starting coordinate's row."
  [input]
  (Integer/parseInt (subs input 0 1)))

(defn get-final-row-as-num
  "Takes in a string representing user input and returns
      the numeric value of the ending coordinate's row."
  [input]
  (Integer/parseInt (subs input 3 4)))

(defn get-initial-col-as-str 
  "Takes in a string representing user input and returns
      the string value of the starting coordinate's column."
  [input]
  (subs input 1 2))

(defn get-final-col-as-str
  "Takes in a string representing user input and returns
        the string value of the ending coordinate's column."
  [input]
  (subs input 4 5))

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

(int \A) ;;65
(int \Z) ;;90
(char 65) ;;\A

(defn get-initial-col-as-num
  "Takes in a string representing user input and returns
            the numeric value of the starting coordinate's column."
 [input-str] 
   (char->number (first (get-initial-col-as-str input-str))))

(defn get-final-col-as-num
  "Takes in a string representing user input and returns
          the numeric value of the ending coordinate's column."
  [input]
  (char->number (first (get-final-col-as-str input))))

(defn middle-keyword
  "Takes in two keywords that are single char and alphabetic,
   returns the midvalue between those characters, as a keyword."
  [kw1 kw2]
  (let [char1 (first (name kw1))
        char2 (first (name kw2))
        min-char (char (min (int char1) (int char2)))
        middle-char (char (inc (int min-char)))
        middle-keyword (keyword (str middle-char))]
    middle-keyword))

(defn middle-number
  "Takes in two numbers, returns the previous value of the one
   that's greater."
  [num1 num2] 
  (let [bigger-number (max num1 num2)]
    (dec bigger-number)))

(defn opposite-player-color
  "For given user color as single char string, returns opposite 
   user color (for \"R\" returns \"B\" and vice versa)."
  [color]
  (if (= color "R")
    "B"
    "R"))


(defn midvalue-num
  "Returns the middle value of two numbers as a keyword.
   Best used with integers with odd sized gaps between the values.
   E.g. 3->5 is good because there is 1 (odd) number between
        3->6 is not so good because there are two numbers 
   between them (4 and 5) so the result will be 9/2"
  [num1 num2]
  (/ (+ num1 num2) 2))

(defn num->keyword
  [num]
  (keyword (str num)))

(defn midvalue-str->keyword
  "Takes in two strings that are single char and alphabetic,
     returns the midvalue between those characters, as a keyword."
  [str1 str2]
  (let [num1 (char->number (first str1))
        num2 (char->number (first str2))
        midvalue-num (midvalue-num num1 num2)]
    (keyword (str (number->char midvalue-num)))))

(midvalue-str->keyword "A" "A")

(defn keyword->str
  [keyword]
  (name keyword))

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
  (let [init-row-num (get-initial-row-as-num purified-input-str)
        init-col-str (get-initial-col-as-str purified-input-str)
        final-row-num (get-final-row-as-num purified-input-str)
        final-col-str (get-final-col-as-str purified-input-str)]
    (if (distinct? init-row-num
                   init-col-str
                   final-row-num
                   final-col-str)
      (vector (num->keyword (midvalue-num init-row-num
                                          final-row-num))
              (midvalue-str->keyword init-col-str
                                                   final-col-str))
      (if (= init-row-num final-row-num)
        (vector (num->keyword init-row-num)
                (midvalue-str->keyword init-col-str
                                                     final-col-str))
        (vector (num->keyword (midvalue-num init-row-num final-row-num))
                (keyword init-col-str))))))

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

(defn seq->keyword-seq
  "Returns list of keyword values for given collection 
   of members given as sequence."
  [a-seq]
  (map #(keyword (str %)) a-seq))

(seq->keyword-seq [1 2 3 4])

(defn numeric-seq->letter-seq
  "Returns list of appropriate character values for given collection 
   of numbers given as sequence. Uses conversion map for converting
   values."
  [numeric-seq]
   (map #(name (num->letter-keyword %))
                numeric-seq))

(defn numeric-seq->letter-keyword-seq
  "Returns list of appropriate keyword char values for given collection 
   of numbers given as sequence. Uses conversion map for converting
   values."
  [numeric-seq]
  (let [letter-seq (numeric-seq->letter-seq numeric-seq)]
    (map #(keyword %) letter-seq)))

(numeric-seq->letter-seq [1 2 3 4])
(numeric-seq->letter-keyword-seq [1 2 3 4])
(defn ?-half-of-seq
  "Returns specified half of sequence. If sequence has odd
   number of elements, middle is excluded from both halves."
  [a-seq which-half]
  (let [len (count a-seq)
        cutoff-index (quot (inc len) 2)]
    (if (= 1 which-half)
      (subvec a-seq 0
              (if (odd? len)
                (dec cutoff-index)
                cutoff-index))
      (subvec a-seq cutoff-index))))

(?-half-of-seq [1 2 3 4 5 6 7 8 9 10] 1)

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
    5 
    (if (< inputted-size 0)
      5
      (if (even? inputted-size)
        (inc inputted-size)
        inputted-size))))

