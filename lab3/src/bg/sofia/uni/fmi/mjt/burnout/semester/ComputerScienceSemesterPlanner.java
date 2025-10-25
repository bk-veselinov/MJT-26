package bg.sofia.uni.fmi.mjt.burnout.semester;

import bg.sofia.uni.fmi.mjt.burnout.exception.CryToStudentsDepartmentException;
import bg.sofia.uni.fmi.mjt.burnout.exception.InvalidSubjectRequirementsException;
import bg.sofia.uni.fmi.mjt.burnout.plan.SemesterPlan;
import bg.sofia.uni.fmi.mjt.burnout.subject.UniversitySubject;

//ComputerScienceSemesterPlanner, където стратегията
// при записването на предметите ще е
//да се запишат предметите с по-добри отзиви от други състуденти,
//без да се гледат нужния брой предмети по категорията им.

public final class ComputerScienceSemesterPlanner extends AbstractSemesterPlanner {

    private static UniversitySubject[] sortSubjects(UniversitySubject[] subjects) {

        for (int i = 0; i < subjects.length; i++) {
            int maxIdx = i;

            for (int j = i + 1; j < subjects.length; j++) {
                if (subjects[maxIdx].rating() < subjects[j].rating()) {
                    maxIdx = j;
                }
            }

            if (i != maxIdx) {
                UniversitySubject temp = subjects[i];
                subjects[i] = subjects[maxIdx];
                subjects[maxIdx] = temp;
            }
        }
        return subjects;
    }

    private static UniversitySubject[] getEnrolledSubjects(SemesterPlan semesterPlan) {

        UniversitySubject[] subjects = sortSubjects(semesterPlan.subjects());

        int currCredits = 0;
        int i = 0;
        UniversitySubject[] enrolledSubjectsTemp = new UniversitySubject[subjects.length];

        while (currCredits < semesterPlan.minimalAmountOfCredits()) {

            if (i >= subjects.length) {
                throw new CryToStudentsDepartmentException("The subjects' credits are not enough to cover the minimum credits for the semester");
            }

            currCredits += subjects[i].credits();
            enrolledSubjectsTemp[i] = subjects[i++];
        }

        UniversitySubject[] enrolledSubjects = new UniversitySubject[i];
        System.arraycopy(enrolledSubjectsTemp, 0, enrolledSubjects, 0, i);

        return enrolledSubjects;
    }

    @Override
    public UniversitySubject[] calculateSubjectList(SemesterPlan semesterPlan) throws InvalidSubjectRequirementsException {
        if (semesterPlan == null) {
            throw new IllegalArgumentException("Semester plan cannot be null");
        }

        if (hasDuplicateSubjectRequirementCategories(semesterPlan)) {
            throw new InvalidSubjectRequirementsException("You cannot have duplicate categories in the subjectRequirements");
        }

        UniversitySubject[] enrolledSubjects = getEnrolledSubjects(semesterPlan);

        return enrolledSubjects;
    }
}
