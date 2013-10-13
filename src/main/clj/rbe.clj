(ns rbe
  (:refer-clojure :exclude (get))
  (:require
   (clojure.contrib)); (except :as except))
  (:import
   (org.apache.http.client HttpClient)
   (org.apache.http.client.methods HttpGet HttpPost HttpDelete HttpPut)
   (org.apache.http.impl.client DefaultHttpClient)
   (org.apache.http.entity StringEntity)
   (org.apache.http.util EntityUtils)))

(defn build-request
  ([token request]
	 (doto request
	   (addHeader "Token" token)
	   (addHeader "Content-type" "application/xml")))
  ([token request data]
	 (doto (build-request token request)
	   (setEntity (StringEntity. data)))))

(defn do-request [token request]
  (.execute (org.apache.http.impl.client.DefaultHttpClient.)
		(build-request token request)))

(defn get [token url]
  (do-request token (org.apache.http.client.methods.HttpGet. url)))

(defn post [token url data]
  (do-request token (org.apache.http.client.methods.HttpPost. url) data))

(defn delete [token url]
  (do-request token (org.apache.http.client.methods.HttpDelete. url)))

(defn put [token url]
  (do-request token (org.apache.http.client.methods.HttpPut. url)))

(defn response-to-string
  [response] (org.apache.http.util.EntityUtils/toString (.getEntity response)))

(defn fetch-as-xml [token url]
  (-> (get token url) response-to-string clojure.xml/parse))
