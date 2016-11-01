# 3. Use Semantic-UI for UI

Date: 01/01/2016

## Status

Accepted

## Context

A web application needs to usable and reasonably presentable to get
people to use it. Given that CSS/Design are not my deepest skillset, a
framework that will help provide reasonable style and UI abstractions
will help focus learning.

Frameworks like Bootstrap and Zurb Foundation are extremely popular and
evolving. They come with Javascript helpers to help provide
functionality, though were not designed with the intent of using with
React.

## Decision

This choice was admittedly made for convience and is slightly arbitrary 
(and perhaps a bit risky).

[Semantic-UI](http://semantic-ui.com) was chosen for simplicity and existing 
cards abstraction which may be helpeful to an application
having Kanban-board style functionality. 

I also found multiple accounts of people using it with React with minimal 
hassle, given the claim from the Semantic UI [integrations
page](http://semantic-ui.com/introduction/integrations.html#react): 

> Semantic UI components are designed to be compatible with libraries that 
tightly manage UI lifecycle like React. No special bindings are needed.  

> Most components use mutation observers to watch for changes in internal
state, and all components are built with initialize, refresh and destroy
methods which will regenerate, refresh cached values, and teardown
components.

## Consequences

Semantic-UI seems to be reasonably complete and actively supported, but
is not as popular as others. 

There appears to be a dependency on jQuery within the Semantic-UI Javascript, 
so it comes along for the ride.

There are also some complaints about lacking support for accessibility,
which is of concern and may need to be replaced if the app gains
significant traction.
