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

├── docs
│   ├── architecture.md
│   ├── benchmark-methodology.md
│   └── design-decisions.md
├── java-limit-order-book
│   ├── pom.xml
│   └── src
│       ├── main
│       │   ├── java
│       │   │   ├── Application.java
│       │   │   ├── benchmark
│       │   │   │   ├── BenchmarkRunner.java
│       │   │   │   └── LatencyRecorder.java
│       │   │   ├── command
│       │   │   │   ├── AddLimitOrderCommand.java
│       │   │   │   ├── CancelOrderCommand.java
│       │   │   │   ├── Command.java
│       │   │   │   ├── MarketOrderCommand.java
│       │   │   │   └── ModifyOrderCommand.java
│       │   │   ├── concurrency
│       │   │   ├── core
│       │   │   │   ├── Order.java
│       │   │   │   ├── OrderBook.java
│       │   │   │   ├── PriceLevel.java
│       │   │   │   └── Side.java
│       │   │   ├── engine
│       │   │   │   ├── MatchingEngine.java
│       │   │   │   └── SingleThreadedMatchingEngine.java
│       │   │   ├── event
│       │   │   │   ├── Event.java
│       │   │   │   ├── OrderAccepted.java
│       │   │   │   ├── OrderCancelled.java
│       │   │   │   └── Trade.java
│       │   │   ├── validation
│       │   │   │   ├── EventRecorder.java
│       │   │   │   └── InvariantChecker.java
│       │   │   └── workload
│       │   │       ├── RandomWorkloadGenerator.java
│       │   │       └── WorkloadGenerator.java
│       │   └── resources
│       └── test
│           └── java
│               ├── core
│               │   ├── OrderBookTest.java
│               │   └── PriceLevelTest.java
│               ├── engine
│               │   └── SingleThreadedMatchingEngineTest.java
│               └── workload
│                   └── RandomWorkloadGeneratorTest.java
└── README.md

```