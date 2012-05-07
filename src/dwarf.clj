; Pattern Matching a la Clojure
;(defmulti fib (fn [x] x) :default)
;(defmethod fib 0 [x] 0)
;(defmethod fib 1 [x] 1)
;(defmethod fib :default [n] (+ (fib (- n 2)) (fib (- n 1))))

;
; DEFINITION
;
; Node = One machine
; System = Collection of nodes
; URL =
; Lastenverteilung (Punktesystem? Teracotta etc.?)
; Nodes exposen ihre Services

;
; Core
;
(ns dwarf.core)

(defmacro
  #^{:doc "Define a private variable."}
  def-
  ([name]
     `(def ~name (with-meta '`(def ~name nil) {:private true})))
  ([name value]
     `(def ~name (with-meta '`(def ~name ~value) {:private true}))))

(defn-
  #^{:doc "Get a symbol by name (string)"}
  getsym
  [name]
  (if (symbol? name)
    name
    (symbol name)))

(defn-
  #^{:doc "Get a global symbol by name (string)"}
  getglobsym
  [name]
  (getsym (str "*" name "*")))

(defmacro
  #^{:doc "Define a global var"}
  defglobvar
  ([name]
     `(def (getsym name) nil)
     name)
  ([name initial-value]
     `(def (getsym name) ~initial-value)
     name))

; Global refs
(defmacro
  #^{:doc "Define a global ref"}
  defglobref
  ([name]
     `(def (getsym name) (ref nil))
     name)
  ([name initial-value]
     `(def (getsym name) (ref ~initial-value))
     name))
(defmacro update-globref
  [name value-fn]
  `(dosync
    (commute (getsym name) ~@value-fn)))

;
(defn
  #^{:doc "Check if any given argument is nil"
     :test (fn []
	     (assert (= true (nil?? "a" nil))))}
  nil??
  [x & ys]
  (if (not-any? #(= nil %) (conj ys x))
    false
    true))

;
; URLs
;
(ns dwarf.url)

;
; Internal URL: local to this node
; External URL: all other URLs
;
(def *urls* (ref {}))
(def *protocols* (ref '("http" "file" "ldap" "ftp" "sql")))
;
(defn-
  #^{:doc "Extract the protocol from an URL. Analyzes the characters until first colon."
     :test (fn [] (assert (= "http" (extract-protocol "http://www.bensmann.com"))))}
  extract-protocol
  [url]
  (let [p (loop [s url
		 i 0]
	    (cond
	     (empty? s) nil
	     (= \: (first s)) (subs url 0 i)
	     :else (recur (rest s) (inc i))))]
    (if (some #(= p %) @*protocols*)
      p
      nil)))
;
(defn-
  #^{:doc "Define an URL."
     :test (fn []
	     (let [url "http://www.bensmann.com"
		   u (defurl 'x url)]
	       (assert (= 'x u))
	       (assert (= "http" (extract-protocol u)))))}
  defurl
  [ident url]
  (let [p (extract-protocol url)]
    (if p
      (let [p (keyword p)
	    u (get @*urls* p)
	    i (keyword (str ident))]
	(dosync
	 (commute *urls* assoc p (assoc u i url)))
	ident)
      nil)))
;
(defn-
  #^{:doc ""
     :test (fn []
	     )}
  findurl
  ([ident]
     ())
  ([{:ident ident :url url}]
     ())
; Make protocol definition functions
(defmacro
  #^{:doc "Make functions for defining URLs: defhttpurl, ..."}
  make-protocol-fns
  []
  `(doseq [p# @*protocols*]
    (let [sym# (symbol (str "def" p#))]
      (def ~sym# (fn []
		  (println "x"))))))

; request
; request parameter
; user, role, rights, session
