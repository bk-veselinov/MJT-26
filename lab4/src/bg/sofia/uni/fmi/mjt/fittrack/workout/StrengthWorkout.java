package bg.sofia.uni.fmi.mjt.fittrack.workout;

public final class StrengthWorkout extends AbstractWorkout implements Workout {
    private static final WorkoutType TYPE = WorkoutType.STRENGTH;

    public StrengthWorkout(String name, int duration, int caloriesBurned, int difficulty) {
        super(name, duration, caloriesBurned, difficulty);
    }

    @Override
    public WorkoutType getType() {
        return TYPE;
    }
}
