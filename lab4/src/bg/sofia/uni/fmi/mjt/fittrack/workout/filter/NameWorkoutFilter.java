package bg.sofia.uni.fmi.mjt.fittrack.workout.filter;

import bg.sofia.uni.fmi.mjt.fittrack.workout.Workout;

public class NameWorkoutFilter implements WorkoutFilter {
    String keyword;
    boolean caseSensitive;

    public NameWorkoutFilter(String keyword, boolean caseSensitive) {
        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException("keyword cannot be null or blank");
        }
        this.caseSensitive = caseSensitive;

        if (!caseSensitive) {
            this.keyword = keyword.toLowerCase();
        } else {
            this.keyword = keyword;
        }
    }

    @Override
    public boolean matches(Workout workout) {
        String workOutName = workout.getName();

        if (!caseSensitive) {
            return workOutName.toLowerCase().contains(keyword);
        }
        return workOutName.contains(keyword);
    }
}
