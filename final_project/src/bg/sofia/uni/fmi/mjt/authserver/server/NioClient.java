package bg.sofia.uni.fmi.mjt.authserver.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class NioClient {

    private static final int SERVER_PORT = 6666;
    private static final String SERVER_HOST = "localhost";
    private static final int BUFFER_SIZE = 4096;

    public static void main() {

        ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

        try (SocketChannel socketChannel = SocketChannel.open();
             Scanner scanner = new Scanner(System.in)) {

            socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
            System.out.println("Connected to the server.");

            mainLoop(scanner, buffer, socketChannel);

        } catch (IOException e) {
            throw new RuntimeException("There is a problem with the network communication", e);
        }
    }

    private static void mainLoop(Scanner scanner, ByteBuffer buffer, SocketChannel socketChannel) throws IOException {
        while (true) {
            System.out.print("Enter message: ");
            String message = scanner.nextLine();

            if ("quit".equals(message)) {
                break;
            }
            System.out.println("Sending message <" + message + "> to the server...");

            buffer.clear();
            buffer.put(message.getBytes(StandardCharsets.UTF_8));
            buffer.flip();
            socketChannel.write(buffer);

            buffer.clear();
            socketChannel.read(buffer);
            buffer.flip();

            byte[] byteArray = new byte[buffer.remaining()];
            buffer.get(byteArray);
            String reply = new String(byteArray, "UTF-8");

            System.out.println("The server replied: \n" + reply);
        }
    }

}
