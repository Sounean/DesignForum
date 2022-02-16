package com.inet.designforum.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.inet.designforum.R;
import com.inet.designforum.fragment.base.BaseFragment;


public class MainMessageFragment extends BaseFragment implements View.OnClickListener {

    private TextView title;
    private LinearLayout linearLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.frament_main_message,container,false);
        return view;
    }

    @Override
    public void findViews(View view) {
        //linearLayout = view.findViewById(R.id.ll_main_message);
        title = view.findViewById(R.id.tv_toolbar_all_title);
    }

    @Override
    public void getData(View view) {

    }

    @Override
    public void initViews(View view) {
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_main_message_comment){

        }
    }
}
