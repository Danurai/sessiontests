# SessionTests

Clojure and Clojurescript http-kit server using Chord async websocket and Reagent for dynamic page updates
Project also includes a simple navbar and tabbed page using bootstrap

## Overview

Template for building interactive web pages with Clojure and Clojurescript

## Setup

To get an interactive development environment open a repl, start the http-kit server and start figwheel, helpers are included in the user namespace:

    C:\sessiontests>lein repl
    ...
    sessiontests.system=> (ns user)
    nil
    user=> (reset)
    :reloading (sessiontests.gameloop sessiontests.web sessiontests.system user)
    Server started on http://localhost:9009
    :resumed
    user=> (fig-start)

and open your browser at [localhost:9009](http://localhost:9009/).
This will auto compile and send all changes to the browser without the
need to reload. After the compilation process is complete, you will
get a Browser Connected REPL.

To clean all compiled files:

    lein clean

To create a production build run:

    lein uberjar
    
Then start the application

    java -jar target\session-standalone.jar

And open your browser at [localhost:9009](http://localhost:9009/). You will not get live reloading, nor a REPL. 

## License

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
