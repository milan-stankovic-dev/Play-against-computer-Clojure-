(ns seminarski-rad.validator-test
  (:require [seminarski-rad.validator :refer :all]
            [midje.sweet :refer :all]
            [seminarski-rad.board :as board]
            [seminarski-rad.computer-logic :refer :all]))

(fact "Confirm validator facts" 
      (confirm-validator-Y-N "y  ") 
      => true
      (confirm-validator-Y-N "   N ")
      => true
      (confirm-validator-Y-N "Y")
      => true
      (confirm-validator-Y-N nil)
      => falsey)
      
(fact "Start not the same as finish facts"
      (start-not-the-same-as-finish-validator "1A-1A")
      => falsey
      (start-not-the-same-as-finish-validator "1A-2A")
      => truthy
      (start-not-the-same-as-finish-validator "1A-2B   ")
      => truthy)

(let [board5 (board/create-board 5)
      board11 (board/create-board 11)
      board7 (board/create-board 7)
      board5-2C-3C (move-piece-computer
                    "2C-3C" board5 "B" 5)
      board5-2C-3C-4C-2C (move-piece-computer
                          "4C-2C" board5-2C-3C "R" 5)
      board5-2C-3C-4C-2C-2C-3C (move-piece-computer
                                "2C-3C" board5-2C-3C-4C-2C "R" 5)
      board5-2C-3C-4C-2C-2C-3C-1C-2C (move-piece-computer
                                      "1C-2C"
                                      board5-2C-3C-4C-2C-2C-3C
                                      "B" 5)
      board5-4B-3C (move-piece-computer
                    "4B-3C" board5 "R" 5)]
  
(fact "Input validation facts part 1"
      (validate-input " " board5 "R" 5)
      => falsey
      (validate-input "2C-3C" board5 "B" 5)
      => truthy
      (validate-input "***2C-3C***" board5 "B" 5)
      => falsey
      (validate-input "7F-6F" board11 "R" 11)
      => truthy
      (validate-input "F7-F6" board11 "R" 11)
      => falsey
      (validate-input "Z1-Z2" board5 "B" 5)
      => falsey
      (validate-input "Z100-Z101" board7 "R" 7)
      => falsey
      (validate-input "   2c-3C " board5 "B" 5)
      => truthy
      (validate-input "   2c-3C " board5 "R" 5)
      => falsey
      (validate-input "1A-2A" board5 "B" 5)
      => falsey
      (validate-input "2c-3C" board5 "B" 5)
      => truthy
      (validate-input "c2-C3" board5 "B" 5)
      => falsey)



(fact "Input validation facts part 2"
      (validate-input "4B-3C" board5 "R" 5)
      => truthy
      (validate-input "4C-2C" board5-2C-3C "R" 5)
      => truthy
      (validate-input "4D-2D" board5-2C-3C "R" 5)
      => falsey
      (validate-input "2C-4C" board5-2C-3C "R" 5)
      => falsey
      (validate-input "2B-4C" board5-2C-3C-4C-2C "B" 5)
      => falsey
      (validate-input "1B-2C" board5-2C-3C-4C-2C-2C-3C "B" 5)
      => falsey
      (validate-input "4A-4C" board5-2C-3C-4C-2C-2C-3C "R" 5)
      => falsey
      (validate-input "3C-1C" board5-2C-3C-4C-2C-2C-3C-1C-2C
                      "R" 5)
      => truthy
      (validate-input "2D-4B" board5-4B-3C "B" 5)
      => truthy
      (validate-input "3A-4B" board5-4B-3C "B" 5)
      => truthy 
      (validate-input "3C-4B" board5-4B-3C "R" 5)
      => truthy
      (validate-input "4C-4B" board5-4B-3C "R" 5)
      => truthy)

(fact "Not empty?"
      (not-empty? "ABC")
      => true
      (not-empty? "")
      => false
      (not-empty? [])
      => false
      (not-empty? ["A" "B" "C"])
       => true
      (not-empty? '("A" "B" "C"))
      => true
      (not-empty? #{"A" "B" "C"})
      => true
      (not-empty? {"A" 1 "B" 2 "C" 3})
      => true)

(fact "User input color?"
      (user-color-input-validator "   r")
      => true
      (user-color-input-validator "   R ")
      => true
      (user-color-input-validator " b")
      => true
      (user-color-input-validator "B   ")
      => true
      (user-color-input-validator "   ")
      => false
      (user-color-input-validator "RB")
      => false
      (user-color-input-validator "foo bar")
      => false
      (user-color-input-validator nil)
      => false
      (user-color-input-validator "")
      => false 
      (user-color-input-validator 1)
      => false
      (user-color-input-validator "G")
      => false))