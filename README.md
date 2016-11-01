# lean-discussion

This is a web application intended to help organize and facilitate 
discussions or meetings. The spirit for a *lean* approach to discussions
comes from ideas like [Lean Coffee &trade;](http://leancoffee.org) created by 
Jim Benson and Jeremy Lightsmith. While the original intent is to support
the kind of discussion described there the implemementation may support
 other options as well.

A discussion with everyone in the same room could simply be facilitated
by using a wall or whiteboard and some sticky notes. But if your participants
are distributed, such as in a remote team, a tool can be handy.

Mostly, this is a learning project for me. I wanted to build something
potentially useful in order to supplement my learning of Clojure/Clojurescript.

## Goals

The initial goal is something usable in-browser only, for a local facilitator
to use (possibly projected) for a single discussion. This will be a 
stepping stone to collaborative features that will allow use for distributed teams.

I intend to add User Story map to use in planning how further features 
are developed.


## Stack 

This is a [re-frame](https://github.com/Day8/re-frame) application 
and still a very early work in progress although basically usable.

## Architecture

Information about architecture and design decisions is kept right in
the repository in the form of [Architecture Decision Records (ADRs)](http://thinkrelevance.com/blog/2011/11/15/documenting-architecture-decisions). 

You can view the [list of ADRs](doc/adr/toc.md) found in the `doc/adr` directory. These are managed using [adr_tools](https://github.com/npryce/adr-tools) for convenience.






## Development Mode

### Compile css:

Compile css file once.

```
lein garden once
```

Automatically recompile css file on change.

```
lein garden auto
```

### Run application:

```
lein clean
lein figwheel dev
```

Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

### Run tests:

```
lein clean
lein doo phantom test once
```

The above command assumes that you have [phantomjs](https://www.npmjs.com/package/phantomjs) installed. However, please note that [doo](https://github.com/bensu/doo) can be configured to run cljs.test in many other JS environments (chrome, ie, safari, opera, slimer, node, rhino, or nashorn). 

## Production Build

```
lein clean
lein cljsbuild once min
```
