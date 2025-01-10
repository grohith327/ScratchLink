public class Encoder {
    private static final String BASE62_CHARACTERS = 
    "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = BASE62_CHARACTERS.length();

    public Encoder() {}

    public String encode(long counter) {
        StringBuilder sb = new StringBuilder();
        while (counter > 0) {
            sb.append(BASE62_CHARACTERS.charAt((int) counter % BASE));
            counter = counter / BASE;
        }
        return sb.reverse().toString();
    }
}
