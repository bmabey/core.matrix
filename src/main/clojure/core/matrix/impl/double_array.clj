(ns core.matrix.impl.double-array
  (:require [core.matrix.protocols :as mp])
  (:use core.matrix.utils)
  (:require core.matrix.impl.persistent-vector)
  (:require [core.matrix.implementations :as imp])
  (:require [core.matrix.impl.mathsops :as mops])
  (:require [core.matrix.multimethods :as mm]))


(set! *warn-on-reflection* true)
(set! *unchecked-math* true)


;; core.matrix implementation for Java double arrays
;;
;; Useful as a fast, mutable 1D vector implementation. Not good for much else.

(def DOUBLE-ARRAY-CLASS (Class/forName "[D"))

(defn is-double-array? [m]
  (instance? DOUBLE-ARRAY-CLASS m))

(defn construct-double-array [data]
  (cond 
    (== (mp/dimensionality data) 1) 
      (double-array (mp/element-seq data))
    (mp/is-scalar? data) 
      data
    :default
      nil))

(extend-protocol mp/PImplementation
  (Class/forName "[D")
    (implementation-key [m] :double-array)
    (new-vector [m length] (double-array (int length)))
    (new-matrix [m rows columns] (error "Can't make a 2D matrix from a double array"))
    (new-matrix-nd [m dims] 
      (if (== 1 (count dims)) 
        (double-array (int (first dims)))
        (error "Can't make a double array of dimensionality: " (count dims))))
    (construct-matrix [m data]
      (construct-double-array data))
    (supports-dimensionality? [m dims]
      (<= dims 1)))

(extend-protocol mp/PDoubleArrayOutput
  (Class/forName "[D")
    (to-double-array [m] (copy-double-array m))
    (as-double-array [m] m)) 

(extend-protocol mp/PIndexedAccess
  (Class/forName "[D")
    (get-1d [m x]
      (aget ^doubles m (int x)))
    (get-2d [m x y]
      (error "Can't do get-2D from 1D double array"))
    (get-nd [m indexes]
      (if (== 1 (count indexes)) 
        (aget ^doubles m (int (first indexes)))
        (error "Can't get from double array with dimensionality: " (count indexes)))))


(extend-protocol mp/PIndexedSetting
  (Class/forName "[D")
    (set-1d [m x v]
      (aset ^doubles m (int x) (double v)))
    (set-2d [m x y v]
      (error "Can't do 2D set on double array"))
    (set-nd [m indexes v]
      (if (== 1 (count indexes)) 
        (aset ^doubles m (int (first indexes)) (double v))
        (error "Can't set on double array with dimensionality: " (count indexes))))
    (is-mutable? [m] true))


(extend-protocol mp/PSliceSeq
  (Class/forName "[D")
    (get-major-slice-seq [m] (seq m)))

(extend-protocol mp/PMatrixScaling
  (Class/forName "[D")
    (scale [m a]
      (let [^doubles m m 
            len (alength m)
            arr (double-array len) 
            a (double a)]
        (dotimes [i len] (aset arr i (* a (aget m i))))
        arr))
    (pre-scale [m a]
      (let [^doubles m m 
            len (alength m)
            arr (double-array len) 
            a (double a)]
        (dotimes [i len] (aset arr i (* a (aget m i))))
        arr)))


(extend-protocol mp/PMatrixMutableScaling
  (Class/forName "[D")
    (scale! [m a]
      (let [^doubles m m 
            a (double a)]
        (dotimes [i (alength m)] (aset m i (* a (aget m i))))))
    (pre-scale! [m a]
      (let [^doubles m m 
            a (double a)]
        (dotimes [i (alength m)] (aset m i (* a (aget m i)))))))


(extend-protocol mp/PConversion
  (Class/forName "[D")
    (convert-to-nested-vectors [m]
      (vec m)))

(extend-protocol mp/PCoercion
  (Class/forName "[D")
    (coerce-param [m param]
      (cond
        (is-double-array? param) param
        :else (construct-double-array param))))

(extend-protocol mp/PMatrixCloning
  (Class/forName "[D")
    (clone [m]
      (java.util.Arrays/copyOf ^doubles m (int (count m)))))


(extend-protocol mp/PDimensionInfo
  (Class/forName "[D")
    (dimensionality [m] 1)
    (is-vector? [m] true)
    (is-scalar? [m] false)
    (get-shape [m] (cons (count m) nil))
    (dimension-count [m x]
      (if (== (long x) 0)
        (count m)
        (error "Double array does not have dimension: " x))))

;; registration

(imp/register-implementation (double-array [1]))

