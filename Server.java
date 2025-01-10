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
import java.util.HashMap;
import java.util.Map;

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
            
            String[] requestParts = request.split(" ");
            String method = requestParts[0];
            String path = requestParts[1];
            logger.info(String.format("Request: %s %s", method, path));


            String response;
            Map<String, String> body = new HashMap<>();
            switch (method) {
                case "POST":
                    body.put("message", "success");
                    response = createResponse(200, "OK", stringifyJson(body));
                    break;
                default:
                    body.put("errorMessage", "Invalid request method");
                    response = createResponse(400, "Bad Request", stringifyJson(body));
                    break;
            }

            outputStreamWriter.write(response);
            outputStreamWriter.flush();
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

    private static String createResponse(int statusCode, String message, String body) {
        return "HTTP/1.1 " + statusCode + " " + message + "\n" + 
        "Content-Type: application/json\n" + 
        "\n" + body;
    }

    private static String stringifyJson(Map<String, String> object) {
        if (object == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean isFirstItem = true;
        for (Map.Entry<String, String> entry : object.entrySet()) {
            if (!isFirstItem) {
                sb.append(",");
            }
            sb.append("\"" + entry.getKey() + "\"");
            sb.append(": ");
            sb.append("\"" + entry.getValue() + "\"");
            isFirstItem = false;
        }
        sb.append("}");
        return sb.toString();
    }
}
