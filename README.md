# limit-order-book
A limit order book program written in Java

Milestone 1: single-threaded command-driven LOB

Deliverables:
AddLimitOrderCommand, CancelOrderCommand, ModifyOrderCommand, MarketOrderCommand.
MatchingEngine interface.
SingleThreadedMatchingEngine.
RandomWorkloadGenerator.
Application.java that runs a small workload.
JUnit tests for:
- adding orders,
- matching,
- canceling,
- modifying,
- partial fills,
- price-time priority.

```bash
FOLDER-STRUCTURE

.
├── docs
│   ├── architecture.md
│   ├── benchmark-methodology.md
│   ├── design-decisions.md
│   ├── m4_reentrantmatchingengine.md
│   ├── m4_syncmatchingengine.md
│   ├── scratchpad.md
│   └── scratchpad2.md
├── java-limit-order-book
│   ├── pom.xml
│   └── src
│       ├── main
│       │   ├── java
│       │   │   ├── Application.java
│       │   │   ├── benchmark
│       │   │   │   ├── BenchmarkRunner.java
│       │   │   │   ├── ConcurrentBenchmarkRunner.java
│       │   │   │   ├── LatencyRecorder.java
│       │   │   │   └── WorkloadProfile.java
│       │   │   ├── command
│       │   │   │   ├── AddLimitOrderCommand.java
│       │   │   │   ├── CancelOrderCommand.java
│       │   │   │   ├── Command.java
│       │   │   │   ├── MarketOrderCommand.java
│       │   │   │   └── ModifyOrderCommand.java
│       │   │   ├── concurrency
│       │   │   │   ├── ConcurrentWorkloadRunner.java
│       │   │   │   └── WorkerCommandGenerator.java
│       │   │   ├── core
│       │   │   │   ├── Order.java
│       │   │   │   ├── OrderBook.java
│       │   │   │   ├── PriceLevel.java
│       │   │   │   └── Side.java
│       │   │   ├── engine
│       │   │   │   ├── concurrent
│       │   │   │   │   ├── ConcurrentMatchingEngine.java
│       │   │   │   │   ├── ReentrantLockMatchingEngine.java
│       │   │   │   │   └── SynchronizedMatchingEngine.java
│       │   │   │   ├── MatchingEngine.java
│       │   │   │   └── SingleThreadedMatchingEngine.java
│       │   │   ├── event
│       │   │   │   ├── Event.java
│       │   │   │   ├── EventListener.java
│       │   │   │   ├── MarketOrderAccepted.java
│       │   │   │   ├── MarketOrderRejected.java
│       │   │   │   ├── OrderAccepted.java
│       │   │   │   ├── OrderCancelled.java
│       │   │   │   ├── OrderModified.java
│       │   │   │   ├── OrderRejected.java
│       │   │   │   ├── Trade.java
│       │   │   │   └── TradeDTO.java
│       │   │   ├── utils
│       │   │   │   ├── LiveOrderIdSource.java
│       │   │   │   └── LiveOrderTracker.java
│       │   │   ├── validation
│       │   │   │   ├── concurrent
│       │   │   │   │   └── ConcurrentEventRecorder.java
│       │   │   │   ├── EventRecorder.java
│       │   │   │   ├── InvariantChecker.java
│       │   │   │   └── OrderRejectedReason.java
│       │   │   └── workload
│       │   │       ├── ConcurrentWorkloadGenerator.java
│       │   │       ├── RandomWorkloadGenerator.java
│       │   │       └── WorkloadGenerator.java
│       │   └── resources
│       └── test
│           └── java
│               ├── benchmark
│               │   └── LatencyRecorderTest.java
│               ├── concurrent
│               │   └── WorkerCommandGeneratorTest.java
│               ├── core
│               │   ├── OrderBookTest.java
│               │   └── PriceLevelTest.java
│               ├── engine
│               │   ├── ReentrantLockMatchingEngineTest.java
│               │   ├── SingleThreadedMatchingEngineTest.java
│               │   └── SynchronizedMatchingEngineTest.java
│               ├── event
│               │   ├── EventRecorderTest.java
│               │   ├── MarketOrderAcceptedTest.java
│               │   ├── MarketOrderRejectedTest.java
│               │   ├── OrderAcceptedTest.java
│               │   ├── OrderCancelledTest.java
│               │   ├── OrderModifiedTest.java
│               │   ├── OrderRejectedTest.java
│               │   └── TradeTest.java
│               └── workload
│                   └── RandomWorkloadGeneratorTest.java
├── profile.jfr
└── README.md
```

Milestone 1:
Single-threaded order book

Milestone 2:
Event-driven engine

Milestone 3:
Benchmark baseline and live-order-aware workload

## Milestone 4: Locked Concurrent Engine

Milestone 4 introduced thread-safe versions of the matching engine using coarse-grained locking.

Two implementations were benchmarked:

- `SynchronizedMatchingEngine`
- `ReentrantLockMatchingEngine`

Both implementations protect the order book by serializing all command processing under a single lock.

The benchmark profiles used were:

- `MT_ADD_ONLY`
- `MT_ADD_AND_MARKET`
- `MT_ADD_THEN_CANCEL`
- `MT_THREAD_LOCAL_CHURN`

The results in docs/m4_reentrantmatchingengine.md && m4_syncmatchingengine.md show that adding more threads does not produce linear throughput improvements. In most cases, throughput degrades once more than one thread is used.

This is expected because the matching engine’s critical section is serialized. Multiple producer threads still contend for the same lock, so the engine does not gain true internal parallelism.

The additional threads add lock acquisition overhead, scheduling overhead, cache contention, and context-switching overhead without removing the fundamental serialization point.

In these runs, `SynchronizedMatchingEngine` was generally competitive with or faster than `ReentrantLockMatchingEngine`. This is plausible because Java’s built-in monitor mechanism is heavily optimized by the JVM. `ReentrantLock` provides more flexibility, such as `tryLock`, fairness, interrupts, and condition queues, but that flexibility does not automatically improve throughput for a simple coarse-grained lock.

The main lesson from milestone 4 is that adding locks around shared mutable state can make a system correct, but it does not necessarily make it scalable.

Coarse-grained locking gives correctness, but it serializes the engine.

More threads do not help much because all threads contend for the same lock.

This motivates the next milestone: a single-writer architecture where one thread owns the order book and commands are submitted through a queue.

Milestone 5:
Single-writer queue engine

Milestone 6:
Ring buffer / low-latency engine

Milestone 7:
Multi-symbol engine

Milestone 8:
Market data / analytics consumer

Milestone 9:
Persistence / replay / recovery

Milestone 10:
Low-latency optimization and profiling