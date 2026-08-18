Orders Accepted: 699718
Orders Modified: 19956
Orders Cancelled: 40015
Orders Rejected: 240311
Market Orders Accepted: 0
Market Orders Rejected: 0
Total Trades: 554624
Last Event Sequence: 1554624
Commands processed: 1000000
Elapsed ms: 25707
====

Orders Accepted: 1000000
Orders Modified: 0
Orders Cancelled: 0
Orders Rejected: 0
Market Orders Accepted: 0
Market Orders Rejected: 0
Total Trades: 794285
Last Event Sequence: 1794285
Commands processed: 1000000
Elapsed ms: 193
====

Orders Accepted: 500006
Orders Modified: 161470
Orders Cancelled: 161616
Orders Rejected: 176908
Market Orders Accepted: 0
Market Orders Rejected: 0
Total Trades: 259398
Last Event Sequence: 1259398
Commands processed: 1000000
Elapsed ms: 167
====

Orders Accepted: 500000
Orders Modified: 202411
Orders Cancelled: 151933
Orders Rejected: 145656
Market Orders Accepted: 0
Market Orders Rejected: 0
Total Trades: 251415
Last Event Sequence: 1251415
Commands processed: 1000000
Elapsed ms: 176
====

Orders Accepted: 799280
Orders Modified: 19650
Orders Cancelled: 20166
Orders Rejected: 160904
Market Orders Accepted: 0
Market Orders Rejected: 0
Total Trades: 634670
Last Event Sequence: 1634670
Commands processed: 1000000
Elapsed ms: 25633
====

Orders Accepted: 600357
Orders Modified: 20001
Orders Cancelled: 59805
Orders Rejected: 319837
Market Orders Accepted: 0
Market Orders Rejected: 0
Total Trades: 476048
Last Event Sequence: 1476048
Commands processed: 1000000
Elapsed ms: 17920
====

Orders Accepted: 699718
Orders Modified: 19956
Orders Cancelled: 40015
Orders Rejected: 240311
Market Orders Accepted: 0
Market Orders Rejected: 0
Total Trades: 554624
Last Event Sequence: 1554624
Commands processed: 1000000
Elapsed ms: 25922
===

< === LinkedHashMap Impl === >

Orders Accepted: 699718
Orders Modified: 19956
Orders Cancelled: 40015
Orders Rejected: 240311
Market Orders Accepted: 0
Market Orders Rejected: 0
Total Trades: 554624
Last Event Sequence: 1554624
Commands processed: 1000000
Elapsed ms: 26135



/Users/timothytan/Library/Java/JavaVirtualMachines/openjdk-26.0.1/Contents/Home/bin/java -javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=58685 -Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8 -classpath /Users/timothytan/Documents/Projects/limit-order-book/java-limit-order-book/target/classes benchmark.BenchmarkRunner
avg: 119.739ns

================
Profile: ADD_ONLY
Seed: 42
Commands: 1000000
Warmup Runs: 3

Latency:
p50: 84ns
p90: 208ns
p99: 375ns
p99.9: 5417ns
max: 5417ns
Events:
Orders Accepted: 1000000
Orders Modified: 0
Orders Cancelled: 0
Orders Rejected: 0
Rejection Reason: UNKNOWN_ORDER | 0
Rejection Reason: DUPLICATED_ORDER_ID | 0
Rejection Reason: INVALID_PRICE | 0
Rejection Reason: INVALID_QTY | 0
Rejection Reason: NO_LIQUIDITY | 0
Market Orders Accepted: 0
Market Orders Rejected: 0
Total Trades: 793404
Last Event Sequence: 1793404


Elapsed ms: 156
Throughput: 6.41M/sec
================

================
Profile: ADD_AND_MARKET
Seed: 42
Commands: 1000000
Warmup Runs: 3

Latency:
p50: 83ns
p90: 167ns
p99: 834ns
p99.9: 2083ns
max: 2083ns
avg: 112.444ns

Events:
Orders Accepted: 849947
Orders Modified: 0
Orders Cancelled: 0
Orders Rejected: 0
Rejection Reason: UNKNOWN_ORDER | 0
Rejection Reason: DUPLICATED_ORDER_ID | 0
Rejection Reason: INVALID_PRICE | 0
Rejection Reason: INVALID_QTY | 0
Rejection Reason: NO_LIQUIDITY | 0
Market Orders Accepted: 150053
Market Orders Rejected: 0
Total Trades: 907127
Last Event Sequence: 1907127


Elapsed ms: 185
Throughput: 5.41M/sec
================

================
Profile: BALANCED
Seed: 42
Commands: 1000000
Warmup Runs: 3

Latency:
p50: 83ns
p90: 125ns
p99: 250ns
p99.9: 1000ns
max: 1000ns
avg: 79.91ns

Events:
Orders Accepted: 467210
Orders Modified: 225149
Orders Cancelled: 225191
Orders Rejected: 0
Rejection Reason: UNKNOWN_ORDER | 0
Rejection Reason: DUPLICATED_ORDER_ID | 0
Rejection Reason: INVALID_PRICE | 0
Rejection Reason: INVALID_QTY | 0
Rejection Reason: NO_LIQUIDITY | 0
Market Orders Accepted: 82450
Market Orders Rejected: 0
Total Trades: 216004
Last Event Sequence: 1216004


Elapsed ms: 98
Throughput: 10.20M/sec
================

================
Profile: HIGH_ADD_LOW_CHURN
Seed: 42
Commands: 1000000
Warmup Runs: 3

Latency:
p50: 83ns
p90: 167ns
p99: 292ns
p99.9: 584ns
max: 584ns
avg: 90.275ns

Events:
Orders Accepted: 681238
Orders Modified: 99543
Orders Cancelled: 99329
Orders Rejected: 0
Rejection Reason: UNKNOWN_ORDER | 0
Rejection Reason: DUPLICATED_ORDER_ID | 0
Rejection Reason: INVALID_PRICE | 0
Rejection Reason: INVALID_QTY | 0
Rejection Reason: NO_LIQUIDITY | 0
Market Orders Accepted: 119890
Market Orders Rejected: 0
Total Trades: 613912
Last Event Sequence: 1613912


Elapsed ms: 117
Throughput: 8.55M/sec
================

================
Profile: CANCEL_HEAVY
Seed: 42
Commands: 1000000
Warmup Runs: 3

Latency:
p50: 42ns
p90: 84ns
p99: 167ns
p99.9: 292ns
max: 292ns
avg: 61.38ns

Events:
Orders Accepted: 422863
Orders Modified: 237693
Orders Cancelled: 316812
Orders Rejected: 0
Rejection Reason: UNKNOWN_ORDER | 0
Rejection Reason: DUPLICATED_ORDER_ID | 0
Rejection Reason: INVALID_PRICE | 0
Rejection Reason: INVALID_QTY | 0
Rejection Reason: NO_LIQUIDITY | 0
Market Orders Accepted: 22632
Market Orders Rejected: 0
Total Trades: 82904
Last Event Sequence: 1082904


Elapsed ms: 80
Throughput: 12.50M/sec
================


Process finished with exit code 0
