package com.andreistraut.drp.local.server;

/**
 * Main entry point. This class initializes the parameters for the local HTTP server, and starts it
 */
public final class LocalHttpServer {

    private static final int DEFAULT_LISTEN_PORT = 8089;

    public static void main(String[] args) throws InterruptedException, IllegalArgumentException, NumberFormatException {
        int port = DEFAULT_LISTEN_PORT;

        // Get the port, if specified, and correct
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);

            if (port <= 0) {
                throw new IllegalArgumentException("Port number must be an integer larger than 0");
            }
        }

        LocalHttpServerInitializer initializer = new LocalHttpServerInitializer();

        new LocalHttpServerRunner(port, initializer).run();
    }
}
