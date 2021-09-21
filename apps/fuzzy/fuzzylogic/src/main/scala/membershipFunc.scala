package FuzzyLogic
import scala.math.{max, min,pow,E}
trait membershipFunc{
  def execute(x:Double):Double
}
case class trimf(name: String, a: Double=0, b: Double=5, c:Double=10) extends membershipFunc {
  def execute(x: Double): Double={
    if (a<=b && b<=c)
      if (a<=x && x<=c) {
        if (x==b) 1
        else {
          if (x < b) (x - a) / (b - a)
          else (c - x) / (c - b)
        }
      }
      else 0
    else 0
  }
}
case class trapmf(name:String, a:Double, b:Double, c:Double, d:Double)extends membershipFunc{
  def execute(x:Double):Double={
    if (a<=b && b<=c && c<=d)
      if (a<=x && x<=d) {
        if (b<=x && x<=c) 1
        else {
          if (x < b) (x - a) / (b - a)
          else (d - x) / (d - c)
        }
      } else 0
    else 0
  }
}
case class gbellmf(name:String, a:Double, b:Double, c:Double)extends membershipFunc {
  def execute(x:Double):Double={1/(1+ pow(((x-c)/a).abs,2*b))}
}
case class gaussmf(name:String, mean:Double, deviation: Double)extends membershipFunc {
  def execute(x:Double):Double={pow(E,(-1*pow(x-mean,2))/(2*pow(deviation,2)))}
}
case class sigmf(name:String, a:Double, c:Double) extends  membershipFunc{
  def execute(x:Double):Double={1/(1+pow(E, -1*a*(x-c) ))}
}
case class pimf(name:String, a:Double, b:Double, c:Double, d:Double){
  def execute(x:Double):Double={
    if (x<=a) 0
    if (a<x && x<=(a+b)/2) 2*pow((x-a)/(b-a),2)
    if ((a+b)/2 < x && x<=b) 1-  2*pow((x-b)/(b-a),2)
    if (b<x && x<=c) 1
    if (c<x && x<=(c+d)/2)  1-  2*pow((x-c)/(d-c),2)
    if ((c+d)/2 < x && x<=d) 2*pow((x-d)/(d-c),2)
    else 0
  }
}
case class smf(name:String, a:Double, b:Double){
  def execute(x:Double):Double={
    if(x<=a) 0
    if(a<x && x<=(a+b)/2) 2*pow((x-a)/(b-a),2)
    if((a+b)/2<x && x<=b) 1-2*pow((x-b)/(b-a),2)
    else 1
  }
}
case class zmf(name:String, a:Double, b:Double){
  def execute(x:Double):Double={
    if (x<=a) 1
    if (a<x && x<= (a+b)/2) 1-2*pow((x-a)/(b-a),2)
    if ((a+b)/2 < x && x <=b) 2*pow((x-b)/(b-a),2)
    else 0
  }
}
