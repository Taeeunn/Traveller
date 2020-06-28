package edu.skku.map.mapproject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class PersonalFragment extends Fragment {
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
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1, mParam2;
    private DatabaseReference mPostReference;
    private ArrayList<WalletItem> items;
    private View rootView;
    private String id, money, date, purpose, url, people, date2, unit, money2;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private Dialog dialog;
    private int onetwo;
    EditText planET, planET2;
    private BigDecimal sum;
    private double rate;
    private String myResponse, entrystr, txtresult;


    public PersonalFragment() {
        // Required empty public constructor
    }


    public static PersonalFragment newInstance(String param1, String param2) {
        PersonalFragment fragment = new PersonalFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView=inflater.inflate(R.layout.fragment_personal, container, false);
        mRecyclerView=(RecyclerView) rootView.findViewById(R.id.datalist);

        if(getArguments()!=null){
            id=getArguments().getString("id");
        }

        items=new ArrayList<WalletItem>();


        mPostReference= FirebaseDatabase.getInstance().getReference();
        mPostReference.child("money").child(id).child("private").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                items.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    money=postSnapshot.child("expend").getValue().toString()+" "+postSnapshot.child("unit").getValue().toString();
                    date=postSnapshot.child("date").getValue().toString();
                    purpose=postSnapshot.child("purpose").getValue().toString();
                    people=postSnapshot.child("people").getValue().toString();
                    url=postSnapshot.child("url").getValue().toString();



                    WalletItem i=new WalletItem(money, date, purpose, people, url);

                    items.add(i);
                }


                mLayoutManager = new GridLayoutManager(getActivity(), 1);
                mRecyclerView.setLayoutManager(mLayoutManager);
                WalletRecyclerAdapter myAdapter = new WalletRecyclerAdapter(items);
                mRecyclerView.setAdapter(myAdapter);




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        OkHttpClient client=new OkHttpClient();

        HttpUrl.Builder urlBuilder=HttpUrl.parse("https://api.exchangeratesapi.io/latest").newBuilder();
        urlBuilder.addQueryParameter("base", "KRW");

        String url=urlBuilder.build().toString();
        Request req=new Request.Builder().url(url).build();

        client.newCall(req).enqueue(new Callback() {

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                myResponse = response.body().string();
                System.out.println(myResponse);

                Gson gson=new GsonBuilder().create();
                JsonObject job=gson.fromJson(myResponse, JsonObject.class);
                JsonElement entry=job.getAsJsonObject("rates");
                entrystr=entry.toString();
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }
        });

        ImageButton calculate = (ImageButton)rootView.findViewById(R.id.calculate);
        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dialog=new Dialog(getActivity());
                dialog.setContentView(R.layout.calculate_pop_up);

                planET=(EditText) dialog.findViewById(R.id.plan);
                planET.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        new DatePickerDialog(getActivity(), myDatePicker,
                                myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                        onetwo=1;

                    }
                });

                planET2=(EditText) dialog.findViewById(R.id.plan2);
                planET2.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        new DatePickerDialog(getActivity(), myDatePicker,
                                myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                        onetwo=2;
                    }
                });

                Button button=(Button) dialog.findViewById(R.id.button);

                dialog.show();

                button.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){

                        sum=new BigDecimal("0");
                        txtresult="";
                        mPostReference.child("money").child(id).child("private").addValueEventListener(new ValueEventListener() {
                            String plan=planET.getText().toString().replace("/", "");
                            String plan2=planET2.getText().toString().replace("/", "");
                            int planint=Integer.parseInt(plan);
                            int plan2int=Integer.parseInt(plan2);

                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {


                                    date2=postSnapshot.child("date").getValue().toString().replace("/", "");
                                    unit=postSnapshot.child("unit").getValue().toString();
                                    money2=postSnapshot.child("expend").getValue().toString();

                                    int dateint=Integer.parseInt(date2);

                                    if (dateint>=planint && dateint<=plan2int){
                                        System.out.println(dateint);
                                        System.out.println(Double.parseDouble(money2));
                                        System.out.println(entrystr);

                                        String [] arr=entrystr.split(unit+"\":", 2);
                                        String [] arr2=arr[1].split(",", 2);
                                        rate=Double.parseDouble(arr2[0]);
                                        System.out.println(rate);

                                        BigDecimal big1=new BigDecimal(money2);
                                        BigDecimal big2=new BigDecimal(arr2[0]);
                                        BigDecimal result=big1.divide(big2, BigDecimal.ROUND_HALF_UP);

                                        sum=sum.add(result);
                                        txtresult+=result+"\n+\n";
                                        System.out.println(sum);

                                    }
                                }

                                if (txtresult!="") {
                                    TextView textView2 = (TextView) dialog.findViewById(R.id.textView2);
                                    textView2.setText(txtresult.substring(0, txtresult.length() - 2) + "=");
                                    TextView textView = (TextView) dialog.findViewById(R.id.textView);
                                    textView.setText(sum.toPlainString() + "  원");
                                }else{
                                    TextView textView2 = (TextView) dialog.findViewById(R.id.textView2);
                                    textView2.setText("소비 내역이 없습니다.");
                                    TextView textView = (TextView) dialog.findViewById(R.id.textView);
                                    textView.setText("");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                });


                Button button2=(Button) dialog.findViewById(R.id.button2);
                button2.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        dialog.dismiss();
                    }
                });
            }

        });

        return rootView;
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
            Toast.makeText(getActivity(), "Please choose correct date", Toast.LENGTH_SHORT).show();
        }else {
            System.out.println(s1);
            System.out.println(sdf.format(myCalendar.getTime()));
            et_date2.setText(sdf.format(myCalendar.getTime()));
        }
    }
}
