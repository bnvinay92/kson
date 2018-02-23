import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.identity

class Parser<T>(val parse: (String) -> Option<Pair<T, String>>) {

    fun <R> map(f: (T) -> R) = Parser { parse(it).map { pair -> pair.mapFirst(f) } }

    fun <T, R> Pair<T, String>.mapFirst(f: (T) -> R) = Pair(f(first), second)

    infix fun <R> thenReturn(r: R) = this.map { r }

    infix fun <R> thenMatch(p: Parser<R>): Parser<R> = this thenReturn { t: R -> identity(t) } fApply p

    infix fun <R> thenDrop(p: Parser<R>): Parser<T> = this.map { t: T -> { r: R -> identity(t) } } fApply p

    infix fun or(other: Parser<T>) = Parser {
        val parsed = parse(it)
        when (parsed) {
            is Some -> parsed
            is None -> other.parse(it)
        }
    }

    companion object {
        fun <T> returns(a: T) = Parser { inp -> Option.pure(Pair(a, inp)) }
        fun <T> none() = Parser { inp -> Option.empty<Pair<T, String>>() }
    }
}

infix fun <T, R> Parser<(T) -> R>.fApply(fa: Parser<T>): Parser<R> = Parser { parse(it).flatMap { (f, rest) -> fa.map(f).parse(rest) } }

fun satisfy(pred: (Char) -> Boolean) = Parser {
    if (it.take(1).indexOfFirst(pred) == 0) {
        Option.pure(Pair(it.get(0), it.drop(1)))
    } else {
        Option.empty()
    }
}

fun <T> cons(t: T) = { ts: List<T> -> listOf(t).plus(ts) }

fun <T> some(p: Parser<T>) = p.map(::cons) fApply many(p)

fun <T> many(p: Parser<T>): Parser<List<T>> = Parser {
    val parsed: Option<Pair<T, String>> = p.parse(it)
    when (parsed) {
        is Some -> many(p).map { ps -> listOf(parsed.get().first).plus(ps) }.parse(parsed.get().second)
        is None -> Option.pure(Pair(emptyList(), it))
    }
}


