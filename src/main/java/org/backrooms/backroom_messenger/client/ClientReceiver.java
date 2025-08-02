package org.backrooms.backroom_messenger.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.backrooms.backroom_messenger.ClientReceiverGUI;
import org.backrooms.backroom_messenger.entity.MultiUserChat;
import org.backrooms.backroom_messenger.entity.PvChat;
import org.backrooms.backroom_messenger.response_and_requests.serverResopnse.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

import static org.backrooms.backroom_messenger.client.Client.*;

public class ClientReceiver implements Runnable {
    private BufferedReader reader;
    private final ObjectMapper mapper = new ObjectMapper();

    public ClientReceiver(BufferedReader reader) {
        this.reader = reader;
        registerMapper();
    }

    private void registerMapper() {
        mapper.registerSubtypes(new NamedType(PvChat.class, "PvChat"));
        mapper.registerSubtypes(new NamedType(MultiUserChat.class,"channel"));
    }

    @Override
    public void run() {
        try {
            while(true){
                StringBuilder sb = new StringBuilder();
                String endMarker = "###END###";

                char[] buffer = new char[1024];
                int charsRead;

                while ((charsRead = reader.read(buffer)) != -1) {
                    String chunk = new String(buffer, 0, charsRead);
                    sb.append(chunk);
                    if (sb.toString().contains(endMarker)) {
                        break;
                    }
                }

                String json = sb.toString().replace(endMarker, "");
                ServerResponse serverResponse = mapper.readValue(json, ServerResponse.class);
                Thread thread = new Thread(() -> {
                    responseCheck(serverResponse);
                });
                thread.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void responseCheck(ServerResponse sr){
        if(sr instanceof SearchedUsersListResponse sulr){
            userListHandle(sulr);
        }else if(sr instanceof ChatModifyResponse cmr){
            Client.chatModifyHandle(cmr);
        }else if(sr instanceof ReceivedMessage rm){
                ClientReceiverGUI.addReceivedMessage(rm.getMessageObject());
        }else if(sr instanceof UserLogResponse ulor){
            Client.setLastSeen(ulor);
        }else if(sr instanceof UserReadResponse urr){
            ClientReceiverGUI.readMessage(urr);
        }else if(sr instanceof FileFoundResponse ffr){
            saveFile(ffr);
        }
    }

    private static void saveFile(FileFoundResponse ffr) {
        String directory = ffr.getDirectory();
        String base64 = ffr.getFileBase64();
        String fileName = ffr.getFileName().replace("\n","");
        if(!base64.equals("")){
            byte[] bytes = Base64.getDecoder().decode(base64);
            File dir = new File(directory);
            File savingFile = new File(dir, fileName);

            try {
                if(!savingFile.exists()){
                    savingFile.getParentFile().mkdirs();
                    savingFile.createNewFile();
                }
                FileOutputStream fos = new FileOutputStream(savingFile);
                int offset = 0;
                int chunkSize = 4096;
                while (offset < bytes.length) {
                    int bytesToWrite = Math.min(chunkSize, bytes.length - offset);
                    fos.write(bytes, offset, bytesToWrite);
                    fos.flush();
                    offset += bytesToWrite;
                }
            }catch (IOException e) {
                System.out.println("Error saving file");
            }
        }
    }
}
