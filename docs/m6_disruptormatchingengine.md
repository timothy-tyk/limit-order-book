Engine Type: Disruptor MPSC
Queue Capacity: 65536
Wait Strategy: com.lmax.disruptor.YieldingWaitStrategy
Backpressure Policy: SPIN_RETRY

=== Profile: MT_ADD_ONLY | Commands: 1000000 | Seed: 42 ===
Threads: 1, Elapsed: 438.473 ms, Throughput: 2.28M/sec
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
Total Trades: 792872
Last Event Sequence: 1792872

Latency:
p50: 17110500ns | 17.1105ms
p90: 20264083ns | 20.264083ms
p99: 20756458ns | 20.756458ms
p99.9: 20805000ns | 20.805ms
max: 20809041ns | 20.809041ms
avg: 1.66841962738E7ns | 16.6841962738ms


Processed Commands: 1000000
Publish Retries: 41797032
Invariance Check Started
Invariance Check Complete

Threads: 2, Elapsed: 460.161 ms, Throughput: 2.17M/sec
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
Total Trades: 793428
Last Event Sequence: 1793428

Latency:
p50: 3521333ns | 3.521333ms
p90: 5635542ns | 5.635542ms
p99: 6157292ns | 6.157292ms
p99.9: 6217625ns | 6.217625ms
max: 6226584ns | 6.226584ms
avg: 3499130.3602ns | 3.4991303602ms


Processed Commands: 1000000
Publish Retries: 5985673
Invariance Check Started
Invariance Check Complete

Threads: 4, Elapsed: 303.267 ms, Throughput: 3.30M/sec
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
Total Trades: 792654
Last Event Sequence: 1792654

Latency:
p50: 2285875ns | 2.285875ms
p90: 3032333ns | 3.032333ms
p99: 3174708ns | 3.174708ms
p99.9: 3188833ns | 3.188833ms
max: 3190500ns | 3.1905ms
avg: 2115857.1396ns | 2.1158571395999997ms


Processed Commands: 1000000
Publish Retries: 4120193
Invariance Check Started
Invariance Check Complete

Threads: 8, Elapsed: 488.389 ms, Throughput: 2.05M/sec
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
Total Trades: 792817
Last Event Sequence: 1792817

Latency:
p50: 2070708ns | 2.070708ms
p90: 3994166ns | 3.994166ms
p99: 4519709ns | 4.519709ms
p99.9: 4567583ns | 4.567583ms
max: 4572542ns | 4.572542ms
avg: 2131289.6475ns | 2.1312896475ms


Processed Commands: 1000000
Publish Retries: 7349965
Invariance Check Started
Invariance Check Complete

=== Profile: MT_ADD_AND_MARKET | Commands: 1000000 | Seed: 42 ===
Threads: 1, Elapsed: 359.045 ms, Throughput: 2.79M/sec
Events:
Orders Accepted: 850133
Orders Modified: 0
Orders Cancelled: 0
Orders Rejected: 0
Rejection Reason: UNKNOWN_ORDER | 0
Rejection Reason: DUPLICATED_ORDER_ID | 0
Rejection Reason: INVALID_PRICE | 0
Rejection Reason: INVALID_QTY | 0
Rejection Reason: NO_LIQUIDITY | 0
Market Orders Accepted: 149867
Market Orders Rejected: 0
Total Trades: 906721
Last Event Sequence: 1906721

Latency:
p50: 1856666ns | 1.856666ms
p90: 2812083ns | 2.812083ms
p99: 3092667ns | 3.092667ms
p99.9: 3114416ns | 3.114416ms
max: 3119708ns | 3.119708ms
avg: 1811602.2245ns | 1.8116022245ms


Processed Commands: 1000000
Publish Retries: 58258266
Invariance Check Started
Invariance Check Complete

