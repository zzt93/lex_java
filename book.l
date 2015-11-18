%{
/* definitions of manifest constants*/
LT, LE, EQ, NE, GT, GE,
IF, THEN, ELSE, ID, NUMBER, RELOP
%}

%%
/* regular definitions */
//1 (a|b)*
//2 (a*|b*)*
3 (a|b)*abb(a|b)*


%%

//{1} {/* no action and no return */}
//{2} {/* no action and no return */}
{3} { return ID; }


%%

