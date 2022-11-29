// Flow control

// if/ else
if (1 > 3) println("Impossible") else println("NIce")
if (1 > 3) {
  println("Impossible")
} else {
  println("NIce")
}

// Matching
val number = 4
number match {
  case 1 => println("One")
  case 2 => println("Two")
  case 3 => println("Three")
  case _ => println("Else")
}

// For loop
for (x <- 1 to 4) {
  val squared = x * x
  println(squared)
}

// While loop
var x = 10
while (x >= 0) {
  println(x)
  x -= 1
}

// Do While loop
x = 0
do {
  println(x); x += 1
} while (x <= 10)

// Expressions
{val x = 10; x + 20}

println({val x = 10; x + 20})

