package com.company;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Scanner;

public class Main extends WebSocketServer {
    public Main(InetSocketAddress address) {
        super(address);
    }

    public static WebSocketServer server;
    public static void main(String[] args) {
	// write your code here
        Thread thread1 = new Thread() {
            public void run() {
                String input;
                System.out.println("\n"+"Enter Ip: ");
                Scanner sc=new Scanner(System.in);
                input=sc.next();


                String host = "192.168.0."+input;
                int port = 4649;

                server = new Main(new InetSocketAddress(host, port));
                System.out.print("Server started");
                server.run();

            }
        };
        thread1.start();




    }
    public static String encodeToString(BufferedImage image, String type) {
        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, type, bos);
            byte[] imageBytes = bos.toByteArray();

            BASE64Encoder encoder = new BASE64Encoder();
            imageString = encoder.encode(imageBytes);

            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageString;
    }


    public BufferedImage captureScreen() throws Exception {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle screenRectangle = new Rectangle(screenSize);
        Robot robot = new Robot();
        return robot.createScreenCapture(screenRectangle);

    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {


        System.out.println("new connection to " + webSocket.getRemoteSocketAddress());
        while(true)
        {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                System.out.println("Thread Exeption");
            }
            try {
                webSocket.send(encodeToString(captureScreen(),"png"));
                System.out.println("Frame Send....................");
            } catch (Exception e) {
                System.out.println("capture failed....................");
            }

        }
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        System.out.println("Retrying....................");
        server.run();
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {

    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        e.printStackTrace();
        System.out.println("Retrying....................");
        server.run();
    }
}
