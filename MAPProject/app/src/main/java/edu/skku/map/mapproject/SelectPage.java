package edu.skku.map.mapproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SelectPage extends AppCompatActivity {

    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_page);

        if (getIntent().getExtras() != null) {
            Intent signupIntent = getIntent();
            Toast toast=Toast.makeText(SelectPage.this, "Welcome, " + signupIntent.getStringExtra("nickname"), Toast.LENGTH_LONG);
            id=signupIntent.getStringExtra("id");
            toast.setGravity(Gravity.TOP,0, 600);

            toast.show();
        }

        Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(SelectPage.this, PlanPage.class);
                loginIntent.putExtra("id", id);
                startActivity(loginIntent);
            }


        });

        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(SelectPage.this, InfoPage.class);
                startActivity(loginIntent);
            }


        });

        Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(SelectPage.this, WalletPage.class);
                loginIntent.putExtra("id", id);
                startActivity(loginIntent);
            }


        });
    }
}
