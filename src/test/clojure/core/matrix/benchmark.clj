(ns core.matrix.benchmark
  (:use core.matrix)
  (:require [criterium.core :as c])
  (:require [core.matrix.impl.persistent-vector]))

;; miscellaneous benchmark code
;;
;; as a rough guide, a good timing target for small matrix operations is in the order of 50-200ns

(defn benchmarks []
  
  (c/quick-bench (add [[1 1]] [[1 1]]))
  
  (c/quick-bench (add (double-array [1 1]) (double-array [1 1])))
  
  (let [da (double-array [1 1])]
    (c/quick-bench (add da da))) 
  
  ) 