## 语法分析结果-1

### 文法

|No.|Grammar|
|---|---|
|0|START->P |
|1|P->Q1 D S |
|2|Q1->~ |
|3|D->L id ; Q2 D |
|4|Q2->~ |
|5|D->~ |
|6|L->int |
|7|L->float |
|8|S->id = E ; |
|9|S->if ( C ) M S N else M S |
|10|S->while M ( C ) M S |
|11|S->S M S |
|12|C->E > E |
|13|C->E < E |
|14|C->E == E |
|15|E->E + T |
|16|E->E - T |
|17|E->T |
|18|T->F |
|19|T->T * F |
|20|T->T / F |
|21|F->( E ) |
|22|F->id |
|23|F->num |
|24|M->~ |
|25|N->~ |
### FIRST集

|文法符号|FIRST集|
|---|---|
|Q1|[~]|
|Q2|[~]|
|C|[num, (, id]|
|D|[float, ~, int]|
|E|[num, (, id]|
|F|[num, (, id]|
|num|[num]|
|L|[float, int]|
|float|[float]|
|while|[while]|
|M|[~]|
|N|[~]|
|P|[id, while, float, if, int]|
|S|[id, while, if]|
|T|[num, (, id]|
|else|[else]|
|id|[id]|
|if|[if]|
|==|[==]|
|(|[(]|
|)|[)]|
|*|[*]|
|+|[+]|
|-|[-]|
|int|[int]|
|/|[/]|
|;|[;]|
|<|[<]|
|=|[=]|
|>|[>]|
|~|[~]|
### FOLLOW集

|文法符号|FOLLOW集|
|---|---|
|Q1|[id, float, while, if, int]|
|Q2|[id, float, while, if, int]|
|C|[)]|
|D|[id, while, if]|
|E|[==, ), ;, +, <, -, >]|
|F|[==, ), *, ;, +, <, -, >, /]|
|L|[id]|
|M|[(, id, while, if]|
|N|[else]|
|P|[$]|
|S|[$, else, id, while, if]|
|T|[==, ), *, ;, +, <, -, >, /]|
