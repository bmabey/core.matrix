(ns core.matrix.implementations
  (:use [core.matrix.utils])
  (:require [core.matrix.protocols :as mp]))

;; =====================================================
;; Implementation utilities
;;
;; Tools to support the registration / manangement of core.matrix implementations 

;; map of known implementation tags to namespace imports
;; we use this to attempt to load an implementation
(def KNOWN-IMPLEMENTATIONS
  {:vectorz 'mikera.vectorz.matrix-api
   :ndarray 'core.matrix.impl.ndarray
   :persistent-vector 'core.matrix.impl.persistent-vector
   :sequence 'core.matrix.impl.sequence
   :double-array 'core.matrix.impl.double-array
   :jblas :TODO
   :clatrix 'clatrix.core
   :parallel-colt :TODO
   :ejml :TODO
   :ujmp :TODO
   :commons-math :TODO})

;; default implementation to use
;; should be included with core.matrix for easy of use
(def DEFAULT-IMPLEMENTATION :persistent-vector)

;; hashmap of implementation keys to canonical objects
;; objects must implement PImplementation protocol at a minimum
(def canonical-objects (atom {}))


(defn get-implementation-key 
  "Returns the implementation code for a given object"
  ([m] 
    (if (keyword? m) 
      m
      (mp/implementation-key m))))

(defn register-implementation
  "Registers a matrix implementation for use. Should be called by all implementations
   when they are loaded."
  ([canonical-object]
  (swap! canonical-objects assoc (mp/implementation-key canonical-object) canonical-object)))

(defn- try-load-implementation [k]
  (if-let [ns-sym (KNOWN-IMPLEMENTATIONS k)]
    (try 
      (require ns-sym)
      (catch Throwable t nil))))

(defn get-canonical-object 
  "Gets the canonical object for a specific implementation"
  ([m]
    (let [k (get-implementation-key m)
          obj (@canonical-objects k)]
      (or obj 
          (try-load-implementation k)
          (error "Unable to find implementation: [" k "]")))))