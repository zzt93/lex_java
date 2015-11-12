 Usage: 
1. how to write a -.l file: 
 - structure:
     declarations
     translation rules
     auxiliary functions

 - delimiter:
     `pattern {action}` -- a space between section
     %% have to at the beginning of line enum

 - declaration
    -- %{ have to at the beginning of line %} have to at the beginning of the line

 - supported regex in the -.l file:
   - have to use {} to surround your predefined regex expression


 - mis:
     a. not allow multiple line comment for -.l components,
     but allow it in translation rule or in your method

 
 
2. How to use produced file: first compile res.c to res.out - input/output:\ input -- a c file output -- run `res.out
 - temp.c` will print the token lists