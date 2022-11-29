import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext

object HelloWorld {
  def main(args: Array[String]): Unit = {

    Logger.getLogger("org").setLevel(Level.ERROR)

    val sc = new SparkContext("local[*]", "HelloWorld")

    println("hello world!")
    sc.stop()

  }
}