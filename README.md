# priory

A project present priortisation framework in a website, this is a quick prototype.  The scoring framework is currently driven from a database allowing different schemes to be implemented easily through configuration.

Currently in every case the system supports a:

 - Set of score-types for each item to be scored
 - A set of weights which are applied to those scores
 - A configurable forumula for the overall score

It assumes the priortisation of existing work rather than conception of the work earlier in the lifecycle of projects.


## Prerequisites

### Lein
You will need [Leiningen][1] 2.0 or above installed.

[1]: https://github.com/technomancy/leiningen

### Node
For closure-script and to use react node needs installing.

Installing node is typically done with the aid of [NVM](https://github.com/nvm-sh/nvm?tab=readme-ov-file#installing-and-updating "NVM").
This can be used to manage the node version in use.

A reasonable baseline would be the LTS version

    nvm install --lts

Install react:

    npm install react


## Running for development

To start a web server for the application, run:

    lein run

Then run:

    npx shadow-cljs watch app

Which will monitor and compile the clojurescript.
