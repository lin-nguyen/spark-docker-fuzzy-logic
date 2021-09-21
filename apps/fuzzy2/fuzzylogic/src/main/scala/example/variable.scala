package FuzzyLogic
import org.apache.spark.sql.SparkSession
import org.apache.spark._
import scala.collection.immutable.NumericRange
import scala.math.{max, min}

case class exprIf(var valueFuzzy:Double, var andMethod: (Double, Double)=>Double, var orMethod: (Double, Double)=>Double) {
  val and=(e:exprIf)=>exprIf(this.andMethod(this.valueFuzzy, e.valueFuzzy),this.andMethod, this.orMethod)
  val or= (e:exprIf)=>exprIf(this.orMethod(this.valueFuzzy, e.valueFuzzy),this.andMethod, this.orMethod)
}
case class exprThen(){
  val and=(e: exprThen)=>exprThen()
}
case class variable(name: String, range: (Double, Double)= (0,10),
                    var value:Double= -1.0,
                    var valueFuzzy:Double= -1.0,
                    var andMethod:(Double, Double)=>Double=AndMethod.Min,
                    var orMethod:(Double, Double)=>Double=OrMethod.Max,
                    var aggregation:
                    (org.apache.spark.rdd.RDD[(Double, Double)])
                    =>(org.apache.spark.rdd.RDD[(Double, Double)])
                    =Aggregation.Max,
                    var sc: org.apache.spark.SparkContext = null,
                    var step: Double = 1000
                    )
{
  var x_range: org.apache.spark.rdd.RDD[Double] = null
  var Y: org.apache.spark.rdd.RDD[(Double, Double)] = null
  
  def init(step:Double): Unit ={
    this.step = step
    if (sc!=null){
    x_range = sc.parallelize(range._1 to range._2 by (range._2-range._1).toDouble/step)
    Y = x_range.map(x => (x,0.0))
    //println(x_range.collect())
  }
    }
  val is = (prop:membershipFunc)=>exprIf(prop.execute(value),this.andMethod, this.orMethod)
  val is_not = (prop:membershipFunc)=>exprIf(1-prop.execute(value), this.andMethod, this.orMethod)
  val will_be = (prop:membershipFunc)=> {
    // this.Y= aggregation(
    // this.Y,
    // (for (i <- x_range) yield if (prop.execute(i.doubleValue) <= valueFuzzy) prop.execute(i.doubleValue) else valueFuzzy).toList)
    val v = this.valueFuzzy
    val Y2 = x_range
    .map(x => 
    if (prop.execute(x) <= v) (x, prop.execute(x)) 
    else (x, v))
    Y = Y.union(Y2)
    exprThen()
  }
}

