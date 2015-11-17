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
{3} {/* no action and no return */}


%%

int installlDQ {/* function to install the lexeme, whose
first character is pointed to by yytext,
arid whose length is yyleng, into the
symbol table and return a pointer
thereto */
}
int installNumO {/* similar to installlD, but puts numer-
ical constants into a separate table */
}