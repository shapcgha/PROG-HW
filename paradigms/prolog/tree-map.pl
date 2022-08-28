height(nil, 0).
height(tree(K,V,L,R,H), H). 
fixheight(tree(K,V,L,R,H), tree(NK, NV, NL, NR, NH)) :- 
	height(L, R1),
	height(R, R2),
	R1 > R2,
	R3 is R1 + 1,
	tree(NK, NV, NL, NR, NH) = tree(K,V,L,R, R3).
fixheight(tree(K,V,L,R,H), tree(NK, NV, NL, NR, NH)) :- 
	height(L, R1),
	height(R, R2),
	R1 < R2,
	R3 is R2 + 1,
	tree(NK, NV, NL, NR, NH) = tree(K,V,L,R, R3).
fixheight(tree(K,V,L,R,H), tree(NK, NV, NL, NR, NH)) :- 
	height(L, R1),
	height(R, R2),
	R1 is R2,
	R3 is R2 + 1,
	tree(NK, NV, NL, NR, NH) = tree(K,V,L,R, R3).
bfactor(tree(K,V,L,R,H), Res) :-
	height(L, R2),
	height(R, R1),
	Res is R1 - R2.
right(tree(K,V,L,R,H), R).
left(tree(K,V,L,R,H), L).
rotateleft(tree(K,V,L,R,H), tree(NK, NV, NL, NR, NH)) :-
	 Temp is R,
	 left(Temp, Left),
	 R = Left,
	 Left = Temp,
	 fixheight(Temp, Res),
	 Temp = Res,
	 fixheight(tree(K,V,L,R,H), Res1),
	 tree(NK, NV, NL, NR, NH) = Res1.
rotateright(tree(K,V,L,R,H), tree(NK, NV, NL, NR, NH)) :-
	 Temp is R,
	 right(Temp, Right),
	 L = Right,
	 Right = Temp,
	 fixheight(Temp, Res),
	 Temp = Res,
	 fixheight(tree(K,V,L,R,H), Res1),
	 tree(NK, NV, NL, NR, NH) = Res1.
balance(tree(K,V,L,R,H), tree(NK, NV, NL, NR, NH)) :-
	fixheight(tree(K,V,L,R,H), FixTree),
	bfactor(FixTree, Res1),
	Res1 is 2,
	balanceintorigth(FixTree, Res2),
	tree(NK, NV, NL, NR, NH) = Res2, !.
balance(tree(K,V,L,R,H), tree(NK, NV, NL, NR, NH)) :-
	fixheight(tree(K,V,L,R,H), FixTree),
	bfactor(FixTree, Res1),
	Res1 is -2,
	balanceintoleft(FixTree, Res2),
	tree(NK, NV, NL, NR, NH) = Res2, !.
balanceintorigth(tree(K,V,L,R,H), tree(NK, NV, NL, NR, NH)) :-
	right(FixTree, Right),
	bfactor(Right, Res2),
	Res2 < 0,
	rotateright(Right, FixRight1),
	rotateleft(FixRight1, FixRight2),
	tree(NK, NV, NL, NR, NH) = FixRight2, !.
balanceintorigth(tree(K,V,L,R,H), tree(NK, NV, NL, NR, NH)) :-
	right(FixTree, Right),
	bfactor(Right, Res2),
	rotateleft(FixRight1, FixRight2),
	tree(NK, NV, NL, NR, NH) = FixRight2.
balanceintoleft(tree(K,V,L,R,H), tree(NK, NV, NL, NR, NH)) :-
	left(FixTree, Left),
	bfactor(Left, Res2),
	Res2 > 0,
	rotateleft(Left, FixLeft1),
	rotateright(FixLeft1, FixLeft2),
	tree(NK, NV, NL, NR, NH) = FixLeft2, !.
balanceintoleft(tree(K,V,L,R,H), tree(NK, NV, NL, NR, NH)) :-
	left(FixTree, Left),
	bfactor(Left, Res2),
	rotateright(FixLeft1, FixLeft2),
	tree(NK, NV, NL, NR, NH) = FixLeft2, !.
insert((K,V),nil, tree(K,V,nil,nil, 1)).
insert((K,V),tree(NodeK,NodeV, L, R, H), tree(NewK,NewV,NewL,NewR, NewH)) :-
  K < NodeK,
  insert((K,V),L,NewTree),
  NewH is H + 1,
  Tree is tree(NodeK,NodeV, NewTree, R, NewH),
  balance(Tree, TreeRes),
  tree(NewK,NewV,NewL,NewR, NewH) = TreeRes.
insert((K,V),tree(NodeK,NodeV, L, R, H), tree(NewK,NewV,NewL,NewR, NewH)) :-
  K > NodeK,
  insert((K,V),R,NewTree),
  NewH is H + 1,
  Tree is tree(NodeK,NodeV, L, NewTree, NewH),
  balance(Tree, TreeRes),
  tree(NewK,NewV,NewL,NewR, NewH) = TreeRes.
insert((K,V),tree(NodeK,NodeV, L, R, H), tree(NewK,NewV,NewL,NewR, NewH)) :-
  NodeK is K,
  tree(NewK,NewV,NewL,NewR) = tree(NodeK, V, L, R).
build([], Tree, Tree).
build([(NK, NV) | Rest], Root, Res) :-
  insert((NK, NV), Root, NewT),
  build(Rest, NewT, Res).
map_build(ListMap, TreeMap) :-
  build(ListMap, nil, TreeMap).
map_get(tree(K, V, _, _, _), K, V).
map_get(tree(NodeK, NodeV, L, R, H), K, V) :- 
  K < NodeK, 
  map_get(L, K, V).
map_get(tree(NodeK, NodeV, L, R, H), K, V) :- 
  K > NodeK, 
  map_get(R, K, V).
map_keys(nil,[]):-!.
map_keys(tree(K,V,nil,nil, 1),[K]):-!.
map_keys(tree(K,V,L,R, _),Res) :-
  map_keys(L,LR),
  append(LR,[K],Res1),
  map_keys(R,RR),
  append(Res1,RR,Res).
map_values(nil,[]):-!.
map_values(tree(K,V,nil,nil, _),[V]):-!.
map_values(tree(K,V,L,R, _),Res) :-
  map_values(L,LR),
  append(LR,[V],Res1),
  map_values(R,RR),
  append(Res1,RR,Res).