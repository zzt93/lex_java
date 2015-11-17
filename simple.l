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

int installlDQ {/* function to install the lexeme, whose
first character is pointed to by yytext,
arid whose length is yyleng, into the
symbol table and return a pointer
thereto */
}
int installNumO {/* similar to installlD, but puts numer-
ical constants into a separate table */
}