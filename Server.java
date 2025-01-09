import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.logging.Logger;

public class Server {
    private static final Logger logger = Logger.getLogger(Server.class.getName());

    public static void main(String[] args) {
        int port = 8080;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info(String.format("Listening to requests from port %d", port));

            while (true) {
                Socket clientSocket = serverSocket.accept();
                logger.info(String.format("Client InetAddress: %s", clientSocket.getInetAddress()));

                InputStreamReader inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
                try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
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
                    throw e;
                } finally {
                    clientSocket.close();
                }
            }
        } catch (IOException e) {
            logger.severe(String.format("Encountered an exception %s, could not start the server", e.toString()));
        }
    }
}
