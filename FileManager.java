import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;
import java.io.IOException;

public class FileManager {
    private static final Logger logger = Logger.getLogger(FileManager.class.getName());

    private static final int BLOCK_SIZE = 4096;

    public FileManager() {}

    public void write(String filePath, String data) throws IOException {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(filePath, "rw")) {
            byte[] paddedData = padToBlockSize(data);
            randomAccessFile.write(paddedData);
            logger.info(String.format("Successfully persisted data to path: %s", filePath));
        }
    }

    public String read(String filePath) throws IOException {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(filePath, "r")) {
            byte[] bytes = new byte[BLOCK_SIZE];
            randomAccessFile.read(bytes);
            return new String(bytes, StandardCharsets.UTF_8).trim();
        }
    }

    // We pad to block size to ensure each string clearly takes one block.
    // Not the most efficient way, you could also store multiple strings within a 
    // single block and maintain its offset but we are not doing that here. 
    private static byte[] padToBlockSize(String data) {
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > BLOCK_SIZE) {
            throw new RuntimeException(String.format("You shall not proceed, data is too big: %s - %s", bytes, bytes.length));
        }

        if (bytes.length == BLOCK_SIZE) {
            return bytes;
        }

        byte[] paddedData = new byte[BLOCK_SIZE];
        System.arraycopy(bytes, 0, paddedData, 0, bytes.length);
        return paddedData;
    }
}
