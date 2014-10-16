(ns longship.otp-test
  (:require [clojure.test :refer :all]
            [longship.otp :refer :all])
  (:import [com.ericsson.otp.erlang
            OtpErlangAtom
            OtpErlangBinary
            OtpErlangDouble
            OtpErlangList
            OtpErlangLong
            OtpErlangObject
            OtpErlangPid
            OtpErlangString
            OtpErlangTuple]))

(deftest tuple-test
  (is (= (tuple :i "have" 9 :small "hamsters" [:in "a cage" (tuple :on :the "porch")] true "story"))
      (OtpErlangTuple.
       (into-array OtpErlangObject
                   [(OtpErlangAtom. "i")
                    (OtpErlangString. "have")
                    (OtpErlangLong. 9)
                    (OtpErlangAtom. "small")
                    (OtpErlangString. "hamsters")
                    (OtpErlangList.
                     (into-array OtpErlangObject
                                 [(OtpErlangAtom. "in")
                                  (OtpErlangString. "a cage")
                                  (OtpErlangTuple.
                                   (into-array OtpErlangObject
                                               [(OtpErlangAtom. "on")
                                                (OtpErlangAtom. "the")
                                                (OtpErlangString. "porch")]))]))
                    (OtpErlangAtom. "true")
                    (OtpErlangString. "story")]))))

