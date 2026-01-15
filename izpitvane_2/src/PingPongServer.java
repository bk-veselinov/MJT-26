//import java.io.BufferedInputStream;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//public class PingPongServer {
//    int port = 9090;
//
//    public void run()   {
//
//        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
//        try (ServerSocket serverSocket = new ServerSocket(port);){
//
//            while (true){
//                Socket socket = serverSocket.accept();
//                executorService.execute(()->pingPong(socket));
//
//                pingPong(socket);
//            }
//
//
//        }catch (IOException e){
//            throw new RuntimeException("Problem while executing io operatin");
//        }
//    }
//
//    private void pingPong(Socket socket) throws IOException {
//        try (socket; BufferedReader in = new BufferedReader(new
//                InputStreamReader(socket.getInputStream()));
//             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
//            String line = in.readLine();
//            out.println("pong");
//        }
//    }
//
//}