Threads: 2, Elapsed: 280.202 ms, Throughput: 3.57M/sec
Events:
Orders Accepted: 849606
Orders Modified: 0
Orders Cancelled: 0
Orders Rejected: 0
Rejection Reason: UNKNOWN_ORDER | 0
Rejection Reason: DUPLICATED_ORDER_ID | 0
Rejection Reason: INVALID_PRICE | 0
Rejection Reason: INVALID_QTY | 0
Rejection Reason: NO_LIQUIDITY | 0
Market Orders Accepted: 150394
Market Orders Rejected: 0
Total Trades: 907665
Last Event Sequence: 1907665

Latency:
p50: 1509916ns | 1.509916ms
p90: 2344417ns | 2.344417ms
p99: 2534708ns | 2.534708ms
p99.9: 2552708ns | 2.552708ms
max: 2553959ns | 2.553959ms
avg: 1465880.2859ns | 1.4658802859ms


Processed Commands: 1000000
Publish Retries: 3285258
Invariance Check Started
Invariance Check Complete

Threads: 4, Elapsed: 258.026 ms, Throughput: 3.88M/sec
Events:
Orders Accepted: 850061
Orders Modified: 0
Orders Cancelled: 0
Orders Rejected: 0
Rejection Reason: UNKNOWN_ORDER | 0
Rejection Reason: DUPLICATED_ORDER_ID | 0
Rejection Reason: INVALID_PRICE | 0
Rejection Reason: INVALID_QTY | 0
Rejection Reason: NO_LIQUIDITY | 0
Market Orders Accepted: 149939
Market Orders Rejected: 0
Total Trades: 906056
Last Event Sequence: 1906056

Latency:
p50: 974167ns | 0.974167ms
p90: 1637333ns | 1.637333ms
p99: 1790083ns | 1.790083ms
p99.9: 1802875ns | 1.802875ms
max: 1804375ns | 1.804375ms
avg: 988497.3555ns | 0.9884973555ms


Processed Commands: 1000000
Publish Retries: 3622212
Invariance Check Started
Invariance Check Complete

Threads: 8, Elapsed: 438.167 ms, Throughput: 2.28M/sec
Events:
Orders Accepted: 849973
Orders Modified: 0
Orders Cancelled: 0
Orders Rejected: 0
Rejection Reason: UNKNOWN_ORDER | 0
Rejection Reason: DUPLICATED_ORDER_ID | 0
Rejection Reason: INVALID_PRICE | 0
Rejection Reason: INVALID_QTY | 0
Rejection Reason: NO_LIQUIDITY | 0
Market Orders Accepted: 150027
Market Orders Rejected: 0
Total Trades: 906484
Last Event Sequence: 1906484

Latency:
p50: 1571000ns | 1.571ms
p90: 2917750ns | 2.91775ms
p99: 3241041ns | 3.241041ms
p99.9: 3268417ns | 3.268417ms
max: 3271291ns | 3.271291ms
avg: 1580013.0588ns | 1.5800130588ms


Processed Commands: 1000000
Publish Retries: 6949478
Invariance Check Started
Invariance Check Complete

=== Profile: MT_MIXED_WITH_STALE_CANCELS | Commands: 1000000 | Seed: 42 ===
Threads: 1, Elapsed: 246.993 ms, Throughput: 4.05M/sec
Events:
Orders Accepted: 352343
Orders Modified: 21
Orders Cancelled: 31
Orders Rejected: 497490
Rejection Reason: UNKNOWN_ORDER | 497490
Rejection Reason: DUPLICATED_ORDER_ID | 0
Rejection Reason: INVALID_PRICE | 0
Rejection Reason: INVALID_QTY | 0
Rejection Reason: NO_LIQUIDITY | 0
Market Orders Accepted: 150115
Market Orders Rejected: 0
Total Trades: 479281
Last Event Sequence: 1479281

Latency:
p50: 2674792ns | 2.674792ms
p90: 3379000ns | 3.379ms
p99: 3634583ns | 3.634583ms
p99.9: 3647542ns | 3.647542ms
max: 3647792ns | 3.647792ms
avg: 2484164.4818ns | 2.4841644818ms


