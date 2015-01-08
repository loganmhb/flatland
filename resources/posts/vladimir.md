title: Vladimir
date: 2014-12-05 10:57:05 -0300
comments: true
categories: programming clojure vladimir

---

Sometime last spring during the first couple months of my exploration of programming, I found my way to Paul Graham's collection of essays online. While I have mixed feelings about his writing, I was thoroughly enchanted by his descriptions of Lisp. Eventually I realized that the languages he was comparing Lisp to were not Python and Ruby but Java, Perl and C++, and in that context his ideas about language power make a lot more sense.

But it didn't matter. I was already devouring all the information I could find about Lisp online -- I began working through [SICP][1], asked my CS major housemates all kinds of questions about functional programming and recursion and tail call optimization, and took a brief shot at [building my own Lisp in C][2]. Eventually, I found my way to Clojure (it seemed the most practical dialect of Lisp). Vladimir, a chess engine, was my first attempt to write a real program in Clojure.

Over the course of occasional tinkering sessions over the summer, I managed to cobble together a board representation, most of a move generator and the beginnings of a UCI interface in a few hundred lines of code. But I was a little over-enamored of the bottom-up design approach Paul Graham writes about as a strength of Lisps, and I found myself more and more lost in my own tiny, disorganized codebase. Adding to the problem, I didn't really know how to use Git when I started, so my half-hearted attempts at version control were all over the place. Commits contained more or less random changes to any of the files and there was nary a branch to be found. I won't even talk about testing. Eventually my momentum ground completely to a halt.

But all is not lost! Plenty of the code I've got is perfectly usable, and since it's mostly small pure functions that compose into more complex actions it's relatively easy to reorganize. But this time I need a little more structure going in. I'm not abandoning a bottom-up style, since it really does work well with Clojure and the read-eval-print loop, but I need to figure out ahead of time how I want the basic flow of information to work. So here goes.

The Plan
========

For basic functionality, Vladimir will have five parts:

### vladimir.board

The most basic requirement of a chess program is that it has to know how chess works. Vladimir's board representation uses a Clojure record (basically a fast hashmap that compiles to a Java class) corresponding to [Forsyth-Edwards notation][3], which is how the UCI protocol communicates board positions. vladimir.board will also contain records to represent pieces and moves, and will expose functions to create, access, and manipulate these data structures: accessing the properties of games, moves and pieces, and creating new games by making moves (a new game must be created each time, because Clojure's data structures are all immutable). I will specify the full board interface in another post; this will be the largest interface by far.

### vladimir.move-gen

The most complicated and bug-prone aspect of a chess engine is usually its move generator, which has to account for all of the rules governing moving pieces in chess, including edge cases like castling, checks, pins, en passant and pawn promotion. This needs to be extremely well-tested, and will probably require the most careful design decisions to keep the code as clean as possible.

The end purpose of the move generator is to exposefunctions to the search module for the regular search and also for [quiescence search][4], which will simplify the move generation by allowing vladimir to safely eliminate all candidate branches where the king is captured rather than checking for pins and checks on every move).

Functions exposed:

(generate-moves [game])
(generate-captures [game])

### vladimir.static-eval

Since chess games contain so many possible positions, the search module cannot possibly trace games out to completetion. Therefore, Vladimir needs a function to evaluate a static position. This can be very basic -- just counting up the values of the pieces on-board, for example -- or can incorporate more factors like king protection, piece locations, center control, bishop pairs, pawn structure, etc.

Functions exposed:
(evaluate [game])

### vladimir.search

The sexy part. At the most basic level, use a Negamax search to find the best move in a given position. I will also be implementing a quiescence search, as mentioned above, which ensures that no obvious captures will result on the next move after the regular search ends.

Functions exposed:
(search [game])

### vladimir.core

The core function of the program -- i.e. what happens when you run it -- is to accept commands according to the UCI protocol and respond appropriately. The core, therefore, controls UCI interaction, calling on vladimir.search to find moves.

One important decision is how the different modules will transfer information. The two primary options are to use FEN strings and algebraic notation, or to use the Game and Move records defined in vladimir.board. I think it will be more consistent and straightforward to use Vladimir's internal representation everywhere, and to keep conversion functions in one place.

Finally, some guiding principles. My main goal here is to write a clean, functional chess engine. I'm not interested in making it as fast as possible or I'd be writing it in a different language, but I am interested in seeing what kind of relative improvements in strength can be made by implementing various chess engine features -- alpha-beta pruning, null move searches, transposition tables, and so on. I'm also looking to learn more about Clojure's concurrency features, so I'll be attempting eventually to parallelize the search process.

More to follow! The first step will be a refactoring of the board representation, and writing some tests. You can follow the rewrite branch on [Github][5]. We'll see how it goes.


[1]: https://mitpress.mit.edu/sicp/full-text/book/book.html

[2]: http://buildyourownlisp.com

[3]: http://en.wikipedia.org/wiki/Forsyth%E2%80%93Edwards_Notation

[4]: http://en.wikipedia.org/wiki/Quiescence_search

[5]: https://github.com/loganmhb/vladimir