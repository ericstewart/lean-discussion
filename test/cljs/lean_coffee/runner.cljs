(ns lean-coffee.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [lean-coffee.core-test]))

(doo-tests 'lean-coffee.core-test)
