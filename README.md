# limit-order-book
A limit order book program written in Java

limit-order-book/
README.md
docs/
architecture.md
benchmark-methodology.md
design-decisions.md
src/main/java/
    core/
        Order.java
        Side.java
        OrderBook.java
        PriceLevel.java
    engine/
        MatchingEngine.java
        Command.java
        Event.java
    concurrency/
        LockedOrderBook.java
        SingleWriterOrderBook.java
        RingBufferOrderBook.java
    benchmark/
        WorkloadGenerator.java
        LatencyRecorder.java
        BenchmarkRunner.java
tests/
InvariantChecker.java
RandomWorkload.java


