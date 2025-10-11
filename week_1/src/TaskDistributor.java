import java.util.Arrays;

public class TaskDistributor {
    public static int minDifference(int[] tasks) {
        if (tasks.length == 0) return 0;

        Arrays.sort(tasks);
        int curr = tasks[tasks.length - 1];

        for (int i = tasks.length - 2; i >= 0; i--) {

            curr = Math.abs(curr - tasks[i]);

        }
        return curr;
    }
}
