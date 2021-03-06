(ns core.matrix.operators
  (:require [core.matrix :as m])
  (:refer-clojure :exclude [* - + / vector?]))

(set! *warn-on-reflection* true)
(set! *unchecked-math* true)

;; =====================================================================
;; Mathematical operators defined for matrices and vectors as applicable



(defn *
  "Matrix multiply operator"
  ([a] a)
  ([a b]
    (m/mul a b))
  ([a b & more]
    (reduce m/mul (m/mul a b) more)))

(defn e*
  "Matrix element-wise multiply operator"
  ([a] a)
  ([a b]
    (m/emul a b))
  ([a b & more]
    (reduce m/emul (m/emul a b) more)))

(defn +
  "Matrix addition operator"
  ([a] a)
  ([a b]
    (if (and (number? a) (number? b))
      (clojure.core/+ a b)
      (m/add a b)))
  ([a b & more]
    (reduce + (+ a b) more)))

(defn -
  "Matrix subtraction operator"
  ([a] a)
  ([a b]
    (if (and (number? a) (number? b))
      (clojure.core/- a b)
      (m/sub a b)))
  ([a b & more]
    (reduce - (- a b) more)))


(defn e=
  "Matrix equality operator"
  ([a] true)
  ([a b]
    (m/equals a b))
  ([a b & more]
    (reduce m/equals (m/equals a b) more)))
