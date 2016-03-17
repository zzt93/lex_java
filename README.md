
###1. how to write a -.l file: 
 - structure: four main blocks  
     - declarations
     - delimiter
     - regular expressions definition
     - delimiter
     - translation rules
     - delimiter
     - auxiliary functions


 - declaration  
    -  `%{` have to at the beginning of line
    -  `%}` have to at the beginning of the line
    
 - delimiter:
     - `%%` have to at the beginning of line
 
 - regular expressions definition
     - `pattern {action}` -  the `pattern` and `action` must be splited by a space

 - supported regex in the -.l file:
    -  have to use `{}` to surround your predefined regex expression, not add any other char(include space) between `{}`
   or it will fail to figure out it.
    -  support nested parenthesis, like `(a(b+)c)+` or `RE (..)` then `(RE+)`.
    -  not support wild card character '.', and using simple dot symbol also have to escape it for I use it internally


 - mis:
    -  not allow multiple line comment for '*.l' components,  
     but allow it in translation rule or in your method
    -  if a single word is in multiple regex range, output is undefined.
   

 
 
###2. How to use produced file: 
 - first compile `res.c` to `res.out ` 
 - input/output:
    -  input: a c file 
    -  output: run `res.out temp.c` will print the token lists in console
 
###3. The longest token my implementation can recognize is 256
