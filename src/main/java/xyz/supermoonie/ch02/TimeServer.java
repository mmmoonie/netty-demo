package xyz.supermoonie.ch02;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author moonie
 * @date 2018/2/22
 */
public class TimeServer {
    public static void main(String[] args) throws IOException {
        int port = 7100;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                // 采用默认值
            }
        }
        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
            System.out.println("the time server is start in port: " + port);
            Socket socket = null;
            while (true) {
                socket = server.accept();
                new Thread(new TimeServerHandler(socket)).start();
            }
        } finally {
            System.out.println("The time server close");
            server.close();
            server = null;
        }
    }
}