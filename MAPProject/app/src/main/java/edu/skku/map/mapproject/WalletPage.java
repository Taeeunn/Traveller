package edu.skku.map.mapproject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class WalletPage extends AppCompatActivity{


    Calendar myCalendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener myDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            updateLabel();

        }
    };

    private DatabaseReference mPostReference;
    private StorageReference moneyStorageRef;
    private String id="";
    private static final int PICK_IMAGE=777;
    Uri currentImageUri;
    ViewPager2 viewPager;
    private String url;
    private Dialog dialog;
    EditText expendET, dateET, purposeET, peopleET;
    private String expend="", unit="", date="", purpose="", people="";
    private UploadTask uploadTask;
    private String postUrl;
    private CheckBox aloneCK, peopleCK;

    private boolean privateMoney, publicMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_page);

        if(getIntent().getExtras() != null){
            Intent profileIntent = getIntent();
            id=profileIntent.getStringExtra("id");
        }

        viewPager = findViewById(R.id.viewpager);


        ImageButton newPost = (ImageButton)findViewById(R.id.newPost);
        newPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog=new Dialog(WalletPage.this);
                dialog.setContentView(R.layout.wallet_pop_up);


                dateET=(EditText) dialog.findViewById(R.id.dateET);
                dateET.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        new DatePickerDialog(WalletPage.this, myDatePicker,
                                myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });


                final Spinner moneySpinner=(Spinner) dialog.findViewById(R.id.spinner_money);
                String [] str=getResources().getStringArray(R.array.money);
                ArrayAdapter<String> moneyAdapter=new ArrayAdapter<String>(WalletPage.this, R.layout.support_simple_spinner_dropdown_item, str);
                moneyAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                moneySpinner.setAdapter(moneyAdapter);

                expendET=(EditText)dialog.findViewById(R.id.moneyET);
                dateET=(EditText)dialog.findViewById(R.id.dateET);
                purposeET=(EditText)dialog.findViewById(R.id.purposeET);
                peopleET=(EditText)dialog.findViewById(R.id.peopleET);
                aloneCK = (CheckBox) dialog.findViewById(R.id.private_money);
                peopleCK = (CheckBox) dialog.findViewById(R.id.public_money);

                dialog.show();

                ImageButton select=(ImageButton) dialog.findViewById(R.id.image);
                select.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        Intent gallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                        startActivityForResult(gallery, PICK_IMAGE);
                    }
                });


                Button btn=(Button) dialog.findViewById(R.id.button);
                btn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {

                        mPostReference = FirebaseDatabase.getInstance().getReference();
                        moneyStorageRef = FirebaseStorage.getInstance().getReference("money");


                        expend = expendET.getText().toString();
                        date = dateET.getText().toString();
                        purpose = purposeET.getText().toString();

                        unit = moneySpinner.getSelectedItem().toString();


                        if (expend.length() * date.length() * purpose.length() == 0) {
                            Toast.makeText(WalletPage.this, "Please fill all blanks", Toast.LENGTH_SHORT).show();
                        } else if (!aloneCK.isChecked() && !peopleCK.isChecked()) {
                            Toast.makeText(WalletPage.this, "Please select 개인 or 단체", Toast.LENGTH_SHORT).show();
                        } else {

                            if (aloneCK.isChecked()) {
                                privateMoney = true;
                            } else if (peopleCK.isChecked()) {
                                publicMoney = true;
                                people = peopleET.getText().toString();
                            }
                            if (currentImageUri != null) {
                                StorageReference ref = moneyStorageRef.child(currentImageUri.getLastPathSegment());
                                uploadTask = ref.putFile(currentImageUri);

                            }

                            postFirebaseDatabase(true);
                            dialog.dismiss();

                        }
                    }
                });
            }
        });



        ViewPager2 viewPager2 = findViewById(R.id.viewpager);
        viewPager2.setAdapter(new myFragmentStateAdapter(this, id));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.TabLayout);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0:
                        tab.setText("Personal");
                        break;
                    case 1:
                        tab.setText("Public");
                        break;
                }
            }
        });
        tabLayoutMediator.attach();

    }

    private void updateLabel() {
        String myFormat = "yyyy/MM/dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);

        final EditText et_date = (EditText) dialog.findViewById(R.id.dateET);
        et_date.setText(sdf.format(myCalendar.getTime()));
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==PICK_IMAGE && data!=null){
            ImageView img=(ImageView) dialog.findViewById(R.id.image);
            currentImageUri = data.getData();
            img.setImageURI(data.getData());

        }
    }

    public void postFirebaseDatabase(boolean add){

        if(add){
            mPostReference= FirebaseDatabase.getInstance().getReference();

            FirebaseMoney money;

            if(currentImageUri==null) {

                if(privateMoney){
                    money = new FirebaseMoney(expend, unit, date, purpose, "", "");
                    mPostReference.child("money").child(id).child("private").push().setValue(money);
                    privateMoney=false;
                }else if(publicMoney){
                    money = new FirebaseMoney(expend, unit, date, purpose, "", people);
                    mPostReference.child("money").child(id).child("public").push().setValue(money);
                    publicMoney=false;
                }

            }else {
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    FirebaseMoney money;

                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        if(taskSnapshot.getMetadata()!=null){
                            if(taskSnapshot.getMetadata().getReference()!=null){
                                Task<Uri> result=taskSnapshot.getStorage().getDownloadUrl();
                                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        postUrl=uri.toString();
                                        if(privateMoney){
                                            money = new FirebaseMoney(expend, unit, date, purpose, postUrl, "");
                                            mPostReference.child("money").child(id).child("private").push().setValue(money);
                                            privateMoney=false;
                                        }else if(publicMoney){
                                            money = new FirebaseMoney(expend, unit, date, purpose, postUrl, people);
                                            mPostReference.child("money").child(id).child("public").push().setValue(money);
                                            publicMoney=false;
                                        }
                                        postUrl="";
                                    }
                                });
                            }
                        }
                    }
                });
            }
        }
    }
}





