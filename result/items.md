## 语法分析结果-2

### LR(0)项集族

#### I(0)

|left|right|
|---|---|
|START|. P |
|P|. Q1 D S |
|Q1|. ~ |
#### I(1)

|left|right|
|---|---|
|START|P . |
#### I(2)

|left|right|
|---|---|
|L|. int |
|P|Q1 . D S |
|D|. L id ; Q2 D |
|D|. ~ |
|L|. float |
#### I(3)

|left|right|
|---|---|
|S|. if ( C ) M S N else M S |
|S|. id = E ; |
|S|. while M ( C ) M S |
|P|Q1 D . S |
|S|. S M S |
#### I(4)

|left|right|
|---|---|
|D|L . id ; Q2 D |
#### I(5)

|left|right|
|---|---|
|L|int . |
#### I(6)

|left|right|
|---|---|
|L|float . |
#### I(7)

|left|right|
|---|---|
|P|Q1 D S . |
|M|. ~ |
|S|S . M S |
#### I(8)

|left|right|
|---|---|
|S|id . = E ; |
#### I(9)

|left|right|
|---|---|
|S|if . ( C ) M S N else M S |
#### I(10)

|left|right|
|---|---|
|S|while . M ( C ) M S |
|M|. ~ |
#### I(11)

|left|right|
|---|---|
|D|L id . ; Q2 D |
#### I(12)

|left|right|
|---|---|
|S|. id = E ; |
|S|. if ( C ) M S N else M S |
|S|. while M ( C ) M S |
|S|S M . S |
|S|. S M S |
#### I(13)

|left|right|
|---|---|
|F|. num |
|E|. E + T |
|T|. T * F |
|E|. E - T |
|E|. T |
|T|. T / F |
|F|. ( E ) |
|F|. id |
|S|id = . E ; |
|T|. F |
#### I(14)

|left|right|
|---|---|
|F|. ( E ) |
|T|. F |
|E|. E - T |
|E|. T |
|F|. id |
|T|. F |
|E|. T |
|E|. E - T |
|F|. ( E ) |
|C|. E == E |
|T|. T * F |
|T|. F |
|T|. T * F |
|F|. ( E ) |
|C|. E > E |
|T|. T * F |
|T|. T / F |
|E|. E + T |
|E|. E - T |
|F|. id |
|F|. num |
|C|. E < E |
|E|. T |
|T|. T / F |
|T|. T / F |
|E|. E + T |
|F|. num |
|S|if ( . C ) M S N else M S |
|E|. E + T |
|F|. num |
|F|. id |
#### I(15)

|left|right|
|---|---|
|S|while M . ( C ) M S |
#### I(16)

|left|right|
|---|---|
|D|L id ; . Q2 D |
|Q2|. ~ |
#### I(17)

|left|right|
|---|---|
|S|S . M S |
|M|. ~ |
|S|S M S . |
#### I(18)

|left|right|
|---|---|
|E|E . + T |
|E|E . - T |
|S|id = E . ; |
#### I(19)

|left|right|
|---|---|
|T|T . / F |
|T|T . * F |
|E|T . |
#### I(20)

|left|right|
|---|---|
|T|F . |
#### I(21)

|left|right|
|---|---|
|F|id . |
#### I(22)

|left|right|
|---|---|
|F|num . |
#### I(23)

|left|right|
|---|---|
|T|. T / F |
|E|. E - T |
|F|. num |
|E|. E + T |
|T|. F |
|E|. T |
|T|. T * F |
|F|. ( E ) |
|F|( . E ) |
|F|. id |
#### I(24)

|left|right|
|---|---|
|E|E . - T |
|C|E . < E |
|E|E . + T |
|E|E . - T |
|C|E . > E |
|E|E . + T |
|C|E . == E |
|E|E . + T |
|E|E . - T |
#### I(25)

|left|right|
|---|---|
|S|if ( C . ) M S N else M S |
#### I(26)

|left|right|
|---|---|
|T|T . * F |
|T|T . * F |
|E|T . |
|T|T . / F |
|E|T . |
|E|T . |
|T|T . / F |
|T|T . * F |
|T|T . / F |
#### I(27)

|left|right|
|---|---|
|T|F . |
|T|F . |
|T|F . |
#### I(28)

