package bg.sofia.uni.fmi.mjt.fittrack.workout;

public final class CardioWorkout extends AbstractWorkout implements Workout {
    private static final WorkoutType TYPE = WorkoutType.CARDIO;

    public CardioWorkout(String name, int duration, int caloriesBurned, int difficulty) {
        super(name, duration, caloriesBurned, difficulty);
    }

    @Override
    public WorkoutType getType() {
        return TYPE;
    }
}
