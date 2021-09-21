package FuzzyLogic
import scala.math.{max, min}
object Defuzzificaton{
  val centroid = (v:variable)=>{
    var sum = 0.0 ; var area = 0.0
    for (i <- 0 until v.x_range.length){
      sum = sum + v.x_range(i).doubleValue* v.Y(i)
      area = area + v.Y(i)
    }
    sum/area
  }
}
object Aggregation{
  val Max = (Y1:List[Double], Y2:List[Double])=>(for(i<-Y1.indices) yield max(Y1(i), Y2(i))).toList
}
object AndMethod{
  val Min = (x1:Double, x2:Double)=> min(x1,x2)
}
object OrMethod{
  val Max = (x1:Double, x2:Double)=>max(x1,x2)
}
case class FIS_Mamdani(name:String,
                       input:List[variable],
                       output:List[variable],
                       andMethod: (Double, Double)=>Double,
                       orMethod: (Double, Double)=>Double,
                       aggregation: (List[Double], List[Double])=> List[Double],
                       defuzz:variable=>Double,
                       step:Double=1000){
  for (v<-input) {v.andMethod=andMethod;v.orMethod=orMethod;}
  for (v<-output) {v.init(step)}
  val If= (e:exprIf) => {output.foreach(_.value=e.value);this}
  val Then=(e:exprThen)=>{}
  def setInput(lst:List[Double]): Unit ={
    for (i<- 0 until input.length){
      input(i).value = lst(i)
    }
  }
}