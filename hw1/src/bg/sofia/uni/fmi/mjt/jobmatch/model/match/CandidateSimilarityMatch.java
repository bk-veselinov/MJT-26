package bg.sofia.uni.fmi.mjt.jobmatch.model.match;

import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Candidate;

import java.util.Objects;

public class CandidateSimilarityMatch {

    private static final double MIN_SIMILARITY_SCORE = 0;
    private static final double MAX_SIMILARITY_SCORE = 1;

    private final Candidate targetCandidate;
    private final Candidate similarCandidate;
    private final double similarityScore;

    public CandidateSimilarityMatch(Candidate targetCandidate, Candidate similarCandidate, double similarityScore) {

        if (Double.compare(similarityScore, MIN_SIMILARITY_SCORE) < 0
                || Double.compare(similarityScore, MAX_SIMILARITY_SCORE) > 0) {
            throw new IllegalArgumentException("similarityScore must be in the interval [0, 1]");
        }

        this.targetCandidate = targetCandidate;
        this.similarCandidate = similarCandidate;
        this.similarityScore = similarityScore;
    }

    public Candidate getTargetCandidate() {
        return targetCandidate;
    }

    public Candidate getSimilarCandidate() {
        return similarCandidate;
    }

    public double getSimilarityScore() {
        return similarityScore;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CandidateSimilarityMatch that = (CandidateSimilarityMatch) o;
        return Objects.equals(targetCandidate, that.targetCandidate)
                && Objects.equals(similarCandidate, that.similarCandidate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetCandidate, similarCandidate);
    }

}
