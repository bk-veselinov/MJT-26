package bg.sofia.uni.fmi.mjt.jobmatch.matchesgenerators;

import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Candidate;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.JobPosting;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Skill;
import bg.sofia.uni.fmi.mjt.jobmatch.model.match.CandidateJobMatch;

import java.util.Collection;
import java.util.Set;

public class CandidateToJobPostingsMatchesGenerator
        extends MatchesGenerator<Candidate, JobPosting, CandidateJobMatch> {

    public CandidateToJobPostingsMatchesGenerator(Candidate staticObject,
                                                  Collection<JobPosting> collectionObjects,
                                                  int limit) {
        super(staticObject, collectionObjects, limit);
    }

    @Override
    protected CandidateJobMatch createTObject(Candidate staticObject,
                                              JobPosting collectionObject,
                                              double similarityScore) {
        return new CandidateJobMatch(staticObject, collectionObject, similarityScore);
    }

    @Override
    protected Set<Skill> getSObjectSkills(Candidate staticObject) {
        return Set.copyOf(staticObject.getSkills());
    }

    @Override
    protected Set<Skill> getCObjectSkills(JobPosting collectionObject) {
        return Set.copyOf(collectionObject.getRequiredSkills());
    }
}
