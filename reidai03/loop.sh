#!/usr/local/bin/bash

# javac Solver.java && java TSP2D | python main.py

# 5時間回すか
for i in {1..30}
do 
    echo $i
    # ./one.sh
    javac Solver.java && java TSP2D | python main.py >> results.csv
done
