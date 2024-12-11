package Utils;

import java.util.List;

public class Utils {

    public static byte[] combineByteArrays(List<byte[]> byteArrayList) {
        // Calculate total size of the combined array
        int totalSize = byteArrayList.stream().mapToInt(b -> b.length).sum();

        // Initialize the combined array
        byte[] combined = new byte[totalSize];

        // Copy each array from the list into the combined array
        int position = 0;
        for (byte[] byteArray : byteArrayList) {
            System.arraycopy(byteArray, 0, combined, position, byteArray.length);
            position += byteArray.length;
        }

        return combined;
    }
}
