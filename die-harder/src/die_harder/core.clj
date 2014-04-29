(ns die-harder.core)

(defn- or-default
  "Fetch first optional value from function arguments preceded by &."
  [val default] (if val (first val) default))

(defn make-jugs
  "Makes a vector of jug states given a vector of integer
capacities. Each JUG STATE is a map of an id, integer capacity, and
integer amount, which must be non-negative and less than or equal to
capacity: {:id 1, :capacity 5, :amount 0}"
  [capacities]
  (->>
   capacities
   (map-indexed (fn [i c] {:id i :capacity c :amount 0}))
   vec))

(defn fill-jug
  "Fills the i-th jug state in a vector of jug states to capacity,
irrespective of current amount."
  [jugs i]
  (let [mj (get jugs i)]
    (assoc jugs i
           (->> (:capacity mj)
                (assoc mj :amount)))))

(defn spill-jug
  "Spills the i-th jug state of a vector of jug states, reducing its
current amount to 0."
  [jugs i]
  (let [mj (get jugs i)]
    (assoc jugs i
           (->> 0
                (assoc mj :amount)))))

(defn pour-from
  "Pours a quantity into the i-th jug state from another, distinct j-th
jug state, both inhabiting the same vector of jug states. The quantity
may fill the i-th jug or empty the j-th jug, or both."
  [jugs i j]
  (let [jug-i            (get       jugs i)
        jug-j            (get       jugs j)
        jug-i-amount     (:amount   jug-i )
        jug-j-amount     (:amount   jug-j )
        jug-i-capacity   (:capacity jug-i )
        available-source jug-j-amount
        available-space  (- jug-i-capacity jug-i-amount)
        amount-to-pour   (min available-space available-source)]
    (-> jugs
        (assoc i (->> (+ jug-i-amount amount-to-pour)
                      (assoc jug-i :amount)))
        (assoc j (->> (- jug-j-amount amount-to-pour)
                      (assoc jug-j :amount))))))

(defn range-excluding
  "Produces a sequence of the integers from 0 through n-1, excluding i."
  [n i]
  (->> (range n)
       (filter #(not= i %))))

(defn gen-fill
  "Generates a fill instruction for jug i."
  [i]   `(fill-jug  ~i))

(defn gen-spill
  "Generates a spill instruction for jug i."
  [i]   `(spill-jug ~i))

(defn gen-pours
  "Generates all legal pour instructions into jug i from other jugs in a
vector of jug states of length n."
  [n i] (map (fn [j] `(pour-from ~i ~j))
             (range-excluding n i)))

(defn all-moves
  "Generates a squences of all moves, excluding repeats of the last
instruction given."
  [jugs last-move]
  (let [n   (count jugs)
        all (range n   )]
    (filter
     #(not= % last-move)
     (concat (map gen-fill  all)
             (map gen-spill all)
             (mapcat #(gen-pours n %) all)
             ))))

(defn detect-win
  "Determines whether a vector of jug states satisfies the required
target amount in-toto."
  [jugs target]
  (== target
      (apply + (map :amount jugs))))

(defn execute-move [jugs move]
  (eval `(-> ~jugs ~move)))

(defn try-moves
  [states moves target seen iters max-iters]
  (if (or (not moves) (> iters max-iters)) nil
      (let [trials
            (->> moves
                 (map (fn [move] {:states (execute-move (:states states) move)
                                 :trace  (conj (:trace states) move)}))
                 (filter #(not (contains? seen (:states %)))))
            wins (filter #(detect-win (:states %) target) trials)
            ]
        (if (not (empty? wins)) wins
            (let [new-seen    (reduce conj seen (map :states trials))
                  last-moves  (map #(-> % :trace peek) trials)
                  k           (count trials)
                  ii          (inc iters)
                  just-states (map :states trials)
                  new-movess  (map all-moves just-states last-moves)
                  ]
              (lazy-seq
               (mapcat try-moves
                       trials
                       new-movess
                       (repeat k target)
                       (repeat k new-seen)
                       (repeat k ii)
                       (repeat k max-iters))))))))

(defn play-game [capacities target]
  (if (or (> target (apply + capacities))) nil
      (try-moves
       {:states (make-jugs capacities), :trace []}
       ['(die-harder.core/fill-jug 0)]
       target
       #{}, 1, 10
       )))

(defn game-amount [capacities target]
  (->> (play-game capacities target)
       first
       :states
       (map :amount)
       (apply +)))
