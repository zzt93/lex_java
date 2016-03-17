%{
/* definitions of manifest constants*/
LT, LE, EQ, NE, GT, GE,
IF, THEN, ELSE, ID, NUMBER, RELOP
%}

%%
/* regular definitions */
//1 (a|b)*
//2 (a*|b*)*
//3 (a|b)*abb(a|b)*
//4 (a|b)*a(a|b)
//5 (a|b)*a(a|b)(a|b)
//6 (a|b)*a(a|b)(a|b)(a|b)
7 aa*(bab*a)*(a|b)b*


%%

//{1} {/* no action and no return */}
//{2} {/* no action and no return */}
//{3} { return ID; }
//{4} { return ID; }
//{5} { return ID; }
//{6} { return ID; }
{7} { return ID; }


%%

