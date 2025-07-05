package org.backrooms.backroom_messenger.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.backrooms.backroom_messenger.entity.User;
import org.backrooms.backroom_messenger.response_and_requests.serverRequest.*;
import org.backrooms.backroom_messenger.response_and_requests.serverResopnse.AvailableUserResponse;
import org.backrooms.backroom_messenger.response_and_requests.serverResopnse.SearchedUsersListResponse;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.List;

import static org.backrooms.backroom_messenger.StaticMethods.hashPassword;

public class ClientHandler implements Runnable {
    private User activeUser;
    Socket socket;
    DataInputStream in;
    DataOutputStream out;
    ObjectMapper mapper = new ObjectMapper();
    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        ObjectMapper mapper = new ObjectMapper();
        while(true){
            try {
                String request = in.readUTF();
                System.out.println(request);
                ServerRequest sr = mapper.readValue(request, ServerRequest.class);
                CheckRequest(sr);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    private void CheckRequest(ServerRequest sr) throws Exception {
            if(sr instanceof LoginRequest lr){
                loginHandle(lr);
            }else if(sr instanceof SignupRequest sur){
                signupHandle(sur);
            }else if(sr instanceof SearchRequest search){
                searchUser(search);
            }else if(sr instanceof NewChatRequest ncr){
                openChat(ncr);
            }
    }

    private void loginHandle(LoginRequest loginRequest) throws IOException {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        User loggedUser = null;
        try{
            loggedUser = DataBaseManager.getUserFromDataBase(username);
            String hashedPassword = hashPassword(password, loggedUser.getSalt());
            if(hashedPassword.equals(loggedUser.getPassword())){
                activeUser = loggedUser;
            }else{
                throw new Exception("Wrong password");
            }
        }catch (Exception e){
            notify(e);
        }

        AvailableUserResponse aur = new AvailableUserResponse(mapper.writeValueAsString(activeUser));
        String response = mapper.writeValueAsString(aur);
        System.out.println("response: " + response);
        out.writeUTF(response);
        out.flush();
    }

    private  void signupHandle(SignupRequest signupRequest) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {

        String username = signupRequest.getUsername();
        String password = signupRequest.getPassword();
        byte[] salt = signupRequest.getSalt();

        String hashedPassword = hashPassword(password,salt);

        User signedUser = null;
        try {
            signedUser = new User(username,hashedPassword,salt);
            DataBaseManager.addUserToDataBase(signedUser);
        } catch (SQLException e) {
            notify(e);
        }

        activeUser = signedUser;

        AvailableUserResponse aur = new AvailableUserResponse(mapper.writeValueAsString(activeUser));
        String response = mapper.writeValueAsString(aur);
        out.writeUTF(response);
        out.flush();
    }

    private void searchUser(SearchRequest searchRequest) throws Exception {
        String searched = searchRequest.getSearchTerm();
        //todo to be updated
        List<User> searchedUsers = DataBaseManager.searchUser(searched);
        String responseMessage = mapper.writeValueAsString(searchedUsers);

        SearchedUsersListResponse sulr = new SearchedUsersListResponse(responseMessage);
        String response = mapper.writeValueAsString(sulr);
        out.writeUTF(response);
        out.flush();
    }

    private void notify(Exception e) {
        System.out.println(e);
    }

    private void openChat(NewChatRequest ncr){
        
    }
}
