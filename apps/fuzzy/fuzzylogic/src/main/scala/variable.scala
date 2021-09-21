package FuzzyLogic

import scala.collection.immutable.NumericRange
import scala.math.{max, min}

case class exprIf(var value:Double, var andMethod: (Double, Double)=>Double, var orMethod: (Double, Double)=>Double) {
  val and=(e:exprIf)=>exprIf(this.andMethod(this.value, e.value),this.andMethod, this.orMethod)
  val or= (e:exprIf)=>exprIf(this.orMethod(this.value, e.value),this.andMethod, this.orMethod)
}
case class exprThen(){
  val and=(e: exprThen)=>exprThen()
}
case class variable(name: String, range: (Double, Double)= (0,10),
                    var value:Double= -1.0,
                    var andMethod:(Double, Double)=>Double=AndMethod.Min,
                    var orMethod:(Double, Double)=>Double=OrMethod.Max,
                    var aggregation: (List[Double], List[Double])=> List[Double]=Aggregation.Max){
  var x_range:List[BigDecimal]=List()
  var Y:List[Double]=List()
  def init(step:Double): Unit ={
    x_range =(BigDecimal(range._1) to BigDecimal(range._2) by (BigDecimal(range._2)-BigDecimal(range._1)).toDouble/step).toList
    Y = (for(i<- x_range) yield i.doubleValue*0).toList
  }
  val is = (prop:membershipFunc)=>exprIf(prop.execute(value),this.andMethod, this.orMethod)
  val is_not = (prop:membershipFunc)=>exprIf(1-prop.execute(value), this.andMethod, this.orMethod)
  val will_be = (prop:membershipFunc)=> {
    this.Y= aggregation(this.Y,(for (i <- x_range) yield if (prop.execute(i.doubleValue) <= value) prop.execute(i.doubleValue) else value).toList)
    exprThen()
  }
}

