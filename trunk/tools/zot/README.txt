-------------------------------------
Zot: a Bounded Satisfiability Checker
(c) by Matteo Pradella, 2006-2007
-------------------------------------

Zot is written in Common Lisp (with ASDF packaging http://www.cliki.net/asdf).
It can be used under Linux, Windows, or MacOS X, but has been tested only 
under Linux and Windows XP, using CLISP (http://clisp.cons.org/).

This release (20070405) contains only the Linux x86 executables of the
external tools. Zot uses the SAT-solver MiniSat
(http://www.cs.chalmers.se/Cs/Research/FormalMethods/MiniSat/) and SAT2CNF,
part of the SatLab library, included in the Alloy Analyzer
(http://alloy.mit.edu/). Please refer to the respective homepages to obtain
their manuals, licenses, sources or binaries compiled for your platform.

Zot to work assumes that the two executables, called "sat2cnf" and
"minisat", are accessible through the PATH environment variable. 


=== CAVEAT ===

Unfortunately, there is no official manual for Zot, yet. Please refer to
the following notes and to the examples for its usage. For any
problem/question, please send me an email (pradella@elet.polimi.it).

"*-test" directories contain tests presented in the submitted papers.


=== BASIC USAGE / SATISFIABILITY CHECKING ===

The typical Zot specification is written as a Lisp script. For example:

(asdf:operate 'asdf:load-op 'bezot)
(use-package :trio-utils)

(bezot:zot
  10
  (&&	(alwp (&& B (!! A)))
	(next (alwf (&& A (!! B))))) 

The first line loads the bi-inifinite plugin (called bezot). The other
available plugins in this release are the mono-infinite one (called ezot)
and the completeness checker (called czot).

The main procedure is called zot, and has two arguments: the time bound and
the formula to be satisfied.

There is also a switch (:loop-free, nil by default) used to check
completeness.

The propositional operators are: && (and), || (or), !! (not). A predicate
Pred(1,2) is written as (-P- Pred 1 2), while proposition Q is written (-P-
Q). Quantifications: (-E- t '(One Two) a-formula) stands for Exists t in
{One, Two} : a-formula; -A- is the universal quantifier.


=== BOUNDED MODEL CHECKING ===

To perform Bounded Model Checking, the user must provide the model through
as argument :transitions. Important: every variable used must be declared
implicitly by e.g. an initialization formula as the second argument of Zot.

Mutex-2.lisp contains a simple (and hopefully readable) example: the
transitions are usually entered as a list of clauses specified by using the
construct "case-clause". Model variables can be defined through the command
"define-variable". The notation used to state that a variable assume a
value, e.g. X=5, is the following (X-is 5).
 

