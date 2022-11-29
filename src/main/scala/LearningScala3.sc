// Functions

// format def <fn name>( parameter name: type...) : return type = {}

def squareIt(x: Int) : Int = {
  x * x
}

// Data structures

//Tuples
//Immutable Lists

val captainStuff = ("Picard", "Enterprise-D", "Lol")
println(captainStuff)

// Refer to the individual fields a ONE-BASED index
println(captainStuff._1)
println(captainStuff._2)
println(captainStuff._3)

val picardsShip = "Picard" -> "Enterprise-D"
println(picardsShip._2)

val aBunchOfStuff = ("Kirk", 1964, true)

// Lists
// Like a tuple, but more functionality
// Must be of the same type

val shipList = List("1","23","4")

println(shipList(0))
//zero-based

println(shipList.head)
println(shipList.tail)

for (ship <- shipList) {println(ship)}

val backShips = shipList.map((ship: String) => {ship.reverse})

// reduce() to combine all the items in a collection using
val numberList = List(1,2,3,4,5)
val sum = numberList.reduce((x: Int, y: Int) => x + y)

// Concat
val moreNums = List(7,8,9)
val LotsOfNums = numberList ++ moreNums
