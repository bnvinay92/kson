sealed class Json

data class JBool(val boolean: Boolean) : Json()
object JNull : Json ()
data class JObject(val members : List<Pair<String, Json>>) : Json ()
data class JArray(val elements: List<Json>) : Json ()
data class JNumber(val double: Double) : Json ()
data class JString(val string: String) : Json ()