|left|right|
|---|---|
|F|id . |
|F|id . |
|F|id . |
#### I(29)

|left|right|
|---|---|
|F|num . |
|F|num . |
|F|num . |
#### I(30)

|left|right|
|---|---|
|E|. E - T |
|T|. F |
|T|. F |
|E|. T |
|E|. E - T |
|E|. T |
|T|. T / F |
|F|. ( E ) |
|E|. E + T |
|E|. E + T |
|F|. ( E ) |
|T|. T * F |
|T|. T * F |
|T|. F |
|E|. E - T |
|F|. num |
|F|. num |
|F|( . E ) |
|T|. T / F |
|T|. T / F |
|F|. id |
|F|( . E ) |
|F|. id |
|F|. id |
|F|. ( E ) |
|F|. num |
|F|( . E ) |
|E|. E + T |
|T|. T * F |
|E|. T |
#### I(31)

|left|right|
|---|---|
|F|. ( E ) |
|T|. T / F |
|F|. num |
|T|. T / F |
|E|. E + T |
|T|. F |
|E|. E - T |
|C|. E == E |
|F|. id |
|E|. T |
|T|. T * F |
|T|. T * F |
|F|. id |
|S|while M ( . C ) M S |
|E|. E - T |
|T|. T * F |
|F|. ( E ) |
|F|. id |
|T|. F |
|E|. T |
|C|. E < E |
|T|. T / F |
|C|. E > E |
|E|. T |
|T|. F |
|F|. ( E ) |
|E|. E + T |
|F|. num |
|E|. E - T |
|E|. E + T |
|F|. num |
#### I(32)

|left|right|
|---|---|
|D|. ~ |
|D|L id ; Q2 . D |
|D|. L id ; Q2 D |
|L|. float |
|L|. int |
#### I(33)

|left|right|
|---|---|
|S|id = E ; . |
#### I(34)

|left|right|
|---|---|
|F|. ( E ) |
|T|. F |
|F|. id |
|T|. T / F |
|F|. num |
|E|E + . T |
|T|. T * F |
#### I(35)

|left|right|
|---|---|
|T|. F |
|T|. T / F |
|F|. num |
|F|. ( E ) |
|F|. id |
|T|. T * F |
|E|E - . T |
#### I(36)

|left|right|
|---|---|
|F|. ( E ) |
|F|. num |
|T|T * . F |
|F|. id |
#### I(37)

|left|right|
|---|---|
|F|. num |
|F|. ( E ) |
|T|T / . F |
|F|. id |
#### I(38)

|left|right|
|---|---|
|F|( E . ) |
|E|E . - T |
|E|E . + T |
#### I(39)

|left|right|
|---|---|
|E|. E - T |
|F|. ( E ) |
|F|. num |
|C|E > . E |
|T|. T / F |
|E|. T |
|E|. E + T |
|F|. id |
|T|. F |
|T|. T * F |
#### I(40)

|left|right|
|---|---|
|T|. T / F |
|T|. F |
|E|. T |
|C|E < . E |
|E|. E - T |
|T|. T * F |
|F|. ( E ) |
|F|. num |
|E|. E + T |
|F|. id |
#### I(41)

|left|right|
|---|---|
|F|. ( E ) |
|C|E == . E |
|E|. T |
|E|. E - T |
|T|. T * F |
|F|. id |
|F|. num |
|E|. E + T |
|T|. F |
|T|. T / F |
#### I(42)

|left|right|
|---|---|
|T|. F |
|F|. id |
|T|. T / F |
|F|. num |
|E|E + . T |
|F|. num |
|F|. id |
|T|. F |
|E|E + . T |
|T|. T * F |
|E|E + . T |
|T|. T * F |
|F|. ( E ) |
|T|. T / F |
|T|. F |
|F|. num |
|F|. ( E ) |
|F|. id |
|T|. T / F |
|F|. ( E ) |
|T|. T * F |
#### I(43)

|left|right|
|---|---|
|E|E - . T |
|T|. F |
|F|. id |
|F|. num |
|T|. T * F |
|F|. ( E ) |
|T|. T / F |
|F|. ( E ) |
|F|. ( E ) |
|F|. num |
|F|. num |
|E|E - . T |
|T|. T * F |
|T|. F |
|T|. T * F |
|T|. T / F |
|E|E - . T |
|F|. id |
|T|. F |
|T|. T / F |
|F|. id |
#### I(44)

