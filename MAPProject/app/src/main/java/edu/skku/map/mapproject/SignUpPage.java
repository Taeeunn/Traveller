package edu.skku.map.mapproject;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SignUpPage extends AppCompatActivity {

    private DatabaseReference mPostReference;
    private String id="", passwd="", nickname="", email="";
    EditText idET, passwdET,  nicknameET, emailET;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        idET=(EditText)findViewById(R.id.signupID);
        passwdET=(EditText)findViewById(R.id.signupPassword);
        nicknameET=(EditText)findViewById(R.id.signupNickname);
        emailET=(EditText)findViewById(R.id.signupEmail);
        Button btn=(Button)findViewById(R.id.signupButton);
        mPostReference= FirebaseDatabase.getInstance().getReference();


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id=idET.getText().toString();
                passwd=passwdET.getText().toString();
                nickname=nicknameET.getText().toString();
                email=emailET.getText().toString();


                if(id.length() * passwd.length() * nickname.length() * email.length() == 0){
                    Toast.makeText(SignUpPage.this, "Please fill all blanks", Toast.LENGTH_SHORT).show();
                } else {
                    mPostReference.child("user_list").addListenerForSingleValueEvent(new ValueEventListener() {
                        boolean result=false;

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                String key = postSnapshot.getKey();

                                if (key.equals(id)) {
                                    result=true;
                                    Toast.makeText(SignUpPage.this, "Please use another username", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }

                            if(!result){
                                Intent signupIntent = new Intent(SignUpPage.this, LoginPage.class);
                                signupIntent.putExtra("id", id);
                                startActivity(signupIntent);
                                postFirebaseDatabase(true);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }
        });
    }


    public void postFirebaseDatabase(boolean add){
        Map<String, Object> childUpdates=new HashMap<>();
        Map<String, Object> postValues=null;

        if(add){
            String profileUrl="";
            FirebaseUser user = new FirebaseUser(id, passwd, nickname, email);
            postValues=user.toMap();
        }

        childUpdates.put("/user_list/"+ id, postValues);
        mPostReference.updateChildren(childUpdates);
    }
}

