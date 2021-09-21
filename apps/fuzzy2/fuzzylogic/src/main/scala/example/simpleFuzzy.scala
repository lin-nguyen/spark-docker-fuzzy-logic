import FuzzyLogic._
import java.net._
import java.io._
import scala.io.StdIn.readLine
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.SparkSession
import org.apache.spark._
import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.{SparkConf, SparkContext}

object simpleFuzzy extends App {
  override def main(args: Array[String]) {
    val spark= SparkSession.builder().appName("FuzzySparkVersion").getOrCreate()
    val sc = spark.sparkContext 

    val service = variable("service", (0, 10), 0.5)
    val food = variable("food", (0, 10), 0.5)
    val atm = variable("atm", (0, 10), 0.5)
    // output
    val tip = variable("tip", (0, 10),sc= sc )

    val poor = trimf("poor", 0, 2, 2);
    val good = trimf("good", 1, 5, 9);
    val excellent = trimf("excellent", 3, 7, 10)
    val rancid = trimf("rancid", 0, 0, 5);
    val delicious = trimf("delicious", 3, 9, 10)
    val cool = trimf("cool", -4, 0, 4);
    val warm = trimf("warm", 1, 5, 9);
    val hot = trimf("hot", 6, 10, 14)
    
    val cheap = trimf("cheap", 0, 0, 5);
    val average = trimf("average", 1, 5, 8);
    val generous = trimf("generous", 5, 10, 10)
    val fis = FIS_Mamdani(name = "tipdemo",
      input = List(service, food, atm),
      output = List(tip),
      andMethod = AndMethod.Min,
      orMethod = OrMethod.Max,
      aggregation = Aggregation.Max,
      defuzzification = Defuzzificaton.centroid,
      step = 1000)
    
    fis.setInput(List(3,2,5))
    fis.If(atm is hot) Then (tip will_be cheap)
    fis If ((service is_not good) or (food is delicious) or (atm is warm)) Then (tip will_be cheap)
    fis If ((service is excellent) or (food is rancid) or (atm is cool)) Then (tip will_be generous)
    println(fis.defuzz(tip))
    Thread.sleep(1000000)
    spark.stop()
  }
}