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
  {:encode #(new OtpErlangAtom (name %))})

(extend clojure.lang.Symbol
  Clojure->OTP
  {:encode #(new OtpErlangAtom (name %))})

(extend String
  Clojure->OTP
  {:encode #(new OtpErlangString %)})

(extend Double
  Clojure->OTP
  {:encode #(new OtpErlangDouble %)})

(extend clojure.lang.PersistentVector
  Clojure->OTP
  {:encode #(new OtpErlangList (into-array OtpErlangObject (map encode %)))})

(extend Long
  Clojure->OTP
  {:encode #(new OtpErlangLong %)})

(extend Boolean
  Clojure->OTP
  {:encode #(new OtpErlangAtom %)})

(extend clojure.lang.IPersistentMap
  Clojure->OTP
  {:encode #(new OtpErlangList
                 (into-array
                  OtpErlangObject
                  (map
                   (fn [[k v]]
                     (encode (tuple k v)))
                   %)))})

(extend nil
  Clojure->OTP
  {:encode (fn [this] (new OtpErlangAtom "undefined"))})

;; Implementations for converting Erlang objects into Clojure/Java objects

(extend OtpErlangAtom
  OTP->Clojure
  {:decode (fn [this]
             (let [value (.atomValue this)]
               (cond
                (or (= "true" value) (= "false" value)) (.booleanValue this)
                 (or (= "undefined" value) (= "null" value)) nil
                 :else (keyword value))))})

(extend OtpErlangBinary
  OTP->Clojure
  {:decode #(.binaryValue %)})

(extend OtpErlangTuple
  OTP->Clojure
  {:decode #(Tuple. (map decode (.elements %))
                    (.arity %))})

(extend OtpErlangDouble
  OTP->Clojure
  {:decode #(.doubleValue %)})

(extend OtpErlangList
  OTP->Clojure
  {:decode #(vec (map decode (.elements %)))})

(extend OtpErlangLong
  OTP->Clojure
  {:decode #(.longValue %)})

(extend OtpErlangString
  OTP->Clojure
  {:decode #(.stringValue %)})

(extend OtpErlangPid
  OTP->Clojure
  {:decode identity}

  Clojure->OTP
  {:encode identity})