Processed Commands: 1000000
Publish Retries: 9471680
Invariance Check Started
Invariance Check Complete

Threads: 2, Elapsed: 251.666 ms, Throughput: 3.97M/sec
Events:
Orders Accepted: 351474
Orders Modified: 6
Orders Cancelled: 5
Orders Rejected: 498441
Rejection Reason: UNKNOWN_ORDER | 498441
Rejection Reason: DUPLICATED_ORDER_ID | 0
Rejection Reason: INVALID_PRICE | 0
Rejection Reason: INVALID_QTY | 0
Rejection Reason: NO_LIQUIDITY | 0
Market Orders Accepted: 150074
Market Orders Rejected: 0
Total Trades: 478940
Last Event Sequence: 1478940

Latency:
p50: 1792084ns | 1.792084ms
p90: 2805750ns | 2.80575ms
p99: 3052042ns | 3.052042ms
p99.9: 3081708ns | 3.081708ms
max: 3085750ns | 3.08575ms
avg: 1745234.9747ns | 1.7452349747ms


Processed Commands: 1000000
Publish Retries: 2092129
Invariance Check Started
Invariance Check Complete

Threads: 4, Elapsed: 309.376 ms, Throughput: 3.23M/sec
Events:
Orders Accepted: 351243
Orders Modified: 6
Orders Cancelled: 2
Orders Rejected: 498860
Rejection Reason: UNKNOWN_ORDER | 498860
Rejection Reason: DUPLICATED_ORDER_ID | 0
Rejection Reason: INVALID_PRICE | 0
Rejection Reason: INVALID_QTY | 0
Rejection Reason: NO_LIQUIDITY | 0
Market Orders Accepted: 149889
Market Orders Rejected: 0
Total Trades: 478433
Last Event Sequence: 1478433

Latency:
p50: 2886125ns | 2.886125ms
p90: 5079167ns | 5.079167ms
p99: 5431042ns | 5.431042ms
p99.9: 5456250ns | 5.45625ms
max: 5458625ns | 5.458625ms
avg: 2879108.8213ns | 2.8791088213ms


Processed Commands: 1000000
Publish Retries: 3836530
Invariance Check Started
Invariance Check Complete

Threads: 8, Elapsed: 400.086 ms, Throughput: 2.50M/sec
Events:
Orders Accepted: 352270
Orders Modified: 2
Orders Cancelled: 3
Orders Rejected: 497743
Rejection Reason: UNKNOWN_ORDER | 497743
Rejection Reason: DUPLICATED_ORDER_ID | 0
Rejection Reason: INVALID_PRICE | 0
Rejection Reason: INVALID_QTY | 0
Rejection Reason: NO_LIQUIDITY | 0
Market Orders Accepted: 149982
Market Orders Rejected: 0
Total Trades: 479596
Last Event Sequence: 1479596

Latency:
p50: 3159542ns | 3.159542ms
p90: 5408834ns | 5.408834ms
p99: 5791167ns | 5.791167ms
p99.9: 5899250ns | 5.89925ms
max: 5903333ns | 5.903333ms
avg: 3186441.527ns | 3.186441527ms


Processed Commands: 1000000
Publish Retries: 5477795
Invariance Check Started
Invariance Check Complete

=== Profile: MT_THREAD_LOCAL_CHURN | Commands: 1000000 | Seed: 42 ===
Threads: 1, Elapsed: 287.836 ms, Throughput: 3.47M/sec
Events:
Orders Accepted: 650088
Orders Modified: 20087
Orders Cancelled: 24461
Orders Rejected: 155633
Rejection Reason: UNKNOWN_ORDER | 155633
Rejection Reason: DUPLICATED_ORDER_ID | 0
Rejection Reason: INVALID_PRICE | 0
Rejection Reason: INVALID_QTY | 0
Rejection Reason: NO_LIQUIDITY | 0
Market Orders Accepted: 149731
Market Orders Rejected: 0
Total Trades: 763028
Last Event Sequence: 1763028

