package bg.sofia.uni.fmi.mjt.jobmatch.matchesgenerators;

import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Candidate;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Skill;
import bg.sofia.uni.fmi.mjt.jobmatch.model.match.CandidateSimilarityMatch;

import java.util.Collection;
import java.util.Set;

public class CandidateToCandidatesMatchesGenerator extends
        MatchesGenerator<Candidate, Candidate, CandidateSimilarityMatch> {

    public CandidateToCandidatesMatchesGenerator(Candidate staticObject,
                                                 Collection<Candidate> collectionObjects,
                                                 int limit) {
        super(staticObject, collectionObjects, limit);
    }

    @Override
    protected CandidateSimilarityMatch createTObject(Candidate staticObject,
                                                     Candidate collectionObject,
                                                     double similarityScore) {
        return new CandidateSimilarityMatch(staticObject, collectionObject, similarityScore);
    }

    @Override
    protected Set<Skill> getSObjectSkills(Candidate staticObject) {
        return Set.copyOf(staticObject.getSkills());
    }

    @Override
    protected Set<Skill> getCObjectSkills(Candidate collectionObject) {
        return Set.copyOf(collectionObject.getSkills());
    }
}
