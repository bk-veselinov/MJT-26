package bg.sofia.uni.fmi.mjt.space.rocket;

import java.util.Objects;
import java.util.Optional;

public record Rocket(String id, String name, Optional<String> wiki, Optional<Double> height) {
    private static final int ID_IDX = 0;
    private static final int NAME_IDX = 1;
    private static final int WIKI_IDX = 2;
    private static final int HEIGHT_IDX = 3;
    private static final int HEIGHT_DIGIT_POSITION = 0;

    public static Rocket of(String stringData) {
        stringData += "d";
        stringData = stringData.replace(",,", ",_,");
        String[] splitLine = stringData.split("(,\")|(\",)|(,\\b(?!\\d+\\)))");

        String id = splitLine[ID_IDX].strip();
        String name = splitLine[NAME_IDX].strip();

        Optional<String> wiki = splitLine[WIKI_IDX].strip().equals("_") ?
                Optional.empty() : Optional.of(splitLine[WIKI_IDX].strip());

        Optional<Double> height;
        double heightNumber;

        try {
            heightNumber = Double.parseDouble(splitLine[HEIGHT_IDX]
                    .split(" ")[HEIGHT_DIGIT_POSITION].strip());
            height = Optional.of(heightNumber);
        } catch (NumberFormatException e) {
            height = Optional.empty();
        }

        return new Rocket(id, name, wiki, height);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Rocket rocket = (Rocket) o;
        return Objects.equals(id, rocket.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}