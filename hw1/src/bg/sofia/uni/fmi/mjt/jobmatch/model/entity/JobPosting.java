package bg.sofia.uni.fmi.mjt.jobmatch.model.entity;

import java.util.Objects;
import java.util.Set;

public class JobPosting {

    private final String id;
    private final String title;
    private final String employerEmail;
    private final Set<Skill> requiredSkills;
    private final Education requiredEducation;
    private final int requiredYearsOfExperience;
    private final double salary;

    public JobPosting(String id, String title, String employerEmail, Set<Skill> requiredSkills,
                      Education requiredEducation, int requiredYearsOfExperience, double salary) {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        } else if (id.isBlank()) {
            throw new IllegalArgumentException("id cannot be blank");
        }

        if (title == null) {
            throw new IllegalArgumentException("title cannot be null");
        } else if (title.isBlank()) {
            throw new IllegalArgumentException("title cannot be blank");
        }

        if (employerEmail == null) {
            throw new IllegalArgumentException("employerEmail cannot be null");
        } else if (employerEmail.isBlank()) {
            throw new IllegalArgumentException("employerEmail cannot be blank");
        }

        if (requiredSkills == null) {
            throw new IllegalArgumentException("requiredSkills set cannot be null");
        } else if (requiredSkills.isEmpty()) {
            throw new IllegalArgumentException("requiredSkills set cannot be empty");
        }

        if (requiredYearsOfExperience < 0) {
            throw new IllegalArgumentException("requiredYearsOfExperience must be a positive number");
        }

        if (Double.compare(salary, 0.0) < 0) {
            throw new IllegalArgumentException("salary must be a positive number");
        }

        this.id = id;
        this.title = title;
        this.employerEmail = employerEmail;
        this.requiredEducation = requiredEducation;
        this.requiredSkills = requiredSkills;
        this.requiredYearsOfExperience = requiredYearsOfExperience;
        this.salary = salary;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getEmployerEmail() {
        return employerEmail;
    }

    public Set<Skill> getRequiredSkills() {
        return requiredSkills;
    }

    public Education getRequiredEducation() {
        return requiredEducation;
    }

    public int getRequiredYearsOfExperience() {
        return requiredYearsOfExperience;
    }

    public double getSalary() {
        return salary;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        JobPosting that = (JobPosting) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
