import bg.sofia.uni.fmi.mjt.authserver.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.authserver.logging.logger.AuditLog;
import bg.sofia.uni.fmi.mjt.authserver.logging.logger.MJTAuditLog;
import bg.sofia.uni.fmi.mjt.authserver.manager.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.authserver.server.Server;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or

// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

void main() throws Exception {
    String logFile = "log.txt";
    String dbFile = "userDB.txt";

    int port = 6666;
    Server server = null;

    AuditLog logger = new MJTAuditLog(logFile);
    AuthenticationManager authenticationManager = new AuthenticationManager(KeyManager.getOrCreateKey(), dbFile);
    try (ExecutorService executor = Executors.newCachedThreadPool()) {


        CommandExecutor commandExecutor = new CommandExecutor(authenticationManager, executor, logger);
        server = new Server(port, commandExecutor);
        server.start();

    } catch (Exception e) {
        server.stop();
        throw new RuntimeException(e.getMessage(), e);
    }
    //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
    // to see how IntelliJ IDEA suggests fixing it.
}

public class KeyManager {
    private static final String KEY_FILE = "secret.key";

    public static SecretKey getOrCreateKey() throws Exception {
        Path keyPath = Paths.get(KEY_FILE);

        if (Files.exists(keyPath)) {
            byte[] keyBytes = Files.readAllBytes(keyPath);
            return new SecretKeySpec(keyBytes, "AES");
        } else {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            SecretKey key = keyGenerator.generateKey();

            Files.write(keyPath, key.getEncoded());
            return key;
        }
    }
}
