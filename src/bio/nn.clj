(ns bio.nn
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [ring.adapter.jetty :as jetty]))

(defn random-array [size]
  (let [ary ^doubles (make-array Double/TYPE size)]
    (doseq [i ^long (range size)]
      (aset ary i (+ 0.5 (* (rand) 0.1))))
    ary))

(defn random-array-2d [size1 size2]
  (let [ary ^"[[D" (make-array Double/TYPE size1 size2)]
    (doseq [i ^long (range size1)
            j ^long (range size2)]
      (aset ary i j ^double (+ 0.5 (* (rand) 0.1))))
    ary))

(defn logit [x]
  (/ (+ 1 (Math/exp (- x)))))

(defn dotp ^double [^doubles a ^doubles b]
  (let [n (alength a)]
    (reduce (fn [sum i]
              (+ sum (* (aget a i)
                        (aget b i))))
            (range n))))

(defn init-network [num-input num-hidden]
  {:d num-input
   :H num-hidden
   :wi (random-array-2d num-hidden num-input)
   :wh (random-array num-hidden)})

(defn forward-pass [H wi wh h* h x]
  ;; Update the h* and h vectors
  (doseq [i (range H)]
    (let [w ^doubles (aget wi i)
          sum (dotp x w)]
      (aset h* i sum)
      (aset h  i (logit sum))))
  {:h* h* :h h :o (logit (dotp h wh))})

(defn predict [net x]
  (let [{H :H wi :wi wh :wh} net
        h* (make-array Double/TYPE H)
        h  (make-array Double/TYPE H)]
    (:o (forward-pass H wi wh h* h x))))

(defn update-network [net x y alpha beta]
  (let [d (:d net)
        H (:H net)
        wi ^"[[D" (:wi net)
        wh ^doubles (:wh net)
        h* ^doubles (make-array Double/TYPE H)
        h  ^doubles (make-array Double/TYPE H)]
    (let [{h* :h* h :h o :o} (forward-pass H wi wh h* h x)
          k (- (* o (- 1 y))
               (* y (- 1 o)))]
      (doseq [i (range H) j (range d)]
        (let [wi-val (aget wi i j)
              wh-val (aget wh i)
              h-val (aget h i)]
          (aset wi i j
                (- wi-val
                   (* alpha
                      k wh-val h-val (- 1 h-val) (aget x j))
                   (* beta wi-val)))
          (aset wh i
                (- wh-val
                   (* alpha
                      k h-val)
                   (* beta wh-val)))))
      (merge net {:wi wi :wh wh}))))

(defn train-net [dat iters init-net]
  (let [dat (cycle dat)]
    (nth (reductions
          (fn [net [x y]]
            (update-network net x y 0.05 0.005))
          init-net
          dat)
         iters)))

(defn train-on-atom [dat anet]
  (let [dat (cycle dat)]
    (future
      (doseq [[x y] dat]
        (swap! anet #(update-network % x y 0.05 0.005))))))

(defn train-from-data [dat num-hidden iters]
  (let [[xproto yproto] (first dat)
        d (alength xproto)
        n0 (init-network d num-hidden)]
    (train-net dat iters n0)))