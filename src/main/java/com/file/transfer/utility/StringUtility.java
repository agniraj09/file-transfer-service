package com.file.transfer.utility;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class StringUtility {

    public static String streamToString(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }
}
