
; a set of jobs currently being done or with results waiting
(def jobs (atom #{}))
;
; queue up some work to be done
(defn add-job [func]
  (let [a (agent nil)]
    (send a (fn [_] (func)))
    (swap! jobs #(conj %1 a))))
;
; grab any completed results and drop those jobs
(defn pop-results []
  (let [results (filter #(zero? (.getQueueCount %1)) @jobs)]
    (swap! jobs #(apply disj %1 results))
    (map deref results)))
;
; example usage: add a computation!
(add-job #(+ 2 2))
; add lots of computations!
(doseq [j [#(+ 1 2)
           #(dotimes [i 10000] (/ 10 2))
           #(reduce * (range 10))
           #(str "Hi mum")]]
  (add-job j))
; first pop gets the first two simple job results
(println (pop-results))
; wait a while and you can get the other results
(Thread/sleep 100)
(println (pop-results))
;(shutdown-agents)
