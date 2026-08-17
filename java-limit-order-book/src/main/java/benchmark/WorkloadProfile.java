package benchmark;

public record WorkloadProfile(
        String name,
        long seed,
        long commandCount,
        int addPercent,
        int cancelPercent,
        int modifyPercent,
        int marketPecent
    ){};
