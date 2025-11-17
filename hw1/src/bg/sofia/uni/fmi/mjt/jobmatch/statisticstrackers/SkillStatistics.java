package bg.sofia.uni.fmi.mjt.jobmatch.statisticstrackers;

import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Candidate;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.JobPosting;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Skill;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class SkillStatistics {

    private final HashMap<Skill, Integer> skillsAppearance = new HashMap<>();
    private final HashMap<Skill, Integer> skillsMaxLevel = new HashMap<>();
    private final Set<Skill> allSkills = new HashSet<>();
    private final MaximumValueTracker<Skill, Integer> mostCommonSkill = new MaximumValueTracker<>();

    public int getAppearanceCount(Skill skill) {
        return skillsAppearance.get(skill);
    }

    public Skill getMaxSeenSkillLevel(Skill skill) {
        return new Skill(skill.name(), skillsMaxLevel.get(skill));
    }

    public Set<Skill> getAllSkills() {
        return new HashSet<>(allSkills);
    }

    public Skill getMostCommonSkill() {
        return mostCommonSkill.getKey();
    }

    private void updateMostCommonSkill(Skill skill, int count) {
        mostCommonSkill.update(skill, count);
    }

    private void updateSkillCount(Skill skill) {
        skillsAppearance.putIfAbsent(skill, 0);

        int currentSkillCount = skillsAppearance.get(skill) + 1;
        skillsAppearance.put(skill, currentSkillCount);

        updateMostCommonSkill(skill, currentSkillCount);
    }

    private void updateHighestSkillLevel(Skill skill) {
        skillsMaxLevel.putIfAbsent(skill, skill.level());
        int currentMaxLevel = Math.max(skill.level(), skillsMaxLevel.get(skill));
        skillsMaxLevel.put(skill, currentMaxLevel);
    }

    public void updateSkillStatistics(JobPosting jobPosting) {

        for (Skill requiredSkill : jobPosting.getRequiredSkills()) {
            updateHighestSkillLevel(requiredSkill);
            allSkills.add(requiredSkill);
        }
    }

    public void updateSkillStatistics(Candidate candidate) {

        for (Skill skill : candidate.getSkills()) {
            updateSkillCount(skill);
            allSkills.add(skill);
        }
    }

}
