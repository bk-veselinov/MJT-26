public static int minDifference(int[] tasks){
    if (tasks.length == 0) return 0;

    Arrays.sort(tasks);
    int curr = tasks[tasks.length-1];

    for (int i=tasks.length-2; i>=0; i--){

        curr = Math.abs(curr - tasks[i]);

    }
    return curr;
}



void main() {
    int[] res = {
        minDifference(new int[]{1,2,3,4,5})	,
        minDifference(new int[]{10,20,15,5})	,
        minDifference(new int[]{7,3,2,1,5,4})	,
        minDifference(new int[]{9,1,1,1})	,
        minDifference(new int[]{})	,
        minDifference(new int[]{120}),
        minDifference(new int[]{30, 30})
    };

    IO.println(Arrays.toString(res));

}