Latency:
p50: 1327208ns | 1.327208ms
p90: 2294541ns | 2.294541ms
p99: 2521458ns | 2.521458ms
p99.9: 2546917ns | 2.546917ms
max: 2547542ns | 2.547542ms
avg: 1293714.4831ns | 1.2937144831ms


Processed Commands: 1000000
Publish Retries: 16037188
Invariance Check Started
Invariance Check Complete

Threads: 2, Elapsed: 316.384 ms, Throughput: 3.16M/sec
Events:
Orders Accepted: 650293
Orders Modified: 20512
Orders Cancelled: 24659
Orders Rejected: 154476
Rejection Reason: UNKNOWN_ORDER | 154476
Rejection Reason: DUPLICATED_ORDER_ID | 0
Rejection Reason: INVALID_PRICE | 0
Rejection Reason: INVALID_QTY | 0
Rejection Reason: NO_LIQUIDITY | 0
Market Orders Accepted: 150060
Market Orders Rejected: 0
Total Trades: 763804
Last Event Sequence: 1763804

Latency:
p50: 1255708ns | 1.255708ms
p90: 2209459ns | 2.209459ms
p99: 2426459ns | 2.426459ms
p99.9: 2442917ns | 2.442917ms
max: 2444958ns | 2.444958ms
avg: 1248335.0359ns | 1.2483350359ms


Processed Commands: 1000000
Publish Retries: 3686414
Invariance Check Started
Invariance Check Complete

Threads: 4, Elapsed: 282.407 ms, Throughput: 3.54M/sec
Events:
Orders Accepted: 650771
Orders Modified: 20404
Orders Cancelled: 24800
Orders Rejected: 153984
Rejection Reason: UNKNOWN_ORDER | 153984
Rejection Reason: DUPLICATED_ORDER_ID | 0
Rejection Reason: INVALID_PRICE | 0
Rejection Reason: INVALID_QTY | 0
Rejection Reason: NO_LIQUIDITY | 0
Market Orders Accepted: 150041
Market Orders Rejected: 0
Total Trades: 764098
Last Event Sequence: 1764098

Latency:
p50: 2045583ns | 2.045583ms
p90: 3752541ns | 3.752541ms
p99: 4147166ns | 4.147166ms
p99.9: 4185791ns | 4.185791ms
max: 4190083ns | 4.190083ms
avg: 2045201.6492ns | 2.0452016492ms


Processed Commands: 1000000
Publish Retries: 4317140
Invariance Check Started
Invariance Check Complete

Threads: 8, Elapsed: 435.313 ms, Throughput: 2.30M/sec
Events:
Orders Accepted: 649726
Orders Modified: 20538
Orders Cancelled: 24711
Orders Rejected: 155078
Rejection Reason: UNKNOWN_ORDER | 155078
Rejection Reason: DUPLICATED_ORDER_ID | 0
Rejection Reason: INVALID_PRICE | 0
Rejection Reason: INVALID_QTY | 0
Rejection Reason: NO_LIQUIDITY | 0
Market Orders Accepted: 149947
Market Orders Rejected: 0
Total Trades: 763194
Last Event Sequence: 1763194

Latency:
p50: 5024958ns | 5.024958ms
p90: 6981833ns | 6.981833ms
p99: 7144125ns | 7.144125ms
p99.9: 7159958ns | 7.159958ms
max: 7161500ns | 7.1615ms
avg: 4646564.1118ns | 4.6465641118ms


Processed Commands: 1000000
Publish Retries: 7140601
Invariance Check Started
Invariance Check Complete

