package bg.sofia.uni.fmi.mjt.jobmatch.comparators;

import bg.sofia.uni.fmi.mjt.jobmatch.model.match.CandidateSimilarityMatch;

import java.util.Comparator;

public class CandidateSimilarityBySimilarityScoreDescThenByName implements Comparator<CandidateSimilarityMatch> {
    @Override
    public int compare(CandidateSimilarityMatch o1, CandidateSimilarityMatch o2) {
        if (Double.compare(o1.getSimilarityScore(), o2.getSimilarityScore()) == 0) {
            return o1.getSimilarCandidate().getName().compareTo(o2.getSimilarCandidate().getName());
        }
        return Double.compare(o2.getSimilarityScore(), o1.getSimilarityScore());
    }
}
