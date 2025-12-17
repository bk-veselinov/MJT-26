package bg.sofia.uni.fmi.mjt.space.loader;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.List;
import java.util.function.Function;

public class Loader {

    public static <T> List<T> loadObjFromCSV(Reader reader, Function<String, T> producer) {

        if (reader == null) {
            throw new IllegalArgumentException("The provided Reader is null");
        }

        if (producer == null) {
            throw new IllegalArgumentException("The producer function is null");
        }

        BufferedReader bufferedReader = new BufferedReader(reader);

        return bufferedReader.lines().skip(1).map(producer).toList();
    }
}
