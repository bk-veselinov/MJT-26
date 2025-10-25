package bg.sofia.uni.fmi.mjt.burnout.semester;

import bg.sofia.uni.fmi.mjt.burnout.exception.DisappointmentException;
import bg.sofia.uni.fmi.mjt.burnout.plan.SemesterPlan;
import bg.sofia.uni.fmi.mjt.burnout.subject.Category;
import bg.sofia.uni.fmi.mjt.burnout.subject.SubjectRequirement;
import bg.sofia.uni.fmi.mjt.burnout.subject.UniversitySubject;

public abstract sealed class AbstractSemesterPlanner implements SemesterPlannerAPI
        permits ComputerScienceSemesterPlanner, SoftwareEngineeringSemesterPlanner {

    protected static boolean hasDuplicateSubjectRequirementCategories(SemesterPlan semesterPlan) {
        SubjectRequirement[] requirements = semesterPlan.subjectRequirements();

        boolean[] categoriesTracker = new boolean[4];

        for (SubjectRequirement requirement : requirements) {
            Category category = requirement.category();

            if (categoriesTracker[category.ordinal()]) {
                return true;
            }
            categoriesTracker[category.ordinal()] = true;
        }

        return false;
    }

    @Override
    public int calculateJarCount(UniversitySubject[] subjects, int maximumSlackTime, int semesterDuration) {
        if (subjects == null || subjects.length == 0) {
            throw new IllegalArgumentException("subjects cannot be null and must have len > 0");
        }

        if (maximumSlackTime <= 0 || semesterDuration <= 0) {
            throw new IllegalArgumentException("maximumSlackTime and semesterDuration must be > 0");
        }

        int totalSlackTime = 0;
        int totalStudyTime = 0;
        int NEEDED_STUDY_TIME_PER_JAR = 5;

        for (UniversitySubject currSubject : subjects) {
            totalStudyTime += currSubject.neededStudyTime();
            totalSlackTime += (int) Math.ceil(currSubject.neededStudyTime() * currSubject.category().getCoefficient());

        }

        if (totalSlackTime > maximumSlackTime) {
            throw new DisappointmentException("Total slack time for all the subjects must not exceed maximumSlackTime. Grandma is not happy");
        }

        int jars = totalStudyTime / NEEDED_STUDY_TIME_PER_JAR;

        if (totalSlackTime + totalStudyTime > semesterDuration) {
            jars *= 2;
        }

        return jars;
    }

}
