// Values are immutable constants
val hello: String = "Hola!"

// Variables are mutable
var helloThere: String = hello
helloThere = hello + " There!"
println(helloThere)

val imuHelloThere = hello + " There"

//Data types
val numberOne: Int = 1
val truth: Boolean = true
val letterA: Char = 'a'
val pi: Double = 3.14159
val piSinglePrecision: Float = 3.14159f
val bigNumber: Long = 123456789
val smallNumber: Byte = 127

println("Here is a message: " + numberOne + truth + letterA)

println(f"Pi is about $piSinglePrecision%.3f")
println(f"Zero padding on the left: $numberOne%05d")

print(s"I can use var like $numberOne $bigNumber")

print(s"The s prefix isnt limited to variables; I can include any ex[ression ${1+2}")

val theUltimateAnswer: String = "TO life, to universe and everything. 42"
val pattern = """.* (\d+).*""".r
val pattern(answerString) = theUltimateAnswer
val answer = answerString.toInt
println(answer)

