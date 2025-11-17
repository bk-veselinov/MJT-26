package bg.sofia.uni.fmi.mjt.jobmatch.comparators;

import bg.sofia.uni.fmi.mjt.jobmatch.model.match.CandidateJobMatch;

import java.util.Comparator;

public class CandidateJobMatchBySimilarityScoreDescThenByNameComparator implements Comparator<CandidateJobMatch> {

    @Override
    public int compare(CandidateJobMatch o1, CandidateJobMatch o2) {
        if (Double.compare(o1.getSimilarityScore(), o2.getSimilarityScore()) == 0) {
            return o1.getCandidate().getName().compareTo(o2.getCandidate().getName());
        }
        return Double.compare(o2.getSimilarityScore(), o1.getSimilarityScore());
    }
}
