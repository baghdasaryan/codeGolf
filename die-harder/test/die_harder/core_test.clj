(ns die-harder.core-test
  (:require [clojure.test :refer :all :exclude [report]]
            [die-harder.core :refer :all]))

(def mjis (make-jugs [3 5]))

(deftest immutable-pour-test
  (= 0 (-> mjis (pour-from 0 1))))

(defn get-amount [states]
  (->> states :states (map :amount) (apply +)))

(deftest immutables-test
  (testing "jugs, immutable version"
    (is (= 3 (:capacity (mjis 0))))
    (is (= 5 (:capacity (mjis 1))))
    (is (= [0 0] (map :amount mjis)))
    (is (= 3 (-> mjis
                 (fill-jug 0)
                 (get  0)
                 :amount)))
    (is (= 0 (-> mjis
                 (fill-jug  0)
                 (spill-jug 0)
                 (get   0)
                 :amount)))
    (is (= 4 (-> mjis
                 (fill-jug  1)
                 (pour-from 0 1)
                 (spill-jug 0)
                 (pour-from 0 1)
                 (fill-jug  1)
                 (pour-from 0 1)
                 (get   1)
                 :amount
                 )))
    (is (= '(die-harder.core/fill-jug 42) (gen-fill 42)))
    (is (= {:id 1, :capacity 5, :amount 4}
           (reduce execute-move
                   mjis
                   '((die-harder.core/fill-jug  1)
                     (die-harder.core/pour-from 0 1)
                     (die-harder.core/spill-jug 0)
                     (die-harder.core/pour-from 0 1)
                     (die-harder.core/fill-jug  1)
                     (die-harder.core/pour-from 0 1)
                     (get  1)))))
    (is (detect-win (reduce execute-move
                            mjis
                            '((die-harder.core/fill-jug  1)
                              (die-harder.core/pour-from 0 1)
                              (die-harder.core/spill-jug 0)
                              (die-harder.core/pour-from 0 1)
                              (die-harder.core/fill-jug  1)
                              (die-harder.core/pour-from 0 1)
                              (die-harder.core/spill-jug 0)))
                    4))
    (is (empty? (play-game [3 6] 4)))
    (is (= 2 (game-amount [3 6 8] 2)))
    (is (= 3 (game-amount [3 6] 3)))
))
