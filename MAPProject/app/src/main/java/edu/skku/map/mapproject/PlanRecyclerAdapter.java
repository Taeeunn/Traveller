package edu.skku.map.mapproject;


import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class PlanRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView mImageView;
        public TextView mTitleView, mCityView;
        private CardView cardView;



        public ViewHolder(View view) {
            super(view);
            mImageView=(ImageView) view.findViewById(R.id.image);
            mTitleView=(TextView) view.findViewById(R.id.title);
            mCityView=(TextView) view.findViewById(R.id.city);
            cardView=(CardView) view.findViewById(R.id.cardview);

        }
    }

    private ArrayList<PlanItem> items;
    private ViewHolder myViewHolder;
    Dialog dialog;

    PlanRecyclerAdapter(ArrayList<PlanItem> item, Context context){
        this.items=item;
        dialog=new Dialog(context);
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.plan_item, parent, false);
        context=parent.getContext();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        myViewHolder=(ViewHolder) holder;
        String url=items.get(position).getUrl();

        Glide.with(context).load(url).into(myViewHolder.mImageView);

        myViewHolder.mTitleView.setText(items.get(position).getTitle());
        myViewHolder.mCityView.setText(items.get(position).getCity());
        System.out.println(items.get(position).getCity());


        final int pos=position;
        myViewHolder.cardView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dialog.setContentView(R.layout.plan_pop_up);

                ImageView img=(ImageView) dialog.findViewById(R.id.image);
                TextView title=(TextView) dialog.findViewById(R.id.title);
                TextView city=(TextView) dialog.findViewById(R.id.city);
                TextView plan=(TextView) dialog.findViewById(R.id.plan);
                TextView budget=(TextView) dialog.findViewById(R.id.budget);
                TextView material=(TextView) dialog.findViewById(R.id.material);
                TextView memo=(TextView) dialog.findViewById(R.id.memo);
                TextView who=(TextView) dialog.findViewById(R.id.who);
                Button btn_close=(Button) dialog.findViewById(R.id.button);

                Glide.with(context).load(items.get(pos).getUrl()).into(img);
                title.setText(items.get(pos).getTitle());
                city.setText(items.get(pos).getCity());
                plan.setText(items.get(pos).getPlan());
                budget.setText("budget: "+items.get(pos).getBudget());
                material.setText("meterial: "+items.get(pos).getMaterials());
                memo.setText("memo: "+items.get(pos).getMemo());
                if(items.get(pos).getWho().contains("alone")){
                    who.setText("Travel "+items.get(pos).getWho());
                }else{
                    who.setText("Travel with "+items.get(pos).getWho());
                }


                dialog.show();

                btn_close.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        dialog.dismiss();
                    }
                });
            }
        });


    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
