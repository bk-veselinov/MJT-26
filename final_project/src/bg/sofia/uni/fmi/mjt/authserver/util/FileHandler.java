package bg.sofia.uni.fmi.mjt.authserver.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;

public class FileHandler {

    private final String fileName;

    public FileHandler(String fileName) {
        this.fileName = fileName;
    }

    public String loadData() {
        try (Reader reader = new FileReader(fileName)) {
            return reader.readAllAsString();

        } catch (FileNotFoundException e) {
            return "[]";

        } catch (IOException e) {
            throw new UncheckedIOException("Problem while reading from the DB. Please contact support.", e);

        }

    }

    public synchronized void writeData(String data, Boolean appendMode) {
        try (Writer writer = new FileWriter(fileName, appendMode)) {
            writer.write(data);
            writer.flush();

        } catch (IOException e) {
            throw new UncheckedIOException("Problem while writing into the DB. Please contact support.", e);

        }

    }
}
