composite(N) :- composite_table(N), !.
composite(N) :-  iscomposite(N, 1),
                                    assert(composite_table(N)).
iscomposite(N, D) :- D > 1,
                                            0 is mod(N, D), !.
iscomposite(N, D) :- N < 1001, 
                           D1 is D + 1,
                           D1 < N,
                            iscomposite(N, D1).
prime(N) :- prime_table(N), !.
prime(N) :- not(composite(N)),
                        assert(prime_table(N)).
minPrimeDivisor(X, I, ANSWER) :- 
    0 is X mod I,
    prime(I), 
    ANSWER is I,!.
    
minPrimeDivisor(X, I, ANSWER) :- 
  NEW_I is I + 1,
    minPrimeDivisor(X, NEW_I, ANSWER).



pass(X, 0, X,X).
pass([HEAD|TAIL],1,TAIL,HEAD).
pass([HEAD|TAIL], N, F, HEAD) :- 
    N > 1, 
  PREVN is N - 1, 
  pass(TAIL, PREVN, F, HEAD).

  
prime_divisors(1, []) :-!.

prime_divisors(X, [X]) :- 
    prime(X), !.
    
prime_divisors(X, [HEAD | TAIL]) :- 
    power_divisors(X,1,[HEAD | TAIL]),!.



power_divisors(1, _, []) :-!.

power_divisors(_, 0, []) :-!.
 
power_divisors(X, Pow, [HEAD | TAIL]) :- 
    X > 1,
    minPrimeDivisor(X, 2, ANSWER),
    HEAD is ANSWER,
    X1 is div(X, HEAD), 
    pass([HEAD | TAIL], Pow, F,HEAD),
    power_divisors(X1, Pow, F),!. 