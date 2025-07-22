package org.highfive.backend.global.util;

public class VectorUtil {
    public static String convertUserVector(String vector) {
        if (vector != null && vector.length() > 1 &&
                vector.startsWith("\"") && vector.endsWith("\"")) {
            return vector.substring(1, vector.length() - 1);
        }
        return vector;
    }
}
