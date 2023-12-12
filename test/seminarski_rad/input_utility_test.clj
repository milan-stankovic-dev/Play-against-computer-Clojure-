(ns seminarski-rad.input-utility-test
  (:require [seminarski-rad.input-utility :refer :all]
            [midje.sweet :refer :all]))

(fact "Your first test"
      (+ 1 1) => 2)

(fact "Purification of user input one gap"
      (purify-user-input "1    ")
      => "1")

(fact "Purification of user input two gaps"
      (purify-user-input " 1    ")
      => "1")

(fact "Purification of user input two gaps lowercase"
      (purify-user-input " a    ")
      => "A")

(fact "Purification of user input one gap lowercase"
      (purify-user-input "     b")
      => "B")

(fact "Purification of user input mixed"
      (purify-user-input " aBc123     ")
      => "ABC123")

(fact "Purification one gap"
            ( purify-move-input "1      " 5) 
            => "1")

(fact "Purification no gaps both lowercase"
            ( purify-move-input "1a-2a" 7)
            => "1A-2A")

(fact "Purification both gaps both lowercase"
            ( purify-move-input "    1a-2a  " 5)
            => "1A-2A")

(fact "Purification both gaps one lowercase"
            ( purify-move-input "  10A-11b " 9)
            => "10A-11B")

(fact "Purification not a string"
      (purify-move-input 12 5)
      => nil)

(fact "Purification board size is over 31"
      (purify-move-input "33a-32a" 33)
      => "33a-32a")

(fact "Key extraction simple"
            ( extract-keys-from-user-input "1A-2A")
            => [:1 :A :2 :A])

(fact "Key extraction two multidigit"
            ( extract-keys-from-user-input "11A-12B")
            => [:11 :A :12 :B])

(fact "Key extraction one multi digit"
            ( extract-keys-from-user-input "10A-2B")
            => [:10 :A :2 :B])

(fact "Move 1st or 2nd coordinate 1st"
            ( move-?-coordinate "1A-2A" 1)
            => [:1 :A])

(fact "Move 1st or 2nd coordinate 2nd"
            ( move-?-coordinate "1A-2A" 2)
            => [:2 :A])

(fact "Move 1st or 2nd coordinate 3rd"
            ( move-?-coordinate "1A-2A" 3)
            => nil)

(fact "Get 1st or 2nd row as num 1st"
            ( get-?-row-as-num "1A-2A" 1)
            => 1)

(fact "Get 1st or 2nd row as num 2nd"
            ( get-?-row-as-num "1A-2A" 2)
            => 2)

(fact "Get 1st or 2nd row as num 3rd"
            ( get-?-row-as-num "1A-2A" 3)
            => nil)

(fact "Get 1st or 2nd col as char 1st"
            ( get-?-col-as-char "1A-2B" 1)
            => \A)

(fact "Get 1st or 2nd col as char 2nd"
            ( get-?-col-as-char "1A-2B" 2)
            => \B)

(fact "Get 1st or 2nd col as char 3rd"
            ( get-?-col-as-char "1A-2B" 3)
            => nil)

(fact "Number to character 1"
            ( number->char 1)
            => \A)

(fact "Number to character 2"
            ( number->char 2)
            => \B)

(fact "Number to character 3"
            ( number->char 3)
            => \C)

(fact "Number to character 4"
            ( number->char 4)
            => \D)

(fact "Number to character 9"
            ( number->char 9)
            => \I)

(fact "Number to character 26"
            ( number->char 26)
            => \Z)

