package engine.disruptor;

public class RetryMetrics {
    public long totalRetries;
    public long maxRetries;

    public RetryMetrics(long totalRetries, long maxRetries) {
        this.totalRetries = totalRetries;
        this.maxRetries = maxRetries;
    }
}
