package org.backrooms.backroom_messenger.server;

import org.backrooms.backroom_messenger.entity.User;
import org.backrooms.backroom_messenger.serverRequest.SignupRequest;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;


import static org.backrooms.backroom_messenger.StaticMethods.hashPassword;


public class Server {
    private static ServerSocket serverSocket;
    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(8888);
            while(true) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }






}
