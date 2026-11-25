

package service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
//basmah
public class FileStorageService {

    // Save a list of objects to a local file.
    public static <T> void saveList(
            String fileName, List<T> list) throws IOException {

        try (ObjectOutputStream output =
                new ObjectOutputStream(
                        new FileOutputStream(fileName))) {

            output.writeObject(new ArrayList<>(list));
        }
    }

    // Load a list of objects from a local file.
    public static <T> List<T> loadList(
            String fileName, Class<T> type)
            throws IOException, ClassNotFoundException {

        File file = new File(fileName);

        // Return an empty list if the file does not exist.
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream input =
                new ObjectInputStream(
                        new FileInputStream(file))) {

            Object data = input.readObject();

            // Check that the stored object is a list.
            if (!(data instanceof List<?>)) {
                return new ArrayList<>();
            }

            List<T> result = new ArrayList<>();

            // Validate the type of each stored object.
            for (Object item : (List<?>) data) {

                if (!type.isInstance(item)) {
                    return new ArrayList<>();
                }

                result.add(type.cast(item));
            }

            return result;

        } catch (ObjectStreamException | EOFException
                | ClassNotFoundException e) {

            // Handle corrupted or incompatible files.
            System.err.println(
                    "Unable to read file: " + fileName
                    + " - " + e.getMessage());

            return new ArrayList<>();
        }
    }
}
