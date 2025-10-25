package bg.sofia.uni.fmi.mjt.burnout.semester;

import bg.sofia.uni.fmi.mjt.burnout.exception.CryToStudentsDepartmentException;
import bg.sofia.uni.fmi.mjt.burnout.exception.InvalidSubjectRequirementsException;
import bg.sofia.uni.fmi.mjt.burnout.plan.SemesterPlan;
import bg.sofia.uni.fmi.mjt.burnout.subject.Category;
import bg.sofia.uni.fmi.mjt.burnout.subject.SubjectRequirement;
import bg.sofia.uni.fmi.mjt.burnout.subject.UniversitySubject;

public final class SoftwareEngineeringSemesterPlanner extends AbstractSemesterPlanner {

    private static UniversitySubject[] sortSubjects(UniversitySubject[] subjects) {
        UniversitySubject[] sorted = new UniversitySubject[subjects.length];
        System.arraycopy(subjects, 0, sorted, 0, subjects.length);

        for (int i = 0; i < sorted.length; i++) {
            int maxIdx = i;

            for (int j = i + 1; j < sorted.length; j++) {
                if (sorted[maxIdx].credits() < sorted[j].credits()) {
                    maxIdx = j;
                }
            }

            if (i != maxIdx) {
                UniversitySubject temp = sorted[i];
                sorted[i] = sorted[maxIdx];
                sorted[maxIdx] = temp;
            }
        }
        return sorted;
    }

    private static int[] getMinSubjectsForCategory(SemesterPlan semesterPlan) {
        int[] minSubjectForCategory = new int[4];

        SubjectRequirement[] requirements = semesterPlan.subjectRequirements();

        for (SubjectRequirement currRequirement : requirements) {
            Category category = currRequirement.category();
            minSubjectForCategory[category.ordinal()] = currRequirement.minAmountEnrolled();
        }

        return minSubjectForCategory;
    }

    private static boolean hasFulfilledCategoryRequirements(int[] minSubjectForCategory) {
        for (int currCount : minSubjectForCategory) {
            if (currCount > 0) {
                return false;
            }
        }
        return true;
    }

    private static void validateRequirementsCanBeMet(UniversitySubject[] subjects, int[] minSubjectForCategory) {
        int[] availableSubjects = new int[4];

        for (UniversitySubject subject : subjects) {
            availableSubjects[subject.category().ordinal()]++;
        }

        for (int i = 0; i < 4; i++) {
            if (availableSubjects[i] < minSubjectForCategory[i]) {
                throw new CryToStudentsDepartmentException(
                        "Not enough subjects available to meet category requirements"
                );
            }
        }
    }

    private static UniversitySubject[] getEnrolledSubjects(SemesterPlan semesterPlan) {
        UniversitySubject[] subjects = sortSubjects(semesterPlan.subjects());
        int[] minSubjectForCategory = getMinSubjectsForCategory(semesterPlan);

        validateRequirementsCanBeMet(subjects, minSubjectForCategory);

        int currCredits = 0;
        int enrolledCount = 0;
        int i = 0;
        UniversitySubject[] enrolledSubjectsTemp = new UniversitySubject[subjects.length];

        while (i < subjects.length && !hasFulfilledCategoryRequirements(minSubjectForCategory)) {
            Category category = subjects[i].category();

            if (minSubjectForCategory[category.ordinal()] > 0) {
                enrolledSubjectsTemp[enrolledCount] = subjects[i];
                currCredits += subjects[i].credits();
                minSubjectForCategory[category.ordinal()] -= 1;
                enrolledCount++;
            }
            i++;
        }

        i = 0;
        while (currCredits < semesterPlan.minimalAmountOfCredits()) {
            if (i >= subjects.length) {
                throw new CryToStudentsDepartmentException(
                        "The subjects' credits are not enough to cover the minimum credits for the semester"
                );
            }

            boolean alreadyEnrolled = false;
            for (int j = 0; j < enrolledCount; j++) {
                if (enrolledSubjectsTemp[j] == subjects[i]) {
                    alreadyEnrolled = true;
                    break;
                }
            }

            if (!alreadyEnrolled) {
                enrolledSubjectsTemp[enrolledCount] = subjects[i];
                currCredits += subjects[i].credits();
                enrolledCount++;
            }
            i++;
        }

        UniversitySubject[] enrolledSubjects = new UniversitySubject[enrolledCount];
        System.arraycopy(enrolledSubjectsTemp, 0, enrolledSubjects, 0, enrolledCount);

        return enrolledSubjects;
    }

    @Override
    public UniversitySubject[] calculateSubjectList(SemesterPlan semesterPlan)
            throws InvalidSubjectRequirementsException {

        if (semesterPlan == null) {
            throw new IllegalArgumentException("Semester plan cannot be null");
        }

        if (hasDuplicateSubjectRequirementCategories(semesterPlan)) {
            throw new InvalidSubjectRequirementsException(
                    "You cannot have duplicate categories in the subjectRequirements"
            );
        }

        return getEnrolledSubjects(semesterPlan);
    }
}