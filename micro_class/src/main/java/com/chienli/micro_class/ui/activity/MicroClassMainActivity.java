package com.chienli.micro_class.ui.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.chienli.micro_class.BR;
import com.chienli.micro_class.R;
import com.chienli.micro_class.ui.activity.base.BaseActivity;
import com.chienli.micro_class.view_model.MicroClassMainActivityViewModel;
import com.chienli.micro_class.databinding.ActivtyMicroClassMainBinding;

public class MicroClassMainActivity extends BaseActivity {
    ActivtyMicroClassMainBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activty_micro_class_main);

        MicroClassMainActivityViewModel model = new MicroClassMainActivityViewModel(
                this,
                R.layout.item_micro_class_main_rcl,
                BR.data,
                BR.position
        );

        binding.setMainVM(model);
    }

    public ActivtyMicroClassMainBinding getBinding(){
        return binding;
    }
}
