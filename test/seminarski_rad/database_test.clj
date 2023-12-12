(ns seminarski-rad.database-test
  (:require [seminarski-rad.database :refer :all]
            [midje.sweet :refer :all]))

(fact "Hashing a password"
      (hashed-password-correct?
       "123abc" 
       "bcrypt+sha512$7248366b29fbc9b14b66e30a1b874d07$12$6eba1462f5fa9e932128166774f60ff535d66bc7f37ab217")
      => true
      (hashed-password-correct?
       "123abc"
       "bcrypt+sha512$6b114eaf0e243b5dce57a5f969c7ad9e$12$a3203c019b211047f4d05e619240966d6c574560fb3781ee")
      => false)


