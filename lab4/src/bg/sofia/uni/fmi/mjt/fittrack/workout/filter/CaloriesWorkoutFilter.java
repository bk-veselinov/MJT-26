package bg.sofia.uni.fmi.mjt.fittrack.workout.filter;

import bg.sofia.uni.fmi.mjt.fittrack.workout.Workout;

public class CaloriesWorkoutFilter implements WorkoutFilter {
    int min;
    int max;

    public CaloriesWorkoutFilter(int min, int max) {
        if (min < 0 || max < 0) {
            throw new IllegalArgumentException("min and max cannot be negative numbers");
        }
        if (min > max) {
            throw new IllegalArgumentException("max must be >= than min");
        }

        this.min = min;
        this.max = max;
    }

    @Override
    public boolean matches(Workout workout) {
        int workoutCaloriesBurned = workout.getCaloriesBurned();
        return min <= workoutCaloriesBurned && workoutCaloriesBurned <= max;
    }
}
