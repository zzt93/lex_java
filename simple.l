%{
/* definitions of manifest constants*/
LT, LE, EQ, NE, GT, GE,
IF, THEN, ELSE, ID, NUMBER, RELOP
%}

%%
/* regular definitions */
part a|b
sample {part}*abb

//sample aa*(bab*a)*(a|b)b*

%%

{sample} {/* no action and no return */}


%%

