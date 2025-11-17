package bg.sofia.uni.fmi.mjt.jobmatch.statisticstrackers;

public class MaximumValueTracker<K, V extends Number> {

    private K key;
    private V value;

    public void update(K key, V value) {
        if (this.key == null || this.value == null) {
            this.key = key;
            this.value = value;
        } else if (this.value.doubleValue() < value.doubleValue()) {
            this.key = key;
            this.value = value;
        }
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }
}
