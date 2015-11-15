 Usage: 
1. how to write a -.l file: 
 - structure:
     declarations
     regular expressions definition
     translation rules
     auxiliary functions

 - delimiter:
     `pattern {action}` -- a space between section
     %% have to at the beginning of line enum

 - declaration
    -- %{ have to at the beginning of line %} have to at the beginning of the line

 - supported regex in the -.l file:
   - have to use `{}` to surround your predefined regex expression, not add any other char(include space) between `{}`
   or it will fail to figure out it.
   - support nested parenthesis, like `(a(b+)c)+` or `RE (..)` then `(RE+)`.
   - not support wild card character '.', but using this also have to escape it for I use it internally


 - mis:
   - not allow multiple line comment for '*.l' components,
     but allow it in translation rule or in your method
   - if a single word is in multiple regex range, output is undefined.

 
 
2. How to use produced file: first compile res.c to res.out - input/output:\ input -- a c file output -- run `res.out
 - temp.c` will print the token lists