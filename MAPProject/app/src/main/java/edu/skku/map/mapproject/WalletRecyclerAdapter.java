package edu.skku.map.mapproject;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class WalletRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    public static class PostViewHolder extends RecyclerView.ViewHolder{

        TextView money, date, purpose, people;
        ImageView image;

        PostViewHolder(View view) {
            super(view);
            money=view.findViewById(R.id.money);
            date=view.findViewById(R.id.date);
            purpose=view.findViewById(R.id.purpose);
            image=view.findViewById(R.id.image);
            people=view.findViewById(R.id.people);
        }
    }

    private ArrayList<WalletItem> items;
    private PostViewHolder myViewHolder;

    WalletRecyclerAdapter(ArrayList<WalletItem> item){
        this.items=item;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.wallet_item, parent, false);
        context=parent.getContext();
        return new PostViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        myViewHolder=(PostViewHolder) holder;

        myViewHolder.money.setText(items.get(position).getMoney());
        myViewHolder.date.setText(items.get(position).getDate());
        myViewHolder.purpose.setText(items.get(position).getPurpose());
        myViewHolder.people.setText(items.get(position).getPeople());


        String url=items.get(position).getUrl();
        Glide.with(context).load(url).into(myViewHolder.image);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
