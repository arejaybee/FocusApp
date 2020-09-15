package net.arejaybee.focus.dto;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    public int secondsBetweenNotifications;
    private Messages message;

    public User(){
        message = new Messages();
        secondsBetweenNotifications = 10;
    }
    public void WriteObjectToFile(Context context) {

        try {
            FileOutputStream fileOut = context.openFileOutput("User.ser", Context.MODE_PRIVATE);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(this);
            objectOut.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static User ReadObjectFromFile(Context context){
        try(
                InputStream file = context.openFileInput("User.ser");
                InputStream buffer = new BufferedInputStream(file);
                ObjectInput input = new ObjectInputStream(buffer);
        ){
            return (User)input.readObject();
        }
        catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }
    public String getMessage(){
        return message.getMessage();
    }
    public ArrayList<String> getAllMessages(){return message.messages;}
    public void removeMessages(int index){message.removeMessage(index);}
    public void addMessage(String s){message.addMessage(s);}
}
