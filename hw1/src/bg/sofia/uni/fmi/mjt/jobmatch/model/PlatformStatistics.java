package bg.sofia.uni.fmi.mjt.jobmatch.model;

public record PlatformStatistics(int totalCandidates, int totalEmployers,
                                 int totalJobPostings, String mostCommonSkillName,
                                 String highestPaidJobTitle) {
    public PlatformStatistics {
        if (totalCandidates < 0) {
            throw new IllegalArgumentException("totalCandidates must be >= 0");
        }

        if (totalEmployers < 0) {
            throw new IllegalArgumentException("totalEmployers must be >= 0");
        }

        if (totalJobPostings < 0) {
            throw new IllegalArgumentException("totalJobPostings must be >= 0");
        }
    }

}
