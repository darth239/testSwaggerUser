
import java.security.SecureRandom;
import org.apache.commons.lang3.RandomStringUtils;

public class UserGenerator {
    public String createStringFromSymbols(int size) {
        return randomString(size, SYMBOLS);
    }

    public String createStringFromLetters(int size) {
        return randomString(size, LETTERS);
    }

    public String createStringFromNumbers(int size) {
        return randomString(size, NUMBERS);
    }

    private char[] SYMBOLS = "QWERTYUIOPASDFGHJKLZXCVBNM0123456789".toCharArray();

    private char[] LETTERS = "QWERTYUIOPASDFGHJKLZXCVBNM".toCharArray();
    private char[] NUMBERS = "0123456789".toCharArray();
    private SecureRandom random = new SecureRandom();

    private String randomString(int size, char[] symbols) {
        return RandomStringUtils.random(size, 0, 0, true, true, symbols, random);
    }
}
