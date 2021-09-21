docker exec -it c0 /bin/bash

./bin/spark-shell --master spark://spark-master:7077
./bin/pyspark --master spark://spark-master:7077


./bin/run-example --master spark://spark-master:7077 SparkPi 1000
./bin/spark-submit --master spark://spark-master:7077 examples/src/main/python/pi.py 1000

./bin/spark-submit --master spark://spark-master:7077 --class SimpleApp /opt/spark-apps/fuzzy/fuzzylogic/target/scala-2.12/fuzzylogic_2.12-0.1.0-SNAPSHOT.jar
./bin/spark-submit --master spark://spark-master:7077 --class simpleFuzzy /opt/spark-apps/fuzzy2/fuzzylogic/target/scala-2.12/fuzzylogic_v2_2.12-0.1.0-SNAPSHOT.jar