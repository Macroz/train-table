Train Table
===========

A small project in ClojureScript. Fetches R-train information from
[rata.digitraffic.fi](https://rata.digitraffic.fi/) API and displays
it in an SVG. A prototype of a real table with LEDs.

![Train Table](train_table.jpg)

Getting started
===============

1. Install the Clojure build tool [Leiningen](https://leiningen.org).
2. Run `lein figwheel dev` to start the server
3. Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).
4. Change the code (browser refreshes automatically thanks to Figwheel)

Project structure
=================

- [`project.clj`](project.clj) – the configuration file for Leiningen
- [`src/cljs/train_table/core.cljs`](src/cljs/train_table/core.cljs) – the code is here
- [`resources/public/index.html`](resources/public/index.html) – the HTML file
- [`resources/public/css/style.css`](resources/public/css/style.css): the CSS style file

Useful documentation
====================

- [ClojureDocs](https://clojuredocs.org) – the Clojure standard library documentation, searchable
- [re-frame](https://github.com/Day8/re-frame/) – the  web framework used in this project that does what React and Redux do
- [Hiccup wiki](https://github.com/weavejester/hiccup/wiki) – how to generate HTML
- [cljs-time](https://github.com/andrewmcveigh/cljs-time) – how to handle time
- [rata.digitraffic.fi](https://rata.digitraffic.fi/) – API for train information

Interactive development
=======================

Interactive development means having a REPL (command prompt) where you
can interact with the code and try out things, while also running the
server.

While you can run Figwheel with `lein figwheel dev` you can also run a regular REPL and start Figwheel in it.

1. Start an interactive clojure environment, for example:
   - run `lein repl` in a terminal
   - use `M-x cider-jack-in-clojurescript` in Emacs (requires [cider](https://github.com/clojure-emacs/cider))
   - install a Clojure IDE like [Cursive for IntelliJ](https://cursive-ide.com/userguide/)
2. After the REPL is running
   - `(use 'figwheel-sidecar.repl-api)`
   - `(start-figwheel!)`
3. Try out things and edit code
4. Code is automatically refreshed thanks to Figwheel
5. (Optional) Connect to the browser
   - `(cljs-repl))`
6. Check that you are there `(.alert js/window "hello from browser")`
