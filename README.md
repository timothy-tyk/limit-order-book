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
        command.java
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



src/
    main/
        java/com/yourname/lob/
            Application.java
            config/
                Config.java
                Mode.java
            core/
                Order.java
                OrderBook.java
                PriceLevel.java
                Side.java
            engine/
                MatchingEngine.java
                SingleThreadedMatchingEngine.java
                SingleWriterMatchingEngine.java
            command/
                Command.java
                AddLimitOrderCommand.java
                CancelOrderCommand.java
                ModifyOrderCommand.java
                MarketOrderCommand.java
            event/
                Event.java
                OrderAccepted.java
                OrderCancelled.java
                Trade.java
            workload/
                WorkloadGenerator.java
                RandomWorkloadGenerator.java
            validation/
                InvariantChecker.java
                EventRecorder.java
            benchmark/
                BenchmarkRunner.java
                LatencyRecorder.java
test/
java/
com/
yourname/
lob/
OrderBookTest.java
MatchingEngineTest.java
RandomWorkloadInvariantTest.java


