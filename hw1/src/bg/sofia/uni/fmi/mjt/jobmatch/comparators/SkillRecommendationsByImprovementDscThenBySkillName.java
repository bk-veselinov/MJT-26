package bg.sofia.uni.fmi.mjt.jobmatch.comparators;

import bg.sofia.uni.fmi.mjt.jobmatch.model.match.SkillRecommendation;

import java.util.Comparator;

public class SkillRecommendationsByImprovementDscThenBySkillName implements Comparator<SkillRecommendation> {
    @Override
    public int compare(SkillRecommendation o1, SkillRecommendation o2) {
        if (Double.compare(o1.improvementScore(), o2.improvementScore()) == 0) {
            return o1.skillName().compareTo(o2.skillName());
        }
        return Double.compare(o2.improvementScore(), o1.improvementScore());
    }
}
