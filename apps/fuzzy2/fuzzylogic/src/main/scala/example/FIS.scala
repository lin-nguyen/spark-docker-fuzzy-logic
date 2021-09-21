package FuzzyLogic
import org.apache.spark._
import scala.math.{max, min}
object Defuzzificaton{
  val centroid = (v:variable)=>{
    v.Y = v.aggregation(v.Y)
    val result = v.Y
    .map(x=>(x._1 *x._2,x._2))
    .reduce( (a,b)=> (a._1+b._1, a._2+b._2))
    result._1 / result._2
  }
}
object Aggregation{
  val Max = (Y:org.apache.spark.rdd.RDD[(Double, Double)])
    => Y.reduceByKey((a,b)=>scala.math.max(a,b))
}
object AndMethod{
  val Min = (x1:Double, x2:Double)=> min(x1,x2)
  //val Prod=
}
object OrMethod{
  val Max = (x1:Double, x2:Double)=>max(x1,x2)
}
case class FIS_Mamdani(name:String,
                       input:List[variable],
                       output:List[variable],
                       andMethod: (Double, Double)=>Double,
                       orMethod: (Double, Double)=>Double,
                       aggregation: 
                       (org.apache.spark.rdd.RDD[(Double, Double)])
                     =>(org.apache.spark.rdd.RDD[(Double, Double)]),
                       defuzzification:variable=>Double,
                       step:Double)
{
  for (v<-input) {v.andMethod=andMethod;v.orMethod=orMethod;}
  for (v<-output) {v.init(step)}
  val If= (e:exprIf) => {output.foreach(_.valueFuzzy=e.valueFuzzy);this}
  val Then=(e:exprThen)=>{}
  def setInput(lst:List[Double]): Unit ={
    for (i<- 0 until input.length){
      input(i).value = lst(i)
    }
  }
  def defuzz:variable=>Double=defuzzification
}