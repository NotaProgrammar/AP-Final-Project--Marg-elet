package org.backrooms.backroom_messenger.server;


import org.backrooms.backroom_messenger.entity.Chat;
import org.backrooms.backroom_messenger.entity.Message;
import org.backrooms.backroom_messenger.response_and_requests.serverResopnse.ServerResponse;

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
                if(clientHandler.getActiveUser() != null) {
                    for (Chat chat : clientHandler.getActiveUser().getChats()) {
                        if (chat.getId().equals(message.getChat()) && !message.getSender().equals(clientHandler.getActiveUser().getUsername())) {
                            clientHandler.receiveMessage(message);
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void sendResponse(ServerResponse response,List<String> usernames) {
        for(ClientHandler clientHandler : onlineClients) {
            if(clientHandler.getActiveUser() != null) {
                for(String username: usernames) {
                    if(clientHandler.getActiveUser().getUsername().equals(username)) {
                        clientHandler.sendResponse(response);
                    }
                }
            }
        }
    }

}
