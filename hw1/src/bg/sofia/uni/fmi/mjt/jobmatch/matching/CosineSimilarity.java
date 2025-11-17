package bg.sofia.uni.fmi.mjt.jobmatch.matching;

import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Skill;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class CosineSimilarity implements SimilarityStrategy {

    private static Map<String, Skill> convertSkillSetToMap(Set<Skill> skills) {
        Map<String, Skill> skillMap = new HashMap<>();

        for (Skill skill : skills) {
            skillMap.put(skill.name(), skill);
        }
        return skillMap;
    }

    private static int getDotProduct(int[] candidateVector, int[] jobVector) {
        int dotProduct = 0;

        for (int i = 0; i < candidateVector.length; i++) {
            dotProduct += candidateVector[i] * jobVector[i];
        }

        return dotProduct;
    }

    private static double getVectorNorm(int[] vector) {
        int squaredSum = 0;

        for (int j : vector) {
            squaredSum += j * j;
        }

        return Math.sqrt(squaredSum);
    }

    private static double calculateCosineSimilarity(Set<Skill> candidateSkills, Set<Skill> jobSkills) {
        Map<String, Skill> candidateSkillMap = convertSkillSetToMap(candidateSkills);
        Map<String, Skill> jobSkillMap = convertSkillSetToMap(jobSkills);

        Set<Skill> skillsUnionSet = new LinkedHashSet<>(candidateSkills);
        skillsUnionSet.addAll(jobSkills);

        int[] candidateSkillLevels = new int[skillsUnionSet.size()];
        int[] jobSkillLevels = new int[skillsUnionSet.size()];

        int i = 0;
        for (Skill skill : skillsUnionSet) {
            Skill candidateSkill = candidateSkillMap.get(skill.name());
            candidateSkillLevels[i] = candidateSkill == null ? 0 : candidateSkill.level();

            Skill jobSkill = jobSkillMap.get(skill.name());
            jobSkillLevels[i] = jobSkill == null ? 0 : jobSkill.level();

            i++;
        }

        return getDotProduct(candidateSkillLevels, jobSkillLevels)
                / (getVectorNorm(candidateSkillLevels) * getVectorNorm(jobSkillLevels));
    }

    @Override
    public double calculateSimilarity(Set<Skill> candidateSkills, Set<Skill> jobSkills) {
        if (candidateSkills == null) {
            throw new IllegalArgumentException("candidateSkills is null");
        }

        if (jobSkills == null) {
            throw new IllegalArgumentException("jobSkills is null");
        }

        if (candidateSkills.isEmpty() && jobSkills.isEmpty()) {
            return 0;
        }

        return calculateCosineSimilarity(candidateSkills, jobSkills);
    }
}
