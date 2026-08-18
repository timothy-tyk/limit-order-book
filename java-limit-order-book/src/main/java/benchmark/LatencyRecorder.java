package benchmark;

import java.util.Arrays;

public final class LatencyRecorder {
    private final long[] samples;
    private int count;

    public LatencyRecorder(int capacity) {
        this.samples = new long[capacity];
    }

    public void record(long latencyNanos) {
        if (count < samples.length) {
            samples[count++] = latencyNanos;
        }
    }

    public long count() {
        return count;
    }

    public double averageNanos() {
        if (count == 0) {
            return 0.0;
        }

        long total = 0;

        for (int i = 0; i < count; i++) {
            total += samples[i];
        }

        return (double) total / count;
    }

    public long percentile(double percentile) {
        if (count == 0) {
            return 0;
        }

        long[] copy = Arrays.copyOf(samples, count);
        Arrays.sort(copy);

        int index = (int) Math.ceil(percentile / 100.0 * count) - 1;
        index = Math.max(0, index);
        index = Math.min(count - 1, index);

        return copy[index];
    }

    public long max() {
        if (count == 0) {
            return 0;
        }

        long max = Long.MIN_VALUE;

        for (int i = 0; i < count; i++) {
            if (samples[i] > max) {
                max = samples[i];
            }
        }

        return max;
    }

    /**
     * p50
     * p90
     * p99
     * p99.9
     * max
     * average
     */
    public String latencySummary(){
        StringBuilder sb = new StringBuilder();
        sb.append("Latency:"+"\n");
        sb.append("    p50: "+percentile(50)+"ns"+"\n");
        sb.append("    p90: "+percentile(90)+"ns"+"\n");
        sb.append("    p99: "+percentile(99)+"ns"+"\n");
        sb.append("    p99.9: "+percentile(99.9)+"ns"+"\n");
        sb.append("    max: "+max()+"ns"+"\n");
        sb.append("    avg: "+averageNanos()+"ns"+"\n");
        return sb.toString();
    }
}
