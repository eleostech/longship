(ns longship.data
  "Primary Erlang/OTP translation namespace"
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

;; Protocols for both directions
(defprotocol Clojure->OTP
  (encode [self]))

(defprotocol OTP->Clojure
  (decode [self]))

;; Clojure tuple implementation
(defrecord Tuple [elements arity]
  Clojure->OTP
  (encode [self]
    (OtpErlangTuple. (into-array OtpErlangObject (map encode (:elements self))))))

(defn tuple [& els]
  (Tuple. els (count els)))

;; Implementations for converting Clojure/JVM objects into Erlang objects

(extend clojure.lang.Keyword
  Clojure->OTP
  {:encode (fn [this]
             (OtpErlangAtom. (name this)))})

(extend clojure.lang.Symbol
  Clojure->OTP
  {:encode (fn [this]
             (OtpErlangAtom. (name this)))})

(extend String
  Clojure->OTP
  {:encode (fn [this]
             (OtpErlangString. this))})

(extend Double
  Clojure->OTP
  {:encode (fn [this]
             (OtpErlangDouble. this))})

(extend clojure.lang.PersistentVector
  Clojure->OTP
  {:encode (fn [this]
             (OtpErlangList. (into-array OtpErlangObject (map encode this))))})

(extend Long
  Clojure->OTP
  {:encode (fn [this]
             (OtpErlangLong. this))})

(extend Boolean
  Clojure->OTP
  {:encode (fn [this]
             (OtpErlangAtom. this))})

(extend clojure.lang.IPersistentMap
  Clojure->OTP
  {:encode (fn [this]
             (OtpErlangList.
              (into-array
               OtpErlangObject
               (map
                (fn [[k v]]
                  (encode (tuple k v)))
                this))))})

;; Implementations for converting Erlang objects into Clojure/Java objects

(extend OtpErlangAtom
  OTP->Clojure
  {:decode (fn [self]
             (let [value (.atomValue self)]
               (if (or (= "true" value) (= "false" value))
                 (.booleanValue self)
                 (keyword value))))})

(extend OtpErlangBinary
  OTP->Clojure
  {:decode (fn [self]
             (.binaryValue self))})

(extend OtpErlangTuple
  OTP->Clojure
  {:decode (fn [self]
             (Tuple. (map decode (.elements self))
                     (.arity self)))})

(extend OtpErlangDouble
  OTP->Clojure
  {:decode (fn [self]
             (.doubleValue self))})

(extend OtpErlangList
  OTP->Clojure
  {:decode (fn [self]
             (vec (map decode (.elements self))))})

(extend OtpErlangLong
  OTP->Clojure
  {:decode (fn [self]
             (.longValue self))})

(extend OtpErlangString
  OTP->Clojure
  {:decode (fn [self]
             (.stringValue self))})

(extend OtpErlangPid
  OTP->Clojure
  {:decode identity}

  Clojure->OTP
  {:encode identity})
