package edu.skku.map.mapproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class PlanPage extends AppCompatActivity {

    Calendar myCalendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener myDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            if (onetwo==1) {
                updateLabel();
            }else if(onetwo==2){
                updateLabel2();
            }
        }
    };

    private DatabaseReference mPostReference;
    private StorageReference tripStorageRef;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private ArrayList<PlanItem> items;
    private String title, city, plan, budget, material, memo, who, url;
    private String title_="", city_="", material_="", memo_="", plan_="", plan2_="", budget_="", money_="";
    String id;
    private Dialog dialog;
    private static final int PICK_IMAGE=777;
    EditText titleET, cityET, planET, planET2, budgetET, materialET, memoET;
    private int onetwo;
    private CheckBox aloneCK, friendCK, familyCK;
    private boolean alone, friend, family;
    private Uri currentImageUri;
    private UploadTask uploadTask;
    private String postUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_page);

        if (getIntent().getExtras() != null) {
            Intent signupIntent = getIntent();
            id=signupIntent.getStringExtra("id");
        }

        items = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.plan_recycler_view);
        mLayoutManager = new GridLayoutManager(PlanPage.this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);



        mPostReference= FirebaseDatabase.getInstance().getReference();
        mPostReference.child("trip").child(id).addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                items.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {


                    title=postSnapshot.child("title").getValue().toString();
                    city=postSnapshot.child("city").getValue().toString();
                    plan=postSnapshot.child("plan").getValue().toString()+"~"+postSnapshot.child("plan2").getValue().toString();
                    budget=postSnapshot.child("budget").getValue().toString()+" "+postSnapshot.child("money").getValue().toString();
                    material=postSnapshot.child("material").getValue().toString();
                    memo=postSnapshot.child("memo").getValue().toString();
                    who=postSnapshot.child("type").getValue().toString();
                    url=postSnapshot.child("url").getValue().toString();



                    PlanItem i=new PlanItem(title, city, plan, budget, material, memo, who, url);

                    items.add(i);
                }


                PlanRecyclerAdapter myAdapter = new PlanRecyclerAdapter(items, PlanPage.this);
                mRecyclerView.setAdapter(myAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });



        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog=new Dialog(PlanPage.this);
                dialog.setContentView(R.layout.activity_new_trip);
                dialog.show();


                ImageButton select=(ImageButton) dialog.findViewById(R.id.image);
                select.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        Intent gallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                        startActivityForResult(gallery, PICK_IMAGE);
                    }
                });

                planET=(EditText) dialog.findViewById(R.id.plan);
                planET.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        new DatePickerDialog(PlanPage.this, myDatePicker,
                                myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                        onetwo=1;

                    }
                });

                planET2=(EditText) dialog.findViewById(R.id.plan2);
                planET2.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        new DatePickerDialog(PlanPage.this, myDatePicker,
                                myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                        onetwo=2;
                    }
                });

                final Spinner moneySpinner=(Spinner) dialog.findViewById(R.id.spinner_money);
                String [] str=getResources().getStringArray(R.array.money);
                ArrayAdapter<String> moneyAdapter=new ArrayAdapter<String>(PlanPage.this, R.layout.support_simple_spinner_dropdown_item, str);
                moneyAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                moneySpinner.setAdapter(moneyAdapter);

                titleET=(EditText) dialog.findViewById(R.id.title);
                cityET=(EditText) dialog.findViewById(R.id.city);
                budgetET=(EditText) dialog.findViewById(R.id.budget);
                materialET=(EditText) dialog.findViewById(R.id.material);
                memoET=(EditText)dialog.findViewById(R.id.memo);
                aloneCK = (CheckBox) dialog.findViewById(R.id.alone);
                friendCK = (CheckBox) dialog.findViewById(R.id.friend);
                familyCK = (CheckBox) dialog.findViewById(R.id.family);

                mPostReference= FirebaseDatabase.getInstance().getReference();
                tripStorageRef= FirebaseStorage.getInstance().getReference("trip");



                Button createPost = (Button) dialog.findViewById(R.id.createPost);
                createPost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        title_=titleET.getText().toString();
                        city_=cityET.getText().toString();
                        plan_=planET.getText().toString();
                        plan2_=planET2.getText().toString();
                        budget_=budgetET.getText().toString();
                        money_=moneySpinner.getSelectedItem().toString();
                        material_=materialET.getText().toString();
                        memo_=memoET.getText().toString();



                        if(title_.length() * city_.length() * plan_.length() * plan2_.length() * budget_.length() * material_.length() == 0){
                            Toast.makeText(PlanPage.this, "Please fill all blanks", Toast.LENGTH_SHORT).show();
                        }else {


                            if (aloneCK.isChecked()){
                                alone=true;
                            }else if(friendCK.isChecked()) {
                                friend=true;
                            }else if(familyCK.isChecked()){
                                family=true;
                            }

                            if (currentImageUri!=null) {
                                StorageReference ref = tripStorageRef.child(currentImageUri.getLastPathSegment());
                                uploadTask = ref.putFile(currentImageUri);
                            }

                            postFirebaseDatabase(true);
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
    }

    private void updateLabel() {
        String myFormat = "yyyy/MM/dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);

        final EditText et_date = (EditText) dialog.findViewById(R.id.plan);
        et_date.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateLabel2() {
        String myFormat = "yyyy/MM/dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);

        final EditText et_date = (EditText) dialog.findViewById(R.id.plan);
        final EditText et_date2 = (EditText) dialog.findViewById(R.id.plan2);
        String s1=et_date.getText().toString().replace("/", "");
        String s2=sdf.format(myCalendar.getTime()).replace("/", "");
        if (Integer.parseInt(s1)>Integer.parseInt(s2)) {
            Toast.makeText(PlanPage.this, "Please choose correct date", Toast.LENGTH_SHORT).show();
        }else {
            System.out.println(s1);
            System.out.println(sdf.format(myCalendar.getTime()));
            et_date2.setText(sdf.format(myCalendar.getTime()));
        }
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

            FirebaseTrip trip;
            if(currentImageUri!=null) {
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    FirebaseTrip trip;

                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        if(taskSnapshot.getMetadata()!=null){
                            if(taskSnapshot.getMetadata().getReference()!=null){
                                Task<Uri> result=taskSnapshot.getStorage().getDownloadUrl();
                                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        postUrl=uri.toString();
                                        String who="";
                                        if (alone==true){
                                            who="alone";
                                            alone=false;
                                        }else if(friend==true){
                                            who="friend";
                                            friend=false;
                                        }else if(family==true){
                                            who="family";
                                            family=false;
                                        }
                                        trip = new FirebaseTrip(title_, city_, plan_, plan2_, budget_, money_, material_, memo_, postUrl, who);

                                        mPostReference.child("trip").child(id).push().setValue(trip);
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
