package org.backrooms.backroom_messenger.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.backrooms.backroom_messenger.entity.User;
import org.backrooms.backroom_messenger.serverRequest.LoginRequest;
import org.backrooms.backroom_messenger.serverRequest.SignupRequest;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;

import static org.backrooms.backroom_messenger.StaticMethods.generateSalt;
import static org.backrooms.backroom_messenger.StaticMethods.hashPassword;

public class Client  {


    static ObjectMapper mapper = new ObjectMapper();
    static User loggedUser;
    static Socket socket;
    static DataInputStream dis;
    static DataOutputStream dos;
    public Client(String host) throws IOException {

    }

    public static void main(String[] args) throws Exception {
        socket = new Socket("localhost",8888);
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());

        System.out.println("1.Login\n 2.sign up");
        Scanner scanner = new Scanner(System.in);
        int option = scanner.nextInt();
        switch (option) {
            case 1:
                String username = scanner.next();
                String password = scanner.next();
                try {
                    loggedUser = login(username,password);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }finally {
                    break;
                }
            case 2:
                System.out.println("name :");
                String name = scanner.next();
                System.out.println("password :");
                String pass = scanner.next();
                loggedUser = signup(name,pass);

        }
    }

    public static User login(String username, String password) throws IOException {
        String message = username + "--" + password;
        LoginRequest lr = new LoginRequest(message);
        mapper.registerSubtypes(new NamedType(LoginRequest.class, "loginRequest"));
        String request = mapper.writeValueAsString(lr);
        System.out.println(request);
        dos.writeUTF(request);
        dos.flush();
        String response = dis.readUTF();
        return null;
    }

    public static User signup(String username, String password) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = generateSalt();

        String message = username + "--" + password + "--" + salt;

        SignupRequest sr = new SignupRequest(message);
        mapper.registerSubtypes(new NamedType(SignupRequest.class, "signupRequest"));
        String request = mapper.writeValueAsString(sr);
        dos.writeUTF(request);
        dos.flush();
        String response = dis.readUTF();
        return null;
    }
}
