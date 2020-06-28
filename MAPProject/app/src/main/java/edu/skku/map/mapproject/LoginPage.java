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

public class LoginPage extends AppCompatActivity {


    private DatabaseReference mPostReference;
    private String id="", passwd="";
    EditText idET, passwdET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        idET=(EditText)findViewById(R.id.userid);
        passwdET=(EditText)findViewById(R.id.password);
        mPostReference= FirebaseDatabase.getInstance().getReference();


        if(getIntent().getExtras() != null){
            idET = (EditText)findViewById(R.id.userid);
            Intent signupIntent = getIntent();
            idET.setText(signupIntent.getStringExtra("id"));
        }


        Button login = (Button)findViewById(R.id.loginButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                id=idET.getText().toString();
                passwd=passwdET.getText().toString();

                if(id.length() == 0){
                    Toast.makeText(LoginPage.this, "Wrong Username", Toast.LENGTH_SHORT).show();
                } else {

                    mPostReference.child("user_list").addListenerForSingleValueEvent(new ValueEventListener() {
                        boolean login = true;
                        boolean exist = false;
                        String nickname="";

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                String key = postSnapshot.getKey();

                                if (key.equals(id)) {
                                    exist=true;
                                    if (!postSnapshot.child("passwd").getValue().equals(passwd)) {
                                        Toast.makeText(LoginPage.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                                        login = false;
                                    }
                                    else{
                                        nickname=postSnapshot.child("nickname").getValue().toString();
                                    }
                                    break;
                                }
                            }
                            if(!exist){
                                Toast.makeText(LoginPage.this, "There is no username you input..", Toast.LENGTH_SHORT).show();
                            }else if(login) {

                                Intent loginIntent = new Intent(LoginPage.this, SelectPage.class);
                                loginIntent.putExtra("nickname", nickname);
                                loginIntent.putExtra("id", id);
                                startActivity(loginIntent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }
        });

        Button signup = (Button)findViewById(R.id.signupButton);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupIntent = new Intent(LoginPage.this, SignUpPage.class);
                startActivity(signupIntent);
            }
        });
    }
}
