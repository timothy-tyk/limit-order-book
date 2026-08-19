## Milestone 3: Benchmarking Baseline

This milestone adds a benchmark runner, workload profiles, event counting, and per-command latency recording for the single-threaded matching engine.

The goals of this milestone were to:

- establish a reproducible baseline benchmark,
- validate that the engine can process realistic workload mixes,
- exercise successful add/cancel/modify/market paths,
- measure throughput and approximate command-processing latency,
- remove stale-order cancel/modify rejections from the main benchmark profiles.

A live-order-aware workload generator was introduced so that cancel and modify commands target orders that are still live. This eliminated `UNKNOWN_ORDER` rejections in the main benchmark profiles and allowed the benchmarks to exercise successful cancel/modify behavior rather than mostly rejection paths.

---

## Benchmark Environment

| Item                 | Value                                       |
|----------------------|---------------------------------------------|
| OS                   | MacOS 26.6.2 (25G83)                        |
| CPU                  | Apple M5 Pro 48GB RAM                       |
| JDK                  | Java 26                                     |
| JVM flags            | default / none                              |
| Engine mode          | Single-threaded synchronous matching engine |
| Event mode           | EventRecorder in counting mode              |
| Workload seed        | 42                                          |
| Commands per profile | 1,000,000                                   |
| Warmup runs          | 3                                           |

---

## Workload Profiles

### `ADD_ONLY`

Only limit-order add commands are generated.
Mix - Add (100%)
This profile exercises:

- limit order acceptance,
- matching,
- trade generation,
- order resting,
- book mutation.

### `ADD_AND_MARKET`

A mix of limit-order adds and market orders.
Mix - Add Limit(100%), Market(15% of Add Limit)
This profile exercises:

- limit order acceptance,
- market order acceptance,
- aggressive matching,
- trade generation.

### `BALANCED`

A mixed workload containing limit adds (50%), cancels (25%), modifies (25%), and market orders (15% of Add).
Mix - Add Limit (50%), Cancels (25%), Modifies (25%), Market (15% of Add)
This profile exercises:

- order acceptance,
- successful cancels,
- successful modifies,
- market orders,
- matching,
- event emission.

### `HIGH_ADD_LOW_CHURN`

A higher add-rate workload with a smaller proportion of cancels and modifies.
Mix - Add (80%), Cancels (10%), Modifies (10%), Market (15% of Add)
This profile exercises:

- book buildup,
- matching,
- occasional cancel/modify churn.

### `CANCEL_HEAVY`

A workload with a high proportion of cancel and modify commands.
Mix - Add (30%), Cancels (30%), Modifies (40%), Market (5% of Add)
This profile exercises:

- successful cancel path,
- successful modify path,
- live-order lookup,
- book removal,
- event emission.

---

## Benchmark Results
The single-threaded engine processes the tested workload profiles at approximately 3M to 10M commands/sec.
Per-command processing latencies appear to be in the approximate low-hundreds-of-nanoseconds range, but these values are subject to timer quantization and should be used for relative comparison only.

| Profile              |  Commands | Elapsed ms | Throughput |   p50 |   p90 |   p99 |    p99.9 |      max |       avg |
|----------------------|----------:|-----------:|-----------:|------:|------:|------:|---------:|---------:|----------:|
| `ADD_ONLY`           | 1,000,000 |        303 |  3.30M/sec | 125ns | 333ns | 709ns | 19,834ns | 19,834ns | 196.713ns |
| `ADD_AND_MARKET`     | 1,000,000 |        191 |  5.24M/sec | 125ns | 291ns | 500ns |  3,875ns |  3,875ns |  156.37ns |
| `BALANCED`           | 1,000,000 |        116 |  8.62M/sec |  83ns | 167ns | 292ns |  1,541ns |  1,541ns | 102.694ns |
| `HIGH_ADD_LOW_CHURN` | 1,000,000 |        151 |  6.62M/sec | 125ns | 250ns | 458ns |   1709ns |   1709ns |  139.09ns |
| `CANCEL_HEAVY`       | 1,000,000 |         99 | 10.10M/sec |  83ns | 167ns | 291ns |    833ns |    833ns |  94.624ns |