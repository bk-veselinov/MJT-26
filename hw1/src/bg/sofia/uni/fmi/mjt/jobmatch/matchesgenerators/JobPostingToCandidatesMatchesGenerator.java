package bg.sofia.uni.fmi.mjt.jobmatch.matchesgenerators;

import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Candidate;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.JobPosting;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Skill;
import bg.sofia.uni.fmi.mjt.jobmatch.model.match.CandidateJobMatch;

import java.util.Collection;
import java.util.Set;

public class JobPostingToCandidatesMatchesGenerator extends
        MatchesGenerator<JobPosting, Candidate, CandidateJobMatch> {

    public JobPostingToCandidatesMatchesGenerator(JobPosting staticObject,
                                                  Collection<Candidate> collectionObjects,
                                                  int limit) {
        super(staticObject, collectionObjects, limit);
    }

    @Override
    protected CandidateJobMatch createTObject(JobPosting staticObject,
                                              Candidate collectionObject,
                                              double similarityScore) {
        return new CandidateJobMatch(collectionObject, staticObject, similarityScore);
    }

    @Override
    protected Set<Skill> getSObjectSkills(JobPosting staticObject) {
        return Set.copyOf(staticObject.getRequiredSkills());
    }

    @Override
    protected Set<Skill> getCObjectSkills(Candidate collectionObject) {
        return Set.copyOf(collectionObject.getSkills());
    }
}
