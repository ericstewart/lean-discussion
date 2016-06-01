(ns lean-coffee.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [lean-coffee.core-test]
              [lean-coffee.subs-test]
              [lean-coffee.handlers-test]))

(doo-tests 'lean-coffee.core-test
           'lean-coffee.subs-test
           'lean-coffee.handlers-test)


