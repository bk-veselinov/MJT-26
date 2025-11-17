package bg.sofia.uni.fmi.mjt.jobmatch.matching;

import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Skill;

import java.util.HashSet;
import java.util.Set;

public class JaccardSimilarity implements SimilarityStrategy {

    @Override
    public double calculateSimilarity(Set<Skill> candidateSkills, Set<Skill> jobSkills) {
        if (candidateSkills == null) {
            throw new IllegalArgumentException("candidateSkills is null");
        }

        if (jobSkills == null) {
            throw new IllegalArgumentException("jobSkills is null");
        }

        Set<Skill> skillsUnionSet = new HashSet<>(candidateSkills);
        skillsUnionSet.addAll(jobSkills);

        Set<Skill> skillsIntersecionSet = new HashSet<>(candidateSkills);
        skillsIntersecionSet.retainAll(jobSkills);

        if (skillsUnionSet.isEmpty()) {
            return 0;
        }

        return (double) skillsIntersecionSet.size() / skillsUnionSet.size();
    }
}
