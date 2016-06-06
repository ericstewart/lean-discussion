(ns lean-discussion.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [lean-discussion.core-test]
              [lean-discussion.subs-test]
              [lean-discussion.handlers-test]))

(doo-tests 'lean-discussion.core-test
           'lean-discussion.subs-test
           'lean-discussion.handlers-test)


