package bg.sofia.uni.fmi.mjt.authserver.server;

import bg.sofia.uni.fmi.mjt.authserver.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.authserver.command.CommandFactory;
import bg.sofia.uni.fmi.mjt.authserver.entity.ConnectionData;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Server {

    private static final int BUFFER_SIZE = 4096;
    private static final String HOST = "localhost";

    private final CommandExecutor commandExecutor;
    private final int port;

    private boolean isServerWorking;
    private Selector selector;

    public Server(int port, CommandExecutor commandExecutor) {
        this.port = port;
        this.commandExecutor = commandExecutor;
    }

    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
             Selector selector = Selector.open()) {

            this.selector = selector;
            configureServerSocketChannel(serverSocketChannel, selector);

            isServerWorking = true;
            mainLoop();

        } catch (IOException e) {
            throw new UncheckedIOException("Failed to start server", e);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        isServerWorking = false;
        if (selector != null && selector.isOpen()) {
            selector.wakeup();
        }
    }

    private void configureServerSocketChannel(ServerSocketChannel channel, Selector selector) throws IOException {
        channel.bind(new InetSocketAddress(HOST, port));
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private void accept(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverChannel.accept();

        if (clientChannel == null) {
            return;
        }

        clientChannel.configureBlocking(false);

        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        clientChannel.register(selector, SelectionKey.OP_READ, new ConnectionData(buffer));

        System.out.println("Accepted connection from " + clientChannel.getRemoteAddress());
    }

    private void handleRead(SelectionKey key) throws IOException, ExecutionException, InterruptedException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ConnectionData connectionData = (ConnectionData) key.attachment();
        ByteBuffer buffer = (ByteBuffer) connectionData.getBuffer();

        buffer.clear();
        int readBytes = clientChannel.read(buffer);

        if (readBytes < 0) {
            System.out.println("Client disconnected: " + clientChannel.getRemoteAddress());
            clientChannel.close();
            key.cancel();
            return;
        }

        if (readBytes == 0) {
            return;
        }

        buffer.flip();
        String clientInput = readClientInputFromBuffer(buffer);
        executeReadCommand(clientChannel, key, buffer, clientInput);
    }

    private void writeClientOutput(SocketChannel clientChannel, String output, ByteBuffer buffer) throws IOException {

        buffer.clear();
        buffer.put((output + "\n").getBytes(StandardCharsets.UTF_8));
        buffer.flip();

        while (buffer.hasRemaining()) {
            clientChannel.write(buffer);
        }
    }

    private void mainLoop() throws IOException, ExecutionException, InterruptedException {
        while (isServerWorking) {
            int readyChannels = selector.select();

            if (readyChannels == 0) {
                continue;
            }

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                keyIterator.remove();

                if (!key.isValid()) {
                    continue;
                }

                if (key.isAcceptable()) {
                    accept(selector, key);
                } else if (key.isReadable()) {
                    handleRead(key);
                }
            }
        }
    }

    private String readClientInputFromBuffer(ByteBuffer buffer) {
        byte[] clientInputBytes = new byte[buffer.remaining()];
        buffer.get(clientInputBytes);

        String clientInput = new String(clientInputBytes, StandardCharsets.UTF_8);
        System.out.println(clientInput);
        return clientInput;
    }

    private void executeReadCommand(SocketChannel clientChannel, SelectionKey key,
                                    ByteBuffer buffer, String clientInput)
            throws IOException {
        try {
            CompletableFuture<String> futureOutput =
                    commandExecutor.executeCommand(CommandFactory.newCommand(clientInput), key);

            futureOutput.thenAccept(output -> {
                try {
                    writeClientOutput(clientChannel, output, buffer);

                } catch (IOException e) {

                    throw new UncheckedIOException(e.getMessage(), e);

                }
            }).exceptionally(throwable -> {
                throwable.printStackTrace();
                return null;
            });
        } catch (RuntimeException e) {

            writeClientOutput(clientChannel, "Error while processing the command. " + e.getMessage(), buffer);

        } catch (Exception e) {

            writeClientOutput(clientChannel,
                    "Server was unable to process this command. Please contact support.", buffer);
        }

    }

}