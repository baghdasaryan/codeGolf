# die-harder

***THE PROBLEM***

In commemoration of [Leslie Lamport's Turing Award][1], let's borrow a
problem from his [TLA+ online hyperbook][2].  There are two versions:
"Die Hard" and "Die Harder." "Die Hard" is an instance of the general,
"Die Harder" problem. "Die Hard" is the following:

Given an empty jug, `jug[0]`, with capacity 3 gallons; and an empty jug,
`jug[1]`, with capacity 5 gallons, deliver exactly 4 gallons of water
under the following rules; you may:

1. fill a jug completely, making its current amount equal to its capacity
2. spill a jug completely, making its current amount equal to zero
3. fill one jug from another, either filling the destination, emptying the source, or both

One solution is to

1. fill jug 1 (amounts are 0, 5)
2. pour jug 1 into jug 0 (amounts are 3, 2)
3. spill jug 0 (amounts are 0, 2)
4. pour jug 1 into jug 0 (amounts are 2, 0)
5. fill jug 1 (amounts are 3, 4)
6. spill jug 0 (amounts are 0, 4)

I believe there are 3 more.

"Die Harder" is the following:

Given `n` empty jugs with non-zero, not-necessarily unique capacities
`c[0], c[1], ..., c[n-1]`, deliver exactly `k` gallons of water, which
may be spread out over multiple jugs, under the same rules as above.

***THE CHALLENGE***

Beat my [reference Clojure code code][3] for

A: performance, by choice of algorithm or by optimization or both (my
algorithm becomes intolerably slow when the number of jugs > 3)

B: clarity (no obfuscators; we want to see your algorithm)

C: brevity

Your code should behave as follows:

Given `n`, capacities in the form of a bracketed list like `[3 5 7]` and
a target amount `k`, print `t` solutions in a form like the following in
Clojure syntax, which is a solution for `n = 2`, `capacities = [3 5]`,
`k = 4`, and `t = 2`:

    ({:states
      [{:amount 0, :capacity 3, :id 0} {:amount 4, :capacity 5, :id 1}],
      :trace
      [(fill-jug 0)        (fill-jug 1)       (spill-jug 0)
       (pour-from 0 1)     (spill-jug 0)      (pour-from 0 1)
       (fill-jug 1)        (pour-from 0 1)    (spill-jug 0) ] }
     {:states
      [{:amount 3, :capacity 3, :id 0} {:amount 1, :capacity 5, :id 1}],
      :trace
      [(fill-jug 0)       (pour-from 1 0)     (fill-jug 0)
       (pour-from 1 0)    (spill-jug 1)       (pour-from 1 0)
       (fill-jug 0) ] } )

Each of your `t` solutions must present the final states of the jugs and
a sequence of instructions that achieve the solution. Minor variations
to the above format are ok.

Extra credit if your code produces optimal (shortest number of moves,
fewest pours, etc.) solutions and you can prove so. You may present
proofs in commentary with your code; acceptance of a proof is at our
sole discretion.

You might unit-test your code on inputs like the following:

    capacities = [3 5 7],    k = any integer from 0 through 15
    capacities = [3 5 7 11], k = any integer from 0 through 26

Include instructions for running your code if it's non-obvious (as in,
"how exactly do I run this bit of INTERCAL?").

***OBSERVATIONS***

If the gcd of the capacities does not divide the target amount, the
problem has no solution. In your golf, you might check this (my
reference code assumes it, instead).

Certain moves, while legal, are trivial, namely:

1. filling a full jug
2. spilling an empty jug
3. pouring from an empty jug
4. pouring into a full jug
5. repeating the last move, whatever it was

In your golf, you may either check for these trivial moves or not.

***A REFERENCE SOLUTION***

You can find a [reference solution in Clojure here][3]. It includes unit
tests that demonstrate the program at work.

  [1]: http://research.microsoft.com/en-us/news/features/lamport-031814.aspx
  [2]:
  http://research.microsoft.com/en-us/um/people/lamport/tla/hyperbook.html
  [3]: https://github.com/rebcabin/ClojureProjects/tree/golf/die-harder

## License

Copyright © 2014 die-harder

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
