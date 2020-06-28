package edu.skku.map.mapproject;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class myFragmentStateAdapter extends FragmentStateAdapter{

    private String id;
    public myFragmentStateAdapter(@NonNull FragmentActivity fragmentActivity, String id_) {
        super(fragmentActivity);
        id=id_;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        PersonalFragment personalFragment=new PersonalFragment();
        PublicFragment publicFragment=new PublicFragment();
        Bundle bundle=new Bundle();
        bundle.putString("id", id);
        personalFragment.setArguments(bundle);
        publicFragment.setArguments(bundle);


        switch(position){
            case 0:
                return personalFragment;
            case 1:
                return publicFragment;

        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}