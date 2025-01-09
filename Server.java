import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Server {
    private static final Logger logger = Logger.getLogger(Server.class.getName());

    public static void main(String[] args) {
        int port = 8080;
        BlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<>(Constants.QUEUE_SIZE);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(Constants.CORE_POOL_SIZE, Constants.MAX_POOL_SIZE,
                Constants.KEEP_ALIVE_TIME, TimeUnit.SECONDS, blockingQueue);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info(String.format("Listening to requests from port %d", port));

            while (true) {
                Socket clientSocket = serverSocket.accept();
                logger.info(String.format("Client InetAddress: %s", clientSocket.getInetAddress()));

                executor.submit(() -> handleRequest(clientSocket));
            }
        } catch (IOException e) {
            logger.severe(String.format("Encountered an exception %s, could not start the server", e.toString()));
        }
    }

    private static void handleRequest(Socket clientSocket) {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(clientSocket.getOutputStream(),
                        StandardCharsets.UTF_8)) {

            String line = bufferedReader.readLine();
            String request = line;
            while (line != null && !line.isEmpty()) {
                line = bufferedReader.readLine();
                request = request + line;
            }
            logger.info(String.format("Request: %s", request));

            String response = "HTTP/1.1 200 OK\n" +
                    "Content-Type: application/json\n" +
                    "\n" +
                    "{\"message\": \"Hello World\"}";
            outputStreamWriter.write(response);

        } catch (IOException e) {
            logger.severe(String.format("Unable to complete request: %s", e.getMessage()));
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                logger.severe(String.format("Exception while closing socket: %s", e));
            }
        }
    }

    private final class Constants {
        public static int CORE_POOL_SIZE = 10;
        public static int MAX_POOL_SIZE = 10;
        public static long KEEP_ALIVE_TIME = 300;
        public static int QUEUE_SIZE = 10;
    }
}
