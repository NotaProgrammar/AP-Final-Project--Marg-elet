package org.backrooms.backroom_messenger.server;


import org.backrooms.backroom_messenger.entity.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class Server {
    private static ServerSocket serverSocket;
    private static List<ClientHandler> onlineClients = new ArrayList<>();
    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(8888);
            while(true) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket);
                onlineClients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void broadcast(Message message) {
        for(ClientHandler clientHandler : onlineClients) {
            try {
                clientHandler.receiveMessage(message);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