=== Profile: MT_ADD_THEN_CANCEL | Commands: 1000000 | Seed: 42 ===
Threads: 1, Elapsed: 216.317 ms, Throughput: 4.62M/sec
Events:
Orders Accepted: 500000
Orders Modified: 0
Orders Cancelled: 500000
Orders Rejected: 0
Rejection Reason: UNKNOWN_ORDER | 0
Rejection Reason: DUPLICATED_ORDER_ID | 0
Rejection Reason: INVALID_PRICE | 0
Rejection Reason: INVALID_QTY | 0
Rejection Reason: NO_LIQUIDITY | 0
Market Orders Accepted: 0
Market Orders Rejected: 0
Total Trades: 0
Last Event Sequence: 1000000

Latency:
p50: 289292ns | 0.289292ms
p90: 543416ns | 0.543416ms
p99: 605875ns | 0.605875ms
p99.9: 609167ns | 0.609167ms
max: 609459ns | 0.609459ms
avg: 300246.0844ns | 0.3002460844ms


Processed Commands: 1000000
Publish Retries: 24687755
Invariance Check Started
Invariance Check Complete

Threads: 2, Elapsed: 254.901 ms, Throughput: 3.92M/sec
Events:
Orders Accepted: 500000
Orders Modified: 0
Orders Cancelled: 500000
Orders Rejected: 0
Rejection Reason: UNKNOWN_ORDER | 0
Rejection Reason: DUPLICATED_ORDER_ID | 0
Rejection Reason: INVALID_PRICE | 0
Rejection Reason: INVALID_QTY | 0
Rejection Reason: NO_LIQUIDITY | 0
Market Orders Accepted: 0
Market Orders Rejected: 0
Total Trades: 0
Last Event Sequence: 1000000

Latency:
p50: 24541ns | 0.024541ms
p90: 52917ns | 0.052917ms
p99: 59541ns | 0.059541ms
p99.9: 61042ns | 0.061042ms
max: 61333ns | 0.061333ms
avg: 27531.8256ns | 0.0275318256ms


Processed Commands: 1000000
Publish Retries: 3716558
Invariance Check Started
Invariance Check Complete

Threads: 4, Elapsed: 284.725 ms, Throughput: 3.51M/sec
Events:
Orders Accepted: 500000
Orders Modified: 0
Orders Cancelled: 500000
Orders Rejected: 0
Rejection Reason: UNKNOWN_ORDER | 0
Rejection Reason: DUPLICATED_ORDER_ID | 0
Rejection Reason: INVALID_PRICE | 0
Rejection Reason: INVALID_QTY | 0
Rejection Reason: NO_LIQUIDITY | 0
Market Orders Accepted: 0
Market Orders Rejected: 0
Total Trades: 0
Last Event Sequence: 1000000

Latency:
p50: 1243750ns | 1.24375ms
p90: 1814667ns | 1.814667ms
p99: 1909875ns | 1.909875ms
p99.9: 1918750ns | 1.91875ms
max: 1919625ns | 1.919625ms
avg: 1084162.9091ns | 1.0841629091ms


Processed Commands: 1000000
Publish Retries: 4652395
Invariance Check Started
Invariance Check Complete

Threads: 8, Elapsed: 405.697 ms, Throughput: 2.46M/sec
Events:
Orders Accepted: 500000
Orders Modified: 0
Orders Cancelled: 500000
Orders Rejected: 0
Rejection Reason: UNKNOWN_ORDER | 0
Rejection Reason: DUPLICATED_ORDER_ID | 0
Rejection Reason: INVALID_PRICE | 0
Rejection Reason: INVALID_QTY | 0
Rejection Reason: NO_LIQUIDITY | 0
Market Orders Accepted: 0
Market Orders Rejected: 0
Total Trades: 0
Last Event Sequence: 1000000

Latency:
p50: 1482458ns | 1.482458ms
p90: 2684917ns | 2.684917ms
p99: 2897375ns | 2.897375ms
p99.9: 2919667ns | 2.919667ms
max: 2923000ns | 2.923ms
avg: 1492555.8578ns | 1.4925558578000002ms


Processed Commands: 1000000
Publish Retries: 4619778
Invariance Check Started
Invariance Check Complete