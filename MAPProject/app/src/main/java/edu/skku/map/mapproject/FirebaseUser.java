package edu.skku.map.mapproject;

import java.util.HashMap;
import java.util.Map;

public class FirebaseUser {
    public String id;
    public String passwd;
    public String nickname;
    public String email;



    public FirebaseUser(String id, String passwd, String nickname, String email){
        this.id=id;
        this.passwd=passwd;
        this.nickname=nickname;
        this.email=email;

    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result=new HashMap<>();
        result.put("id", id);
        result.put("passwd", passwd);
        result.put("nickname", nickname);
        result.put("email", email);
        return result;
    }
}
