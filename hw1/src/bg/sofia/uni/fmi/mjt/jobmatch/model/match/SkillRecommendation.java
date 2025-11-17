package bg.sofia.uni.fmi.mjt.jobmatch.model.match;

import java.util.Objects;

public record SkillRecommendation(String skillName, double improvementScore) {

    public SkillRecommendation {
        if (skillName == null) {
            throw new IllegalArgumentException("skillName cannot be null");
        } else if (skillName.isBlank()) {
            throw new IllegalArgumentException("skillName cannot be blank");
        }

        if (Double.compare(improvementScore, 0) < 0) {
            throw new IllegalArgumentException("improvementScore must be >= 0");
        }
    }

    @Override
    public String skillName() {
        return skillName;
    }

    @Override
    public double improvementScore() {
        return improvementScore;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SkillRecommendation that = (SkillRecommendation) o;
        return Double.compare(improvementScore, that.improvementScore) == 0
                && Objects.equals(skillName, that.skillName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(skillName, improvementScore);
    }

}
