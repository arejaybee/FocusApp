package net.arejaybee.focus.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class Messages implements Serializable {

    public ArrayList<String> messages;

    public Messages(){
        messages = new ArrayList<>();

        messages.add("You're getting distracted.");
    }

    public String getMessage(){
        if(messages.size() == 0){
            return "Focus!";
        }
        else {
            Random rand = new Random();
            return messages.get(rand.nextInt(messages.size()));
        }
    }

    public void addMessage(String m){
            messages.add(m);
    }
    public void removeMessage(int i){
        messages.remove(i);
    }
}
