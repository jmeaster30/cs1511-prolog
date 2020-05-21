left.
right.
center.

move(1, X, Y, Z) :-
  write('Move top disk from '),
  write(X),
  write(' to '),
  write(Y),
  nl.

move(N, X, Y, Z) :-
  greaterthan(N, 1),
  subtract(N, 1, M),
  move(M, X, Z, Y),
  move(1, X, Y, W),
  move(M, Z, Y, X).

greaterthan(X, Y) :-
  successorof(X, Y).
greaterthan(X, Y) :-
  subtract(X, 1, Z),
  greaterthan(Z, Y).

successorof(2, 1).
successorof(3, 2).
successorof(4, 3).
successorof(5, 4).
successorof(6, 5).
successorof(7, 6).
successorof(8, 7).
successorof(9, 8).
successorof(10, 9).

subtract(X, 1, Z) :-
  successorof(X, Z).
subtract(X, Y, Z) :-
  successorof(Y, M),
  successorof(X, N),
  subtract(N, M, Z).

