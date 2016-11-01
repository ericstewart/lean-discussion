# 2. Use re-frame for client-side SPA

Date: 01/01/2016

## Status

Accepted

## Context

One of the the goals of this project is learning Clojurescript through
building a non-trivial application. A good choice should likely come
from an actively developed and supported library/framework with good
documentation. 

There are many good options available including [om](https://github.com/omcljs/om), [om next](https://github.com/omcljs/om/wiki/Documentation-(om.next)), [Reagent](https://github.com/reagent-project/reagent), [re-frame](https://github.com/Day8/re-frame), and others.

## Decision

Om would be a good choice, but is also undergoing a lengthy transition
from the original Om to the next generation Om.next. Reagent is very
popular as well, but interestingly some of the most popular documention
for Reagent is in a wrapping framework called **re-frame**. 

Re-frame was chosen because of a very active community, excellent
documentation, and simplicity of managing data/state. Also many people
have commented on the ease of learning which is primary goal.

## Consequences

Re-frame leverages Reagent, which wraps React, so the
constraints/benefits of React apply to whatever degree is enhanced or
limited by Clojurescript, Reagent, and re-frame itself.

Re-frame also has strong opinions on how data should be managed in one
place: a single app-db. This may limit how some components can be
directly leveraged. A publicly discussed challenge is a tool called
[Devcards](https://github.com/bhauman/devcards) which can be a challenge
to isolate state for testing.