(fact "Number to character 27"
            ( number->char 27)
            => \[)

(fact "Number to character 37"
            ( number->char 37)
            => \e)

(fact "Char to number A"
            ( char->number \A)
            => 1)

(fact "Char to number B"
            ( char->number \B)
            => 2)

(fact "Char to number C"
            ( char->number \C)
            => 3)

(fact "nth column as number 1st"
            ( get-?-col-as-num "2C-2B" 1)
            => 3)

(fact "nth column as number 2nd"
            ( get-?-col-as-num "2C-2B" 2)
            => 2)

(fact "nth column as number 3d"
            ( get-?-col-as-num "2C-2B" 3)
            => nil)

(fact "Middle keyword gap of 1"
            ( middle-keyword :A :C)
            => :B)

(fact "Middle keyword gap of 2"
            ( middle-keyword :A :D)
            => :B)

(fact "Middle keyword gap of 1 (2nd)"
            ( middle-keyword :X :Z)
            => :Y)

(fact "Middle keyword gap of 3"
            ( middle-keyword :A :E)
            => :C)

(fact "Middle number gap of 1"
            ( middle-number 1 3)
            => 2)

(fact "Middle number gap of 3"
            ( middle-number 1 5)
            => 3)

(fact "Middle number gap of 0"
            ( middle-number 0 0)
            => 0)

(fact "Middle number gap of 4"
            ( middle-number 1 4)
            => 2)

(fact "Opposite player B"
            ( opposite-player-color "B")
            => "R")

(fact "Opposite player R"
            ( opposite-player-color "R")
            => "B")

(fact "Opposite player wrong letter"
            ( opposite-player-color "G")
            => nil)

(fact "Opposite player number"
            ( opposite-player-color 17)
            => nil)

(fact "Num to keyword"
            ( num->keyword 17)
            => :17)

(fact "Midchar as a keyword gap of 1"
            ( midvalue-char->keyword \A \C)
            => :B)

(fact "Midchar as a keyword gap of 2"
            ( midvalue-char->keyword \A \D)
            => :B)

(fact "Midchar as a keyword gap of 3"
            ( midvalue-char->keyword \A \E)
            => :C)

(fact "Midchar as a keyword one nil"
            ( midvalue-char->keyword \A nil)
            => nil)

(fact "Midchar as a keyword two nils"
            ( midvalue-char->keyword nil nil)
            => nil)

(fact "Numeric keyword to number single digit"
            ( numeric-keyword->num :1)
            => 1)

(fact "Numeric keyword to number multi digit"
            ( numeric-keyword->num :12)
            => 12)

;; ( fact "Numeric keyword to number 3 should throw NumberFormatException"
;;             ( numeric-keyword->num :foo)
;;             => (throws NumberFormatException))

(fact "Calculate field to eat row"
            ( calculate-field-to-eat "1A-3A")
            => [:2 :A])

(fact "Calculate field to eat column"
            ( calculate-field-to-eat "10A-12A")
            => [:11 :A])

(fact "Calculate field to eat row back"
            ( calculate-field-to-eat "45F-43F")
            => [:44 :F])

(fact "Calculate field to eat column back" 
      ( calculate-field-to-eat "5D-3D")
            => [:4 :D])

(fact "Calculate field to eat diag 1"
            ( calculate-field-to-eat "1A-3C")
            => [:2 :B])

(fact "Calculate field to eat diag 2"
            ( calculate-field-to-eat "101D-99B")
            => [:100 :C])

(fact "Reverse input"
            ( reverse-input "1A-2B")
            => "2B-1A")

;; ( fact "Take user input move"
;;       ( take-user-input-move) 
;;             => (provided [read-line] => "mocked user input")
;;       (my-function-under-test) => expected-result)

(fact "Number to letter keyword single digit"
            ( num->letter-keyword 1)
            => :A)

(fact "Number to letter keyword more digits"
            ( num->letter-keyword 25)
            => :Y)

(fact "Sequence to sequence of keywords numbers"
            ( seq->keyword-seq [1 2 3 4 5])
            => '(:1 :2 :3 :4 :5))

(fact "Sequence to sequence of keywords words"
            ( seq->keyword-seq ["foo" "bar" "baz"])
            => '(:foo :bar :baz))

(fact "Sequence to sequence of keywords single letter strings"
            ( seq->keyword-seq ["A" "B" "C"])
            => '(:A :B :C))

(fact "Sequence to sequence of keywords mixed"
  ( seq->keyword-seq [1 "A" "foo"])
  => '(:1 :A :foo))

(fact "Sequence to sequence of keywords nil"
            ( seq->keyword-seq nil)
            => '())

(fact "Sequence to sequence of keywords list"
            ( seq->keyword-seq '("A" "fizz" 1))
            => '(:A :fizz :1))

;; (fact "Sequence to sequence of keywords hash set"
;;             ( seq->keyword-seq #{"A" "fizz" 1})
;;             => )

(fact "Sequence of numbers to list of letters vector"
      (numeric-seq->letter-seq [1 2 23 9 10])
      => '(\A \B \W \I \J))

(fact "Sequence of numbers to list of letters list"
      (numeric-seq->letter-seq '(1 2 23 9 10))
      => '(\A \B \W \I \J))


(fact "Sequence of numbers to list of keyword letters vector"
      (numeric-seq->letter-keyword-seq [1 2 23 9 10])
      => '(:A :B :W :I :J))

(fact "Sequence of numbers to list of keyword letters list"
      (numeric-seq->letter-keyword-seq '(1 2 23 9 10))
      => '(:A :B :W :I :J))

(fact "First half of vector, even length"
      (?-half-of-vec [1 2 3 4] 1)
      => [1 2])

(fact "Second half of vector, even length"
      (?-half-of-vec [1 2 3 4] 2)
      => [3 4])

(fact "Third half of vector, even length"
      (?-half-of-vec [1 2 3 4] 3)
      => nil)

(fact "Reversing extraction of keys"
      (reverse-extraction-of-keys [:1 :A :2 :B])
      => "1A-2B")

(fact "Reversing extraction of keys"
      (reverse-extraction-of-keys [:12 :C :25 :B])
      => "12C-25B")

;; TODO prompt info!!!

(fact "Adjusting board size not-a-number"
      (adjust-board-size "foo")
      => 5)

(fact "Adjusting board size too small"
      (adjust-board-size 1)
      => 5)

(fact "Adjusting board size negative"
      (adjust-board-size -2)
      => 5)

(fact "Adjusting board too big"
      (adjust-board-size 200)
      => 5)

(fact "Adjusting board even"
      (adjust-board-size 2)
      => 3)

(fact "Adjusting board even"
      (adjust-board-size 2)
      => 3)

(fact "Adjusting board odd"
      (adjust-board-size 11)
      => 11)