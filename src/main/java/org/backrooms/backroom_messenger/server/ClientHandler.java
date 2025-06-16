package org.backrooms.backroom_messenger.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.backrooms.backroom_messenger.entity.User;
import org.backrooms.backroom_messenger.serverRequest.LoginRequest;
import org.backrooms.backroom_messenger.serverRequest.ServerRequest;
import org.backrooms.backroom_messenger.serverRequest.SignupRequest;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;

import static org.backrooms.backroom_messenger.StaticMethods.hashPassword;

public class ClientHandler implements Runnable {
    private User activeUser;
    Socket socket;
    DataInputStream in;
    DataOutputStream out;
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

    private void CheckRequest(ServerRequest sr) throws NoSuchAlgorithmException, InvalidKeySpecException {
            if(sr instanceof LoginRequest lr){
                loginHandle(lr);
            }else if(sr instanceof SignupRequest sur){
                signupHandle(sur);
            }
    }

    private void loginHandle(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        User loggedUser = null;
        try{
            loggedUser = DataBaseManager.getUserFromDataBase(username);
            String hashedPassword = hashPassword(password, loggedUser.getSalt());
            if(hashedPassword.equals(loggedUser.getPassword())){
                activeUser = loggedUser;
                System.out.println("User logged in");
            }else{
                throw new Exception("Wrong password");
            }
        }catch (Exception e){
            notify(e);
        }
    }

    private  void signupHandle(SignupRequest signupRequest) throws NoSuchAlgorithmException, InvalidKeySpecException {

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
    }

    private void notify(Exception e) {
        //todo add more notifications
        System.out.println(e);
    }


}
