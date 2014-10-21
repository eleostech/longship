(ns longship.data-test
  (:require [clojure.test :refer :all]
            [longship.data :refer :all])
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
  (let [clj-tuple (tuple :i "have" 9 :small "hamsters"
                         [:in "a cage" (tuple :on :the "porch")]
                         true "story" nil)
        erl-tuple (OtpErlangTuple.
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
                                (OtpErlangString. "story")
                                (OtpErlangAtom. "undefined")]))]
    (is (= (encode clj-tuple)
           erl-tuple))
    (is (= (decode erl-tuple)
           clj-tuple))))

(deftest proplist-test
  (is (= (encode (sorted-map :a "horse"
                             :these "badgers"
                             :those "hamsters"))
         (OtpErlangList.
          (into-array OtpErlangObject
                      [(OtpErlangTuple.
                        (into-array OtpErlangObject
                                    [(OtpErlangAtom. "a")
                                     (OtpErlangString. "horse")]))
                       (OtpErlangTuple.
                        (into-array OtpErlangObject
                                    [(OtpErlangAtom. "these")
                                     (OtpErlangString. "badgers")]))
                       (OtpErlangTuple.
                        (into-array OtpErlangObject
                                    [(OtpErlangAtom. "those")
                                     (OtpErlangString. "hamsters")]))])))))

(deftest lazy-seq-test
  (is (= (encode (take 3 (repeat "bean")))
         (OtpErlangList.
          (into-array OtpErlangObject
                      [(OtpErlangString. "bean")
                       (OtpErlangString. "bean")
                       (OtpErlangString. "bean")])))))
