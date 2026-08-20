package workload;

public interface ConcurrentWorkloadGenerator {
    void generate(int threadId,int threadCount,boolean measuredRun);
    long getLastProcessedSequence();
}
