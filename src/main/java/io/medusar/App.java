package io.medusar;

import java.io.IOException;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException, InterruptedException {
        new GrpcServer(6666).startAndWait();
    }
}
