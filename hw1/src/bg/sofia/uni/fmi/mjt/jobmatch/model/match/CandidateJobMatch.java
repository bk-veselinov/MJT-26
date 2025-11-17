package bg.sofia.uni.fmi.mjt.jobmatch.model.match;

import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Candidate;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.JobPosting;

import java.util.Objects;

public class CandidateJobMatch {

    private static final double MIN_SIMILARITY_SCORE = 0;
    private static final double MAX_SIMILARITY_SCORE = 1;

    private final Candidate candidate;
    private final JobPosting jobPosting;
    private final double similarityScore;

    public CandidateJobMatch(Candidate candidate, JobPosting jobPosting, double similarityScore) {
//        if (candidate == null) {
//            throw new IllegalArgumentException("candidate cannot be null");
//        }
//
//        if (jobPosting == null) {
//            throw new IllegalArgumentException("jobPosting cannot be null");
//        }

        if (Double.compare(similarityScore, MIN_SIMILARITY_SCORE) < 0
                || Double.compare(similarityScore, MAX_SIMILARITY_SCORE) > 0) {
            throw new IllegalArgumentException("similarityScore must be in the interval [0, 1]");
        }

        this.candidate = candidate;
        this.jobPosting = jobPosting;
        this.similarityScore = similarityScore;
    }

    public JobPosting getJobPosting() {
        return jobPosting;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public double getSimilarityScore() {
        return similarityScore;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CandidateJobMatch that = (CandidateJobMatch) o;
        return Objects.equals(candidate, that.candidate) && Objects.equals(jobPosting, that.jobPosting);
    }

    @Override
    public int hashCode() {
        return Objects.hash(candidate, jobPosting);
    }
    
}
