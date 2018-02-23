val space = one(' ')
val spaces = many(space)

val jtrue: Parser<Json> = matchSeq("true") thenReturn JBool(true)
val jfalse: Parser<Json> = matchSeq("false") thenReturn JBool(false)
val jnull: Parser<Json> = matchSeq("null") thenReturn JNull
val jvalue = (jtrue or jfalse or jnull) `surroundedBy` spaces

fun one(c: Char) = satisfy { it.equals(c) }

fun matchSeq(s: String) = s.map(::one).foldRight(Parser.returns("".toList())) { p, ps -> p.map(::cons) fApply ps }

infix fun <T, R> Parser<R>.surroundedBy(p: Parser<T>) = p thenMatch this thenDrop p

fun main(args: Array<String>) {
    println(some(jvalue).parse("true   null sss"))
}