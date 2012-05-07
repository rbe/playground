;;; erlang.clj -- Clojure wrapper for Erlang's JInterface.

(ns erlang)

;; YMMV
;(add-classpath "file:///opt/local/lib/erlang/lib/jinterface-1.4/priv/OtpErlang.jar")

(import
 '(com.ericsson.otp.erlang OtpErlangAtom
                           OtpErlangBinary
                           OtpErlangBoolean
                           OtpErlangChar
                           OtpErlangDouble
                           OtpErlangInt
                           OtpErlangList
                           OtpErlangLong
                           OtpErlangObject
                           OtpErlangRef
                           OtpErlangString
                           OtpErlangTuple
                           OtpNode))

(defmulti to-erlang   class)
(defmulti from-erlang class)

;; atom
(defmethod to-erlang clojure.lang.Symbol [s]
  (new OtpErlangAtom (str s)))

(defmethod from-erlang OtpErlangAtom [s]
  (symbol (.atomValue s)))

(defmethod to-erlang clojure.lang.Keyword [s]
  (new OtpErlangAtom (subs (str s) 1)))

;; binary -- Can send anything serializable between Java nodes.
(defmethod to-erlang java.lang.Object [o]
  (new OtpErlangBinary o))

(defmethod from-erlang OtpErlangBinary [o]
  (.getObject o))

;; boolean
(defmethod to-erlang java.lang.Boolean [b]
  (new OtpErlangBoolean b))

(defmethod from-erlang OtpErlangBoolean [b]
  (.booleanValue b))

;; char
(defmethod to-erlang java.lang.Character [c]
  (new OtpErlangChar c))

(defmethod from-erlang OtpErlangChar [c]
  (.charValue c))

;; double
(defmethod to-erlang java.lang.Double [d]
  (new OtpErlangDouble d))

(defmethod from-erlang OtpErlangDouble [d]
  (.doubleValue d))

;; int
(defmethod to-erlang java.lang.Integer [i]
  (new OtpErlangInt i))

(defmethod from-erlang OtpErlangInt [i]
  (.intValue i))

(defmethod from-erlang OtpErlangLong [i]
  (.longValue i))

;; list
(defmethod to-erlang clojure.lang.Sequential [lst]
  (new OtpErlangList (into-array OtpErlangObject (map to-erlang lst))))

(defmethod from-erlang OtpErlangList [lst]
  (map from-erlang (seq (.elements lst))))

(defmethod to-erlang nil [_]
  (new OtpErlangList (into-array OtpErlangObject [])))

;; erl -man proplists
(defmethod to-erlang clojure.lang.IPersistentMap [map]
  (to-erlang (seq map)))

;; string
(defmethod to-erlang java.lang.String [s]
  (new OtpErlangString s))

(defmethod from-erlang OtpErlangString [s]
  (.stringValue s))

;; tuple
(defmethod to-erlang clojure.lang.IPersistentVector [vec]
  (new OtpErlangTuple (into-array OtpErlangObject (map to-erlang vec))))

(defmethod from-erlang OtpErlangTuple [vec]
  (apply vector (map from-erlang (seq (.elements vec)))))

;; object
(defmethod to-erlang OtpErlangObject [o]
  o)

(defmethod from-erlang OtpErlangObject [o]
  o)

;; Generate a unique object reference.
(let [node   (agent "")
      serial (ref 0)]
  (defn start-node [name]
    (send node (constantly (str name)))
    (new OtpNode (str name)))
  (defn gen-ref []
    (let [id       (dosync (alter serial inc) @serial)
          creation (int (rem (.getTime (new java.util.Date)) 1000000000))]
      (new OtpErlangRef @node id creation))))

;; Just a wrapper around OtpMbox.send().
(defn erl-send
  ([mbox pid msg]
     (.send mbox pid (to-erlang msg)))
  ([mbox name node msg]
     (.send mbox (str name) (str node) (to-erlang msg))))

(defn erl-recv [mbox]
  (from-erlang (.receive mbox)))

;; A call (two-way request/response) using the gen_server protocol.
(defn gen-call [mbox name node m f & a]
  (let [self (.self mbox), ref (gen-ref)]
    (erl-send mbox name node
              ['$gen_call [self ref] ['call m f a self]])
    ;; TODO: Need to check reply for matching ref.
    (let [[_ result] (erl-recv mbox)]
      result)))

;; A cast (one-way notification) using the gen_server protocol.
(defn gen-cast [mbox name node m f & a]
  (erl-send mbox name node
            ['$gen_cast ['cast m f a (.self mbox)]]))

;; rpc:call(node, module, function, arguments)
(defn rpc-call [mbox node m f & a]
  (apply gen-call mbox 'rex node m f a))

;; rpc:cast(node, module, function, arguments)
(defn rpc-cast [mbox node m f & a]
  (apply gen-cast mbox 'rex node m f a))

(defn spawn [r]
  (.start (new java.lang.Thread r)))

(comment
  (load-file "src/erlang.clj")
  (refer 'erlang)

  ;; see echo.erl at end
  (def self (start-node 'clojure))
  (def mbox (.createMbox self))
  (rpc-call mbox 'erlang 'echo 'start)
  (erl-send mbox 'echo 'erlang [(.self mbox) "Hello, Erlang!"])
  (erl-recv mbox)
  (rpc-call mbox 'erlang 'echo 'stop)

  (let [mbox (.createMbox self "echo")]
    (spawn
     (fn []
       (let [[pid msg] (erl-recv mbox)]
         (erl-send mbox pid msg)
         (recur)))))
  (erl-send mbox 'echo 'clojure [(.self mbox) "Hello, Clojure!"])
  (erl-recv mbox)

  (rpc-call mbox 'erlang 'proplists 'get_value :a {:a 1 :b 2})

  ;; %% echo.erl
  ;; %% erl -sname erlang
  ;; %% 1> c(echo).
  ;; -module(echo).
  ;; -export([start/0, stop/0]).
  ;; 
  ;; echo() ->
  ;;     receive
  ;;         {Pid, Msg} -> Pid ! Msg, echo();
  ;;         stop       -> ok
  ;;     end.
  ;; 
  ;; start() ->
  ;;     register(echo, spawn(fun() -> echo() end)).
  ;; 
  ;; stop() ->
  ;;     echo ! stop.
)
