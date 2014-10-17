(ns longship.server
  (:use [longship.data :only [encode decode tuple]])
  (:require [clojure.tools.logging :as log])
  (:import [com.ericsson.otp.erlang
            OtpErlangDecodeException
            OtpErlangExit
            OtpNode]))

(defmulti handle (fn [type & args] type))

(defmacro defhandler [message-type [& bindings] & body]
  "Use to define new message type handlers, assuming that the first "
  `(defmethod handle ~(keyword message-type)
     [mtype# ~@bindings]
     ~@body))

; (defhandler ping []
;    :pong)

(defn receive [mbox]
  (try
    (let [o (.receive mbox)]
      (when o
        (decode o)))
    (catch RuntimeException e
      (log/error e "Error receiving message")
      nil)
    (catch OtpErlangExit e
      (log/info "Exit message; shutting down")
      :shutdown)
    (catch OtpErlangDecodeException e
      (log/error e "Unable to decode message.")
      nil)))

(defn ! [mbox pid message]
  (.send mbox pid (encode message)))

(defn receive-loop [mbox]
  (let [msg (receive mbox)]
    (case msg
      nil (recur mbox)
      :shutdown nil
      (let [[pid message-type & vals] (:elements msg)]
        (try
          (log/info "Received " message-type " message with params " vals)
          (! mbox pid (apply handle (cons message-type vals)))
          (catch Exception ex
            (! mbox pid (tuple :error (.getMessage ex)))))
        (recur mbox)))))

(defn start-server [node-name message-box-name cookie]
  (receive-loop (.createMbox (OtpNode. node-name cookie) message-box-name)))
