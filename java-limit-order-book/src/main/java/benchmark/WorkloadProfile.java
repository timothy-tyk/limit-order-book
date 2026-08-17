package benchmark;

public class WorkloadProfile {
    String name;
    long seed;
    long commandCount;
    int addPercent;
    int cancelPercent;
    int modifyPercent;
    int marketPercent;

    public WorkloadProfile(String name, long seed, long commandCount, int addPercent, int cancelPercent, int modifyPercent, int marketPercent) {
        this.name = name;
        this.seed = seed;
        this.commandCount = commandCount;
        this.addPercent = addPercent;
        this.cancelPercent = cancelPercent;
        this.modifyPercent = modifyPercent;
        this.marketPercent = marketPercent;
    }

    public String getName() {
        return name;
    }

    public long getSeed() {
        return seed;
    }

    public long getCommandCount() {
        return commandCount;
    }

    public int getAddPercent() {
        return addPercent;
    }

    public int getCancelPercent() {
        return cancelPercent;
    }

    public int getMarketPercent() {
        return marketPercent;
    }
}