|left|right|
|---|---|
|M|. ~ |
|S|if ( C ) . M S N else M S |
#### I(45)

|left|right|
|---|---|
|T|T * . F |
|F|. id |
|T|T * . F |
|F|. id |
|F|. num |
|T|T * . F |
|F|. num |
|F|. ( E ) |
|F|. id |
|F|. ( E ) |
|F|. num |
|F|. ( E ) |
#### I(46)

|left|right|
|---|---|
|F|. num |
|F|. num |
|F|. ( E ) |
|T|T / . F |
|F|. ( E ) |
|F|. num |
|F|. id |
|F|. ( E ) |
|F|. id |
|T|T / . F |
|T|T / . F |
|F|. id |
#### I(47)

|left|right|
|---|---|
|E|E . - T |
|E|E . + T |
|F|( E . ) |
|F|( E . ) |
|E|E . + T |
|E|E . - T |
|E|E . + T |
|E|E . - T |
|F|( E . ) |
#### I(48)

|left|right|
|---|---|
|S|while M ( C . ) M S |
#### I(49)

|left|right|
|---|---|
|D|L id ; Q2 D . |
#### I(50)

|left|right|
|---|---|
|T|T . / F |
|T|T . * F |
|E|E + T . |
#### I(51)

|left|right|
|---|---|
|T|T . * F |
|E|E - T . |
|T|T . / F |
#### I(52)

|left|right|
|---|---|
|T|T * F . |
#### I(53)

|left|right|
|---|---|
|T|T / F . |
#### I(54)

|left|right|
|---|---|
|F|( E ) . |
#### I(55)

|left|right|
|---|---|
|E|E . - T |
|E|E . + T |
|C|E > E . |
#### I(56)

|left|right|
|---|---|
|C|E < E . |
|E|E . - T |
|E|E . + T |
#### I(57)

|left|right|
|---|---|
|C|E == E . |
|E|E . - T |
|E|E . + T |
#### I(58)

|left|right|
|---|---|
|E|E + T . |
|T|T . * F |
|T|T . / F |
|E|E + T . |
|T|T . * F |
|T|T . * F |
|T|T . / F |
|T|T . / F |
|E|E + T . |
#### I(59)

|left|right|
|---|---|
|T|T . / F |
|E|E - T . |
|E|E - T . |
|E|E - T . |
|T|T . * F |
|T|T . / F |
|T|T . * F |
|T|T . * F |
|T|T . / F |
#### I(60)

|left|right|
|---|---|
|S|. if ( C ) M S N else M S |
|S|. while M ( C ) M S |
|S|. S M S |
|S|if ( C ) M . S N else M S |
|S|. id = E ; |
#### I(61)

|left|right|
|---|---|
|T|T * F . |
|T|T * F . |
|T|T * F . |
#### I(62)

|left|right|
|---|---|
|T|T / F . |
|T|T / F . |
|T|T / F . |
#### I(63)

|left|right|
|---|---|
|F|( E ) . |
|F|( E ) . |
|F|( E ) . |
#### I(64)

|left|right|
|---|---|
|M|. ~ |
|S|while M ( C ) . M S |
#### I(65)

|left|right|
|---|---|
|S|if ( C ) M S . N else M S |
|N|. ~ |
|S|S . M S |
|M|. ~ |
#### I(66)

|left|right|
|---|---|
|S|. if ( C ) M S N else M S |
|S|while M ( C ) M . S |
|S|. while M ( C ) M S |
|S|. S M S |
|S|. id = E ; |
#### I(67)

|left|right|
|---|---|
|S|if ( C ) M S N . else M S |
#### I(68)

|left|right|
|---|---|
|S|while M ( C ) M S . |
|M|. ~ |
|S|S . M S |
#### I(69)

|left|right|
|---|---|
|S|if ( C ) M S N else . M S |
|M|. ~ |
#### I(70)

|left|right|
|---|---|
|S|. while M ( C ) M S |
|S|. id = E ; |
|S|. S M S |
|S|if ( C ) M S N else M . S |
|S|. if ( C ) M S N else M S |
#### I(71)

|left|right|
|---|---|
|S|if ( C ) M S N else M S . |
|S|S . M S |
|M|. ~ |

