import FuzzyLogic._
object main extends App {
    // input
    var service = variable("service", (0, 10), 0.5)
    var food = variable("food", (0, 10), 0.5)
    var atm = variable("atm", (0, 10), 0.5)
    // output
    var tip = variable("tip", (0, 10))

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
      defuzz = Defuzzificaton.centroid, 
      step = 10)


    fis.setInput(List(3, 2, 5))

    fis.If(atm is hot) Then (tip will_be cheap)
    fis If ((service is_not good) or (food is delicious) or (atm is warm)) Then (tip will_be cheap)
    fis If ((service is poor) or (food is rancid) or (atm is cool)) Then (tip will_be generous)

    println(fis.defuzz(tip))
}
