import FuzzyLogic._
import org.apache.spark.sql.SparkSession
import org.apache.spark._
import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.storage.StorageLevel
import java.time._ 
import java.time.format.DateTimeFormatter
import java.io._;
import java.net._;
object irrigation {
    def main(args: Array[String]): Unit = {
        val soc = new Socket("10.1.8.80", 12346)
        val dout = new DataOutputStream(soc.getOutputStream());
        val spark= SparkSession.builder().appName("SparkIrrigation").getOrCreate()
        val sc = spark.sparkContext

        var temp = variable("temp", (15,60))
        var lux  = variable("lux", (0,12000))
        var soil = variable("soil", (0,80))
        var output = variable("out", (0,32),sc=sc)

        var cold = trapmf("cold",15,15,25,30)
        var medium_t = trimf("medium", 25,30,35)
        var hot = trapmf("hot", 30,35,60,60)

        var dark = trapmf("dark", 0,0,0.25,0.5)
        var medium_l = trapmf("medium", 0.25,0.5,7500,10000)
        var light = trapmf("light", 7500, 10000,12000,12000)

        var dry = trapmf("dry", 0,0,17,33)
        var medium_s = trimf("medium", 17,33,50)
        var wet = trapmf("wet", 33,50,80,80)

        var zero = trimf("zero", 0,0,8)
        var veryshort = trimf("veryshort", 0,8,16)
        var short = trimf("short", 8,16,24)
        var long = trimf("long", 16,24,32)
        var verylong = trimf("verylong", 24,32,32)

        val fis = FIS_Mamdani(name = "tipdemo",
            input = List(temp, lux, soil),
            output = List(output),
            andMethod = AndMethod.Min,
            orMethod = OrMethod.Max,
            aggregation = Aggregation.Max,
            defuzzification = Defuzzificaton.centroid, 
            step = 10000)

        import spark.implicits._
        val schema = new StructType()
        .add("temp",DoubleType)
        .add("lux", DoubleType)
        .add("soil", DoubleType)
        .add("date", StringType)
        .add("ts", StringType)
        var check = false
        val irrigation_script = List(
        (0,30),(1,30), (2,30), (3,30), (4, 30), (5,30), (6,30), (7,30),
        (8,50),(9,50),(10,50),(11,50),(12,50),(13,50),(14,50),(15,50),
        (16,70),(17,70),(18,70),(19,70),(20,70),(21,70),(22,70),(23,70), 
        )
        var ref_soil = 0
        while (true){
            for ((h, s)<- irrigation_script){
                if (h==LocalDateTime.now().getHour()){ref_soil = s}
            }
            println(check)
            println("start read data")
            var df:org.apache.spark.sql.DataFrame = null
            try{
                df = spark.read.csv("/user/spark/irrigation_csv/partition="+LocalDateTime.now().plusMinutes(-1).format(DateTimeFormatter.ofPattern("HH-dd-MMMM-yyyy")) )
            }
            catch{
                case e: Exception =>{
                    println("Path does not exist. please wait 3m")
                    Thread.sleep(3 * 60 * 1000)
                    df = spark.read.csv("/user/spark/irrigation_csv/partition="+LocalDateTime.now().plusMinutes(-1).format(DateTimeFormatter.ofPattern("HH-dd-MMMM-yyyy")) )
                }
            }
            val row = df.orderBy(col("_c4").desc).collect.head
            val temp_data = row(0).asInstanceOf[String].toDouble
            val lux_data = row(1).asInstanceOf[String].toDouble
            val soil_data = row(2).asInstanceOf[String].toDouble
            if (check == true){
                check = false
                val rowDF_after = Seq((row(0).asInstanceOf[String],row(1).asInstanceOf[String],row(2).asInstanceOf[String],row(3).asInstanceOf[String],row(4).asInstanceOf[String], "after") ).toDF()
                rowDF_after.coalesce(1).write.mode(SaveMode.Append).csv("/user/spark/result")
                //rowDF_after.show()
            }
            // val temp_data = 25.1
            // val lux_data = 10000
            // val soil_data = 15
            if (soil_data < ref_soil){
                check = true
                println(temp_data, lux_data, soil_data)
                fis.setInput(List(temp_data, lux_data, soil_data))
                fis.If(soil is wet) Then (output will_be zero)
                fis.If((temp is cold) and (lux is light) and (soil is medium_s)) Then (output will_be short)
                fis.If((temp is cold) and (lux is medium_l) and (soil is medium_s)) Then (output will_be short)
                fis.If((temp is cold) and (lux is dark) and (soil is medium_s)) Then (output will_be short)

                fis.If((temp is medium_t) and (lux is light) and (soil is medium_s)) Then (output will_be veryshort)
                fis.If((temp is medium_t) and (lux is medium_l) and (soil is medium_s)) Then (output will_be short)
                fis.If((temp is medium_t) and (lux is dark) and (soil is medium_s)) Then (output will_be short)

                fis.If((temp is hot) and (lux is light) and (soil is medium_s)) Then (output will_be zero)
                fis.If((temp is hot) and (lux is medium_l) and (soil is medium_s)) Then (output will_be veryshort)
                fis.If((temp is hot) and (lux is dark) and (soil is medium_s)) Then (output will_be long)

                fis.If((temp is cold) and (lux is light) and (soil is dry)) Then (output will_be verylong)
                fis.If((temp is cold) and (lux is medium_l) and (soil is dry)) Then (output will_be verylong)
                fis.If((temp is cold) and (lux is dark) and (soil is dry)) Then (output will_be verylong)

                fis.If((temp is medium_t) and (lux is light) and (soil is dry)) Then (output will_be verylong)
                fis.If((temp is medium_t) and (lux is medium_l) and (soil is dry)) Then (output will_be verylong)
                fis.If((temp is medium_t) and (lux is dark) and (soil is dry)) Then (output will_be verylong)

                fis.If((temp is hot) and (lux is light) and (soil is dry)) Then (output will_be zero)
                fis.If((temp is hot) and (lux is medium_l) and (soil is dry)) Then (output will_be veryshort)
                fis.If((temp is hot) and (lux is dark) and (soil is dry)) Then (output will_be verylong)
                
                val irrigation_time = fis.defuzz(output)
                //Socket.sendMsg(irrigation_time.toString)
                dout.writeUTF(irrigation_time.toString);
                dout.flush();
                println(irrigation_time)
                
                val rowDF_before = Seq((row(0).asInstanceOf[String],row(1).asInstanceOf[String],row(2).asInstanceOf[String],row(3).asInstanceOf[String],row(4).asInstanceOf[String], "before", irrigation_time) ).toDF()
                rowDF_before.coalesce(1).write.mode(SaveMode.Append).csv("/user/spark/result")
                //rowDF_before.show()
                println("irrigating waiting in "+ irrigation_time.toString+ "m.....")
                Thread.sleep((irrigation_time * 60*1000).toInt)
                println("stable soil moisture waiting in "+ (0.5*irrigation_time).toString+ "m.....")
                Thread.sleep((irrigation_time* 0.5 * 60*1000).toInt)
                
            }    
            val update_wait = 1
            println("waiting for updating data in "+ update_wait.toString +"m.....")
            Thread.sleep((update_wait * 60 * 1000).toInt)
        }
        spark.stop();
        dout.close();
        soc.close();
    }
}