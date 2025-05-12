package com.frontend.libertywallet.fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.frontend.libertywallet.R;
import com.frontend.libertywallet.service.ForceLogOut;

public class SettingsFragment  extends Fragment {


    Button lougout;

    TextView backView,sendFeedbackView,changePasswordView,changeUsernameView,faqView;

    Switch pincodeSwitch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lougout = view.findViewById(R.id.logout_button);

        sendFeedbackView = view.findViewById(R.id.sendFeedback);
        faqView = view.findViewById(R.id.faqs);
        changePasswordView = view.findViewById(R.id.changePassword);
        changeUsernameView = view.findViewById(R.id.changeUsername);



        changePasswordView.setOnClickListener(v -> {
            FaqPopupDialogFragment popup = new FaqPopupDialogFragment();
            popup.show(getParentFragmentManager(), "faq_popup");
        });

        changeUsernameView.setOnClickListener(v -> {
            FaqPopupDialogFragment popup = new FaqPopupDialogFragment();
            popup.show(getParentFragmentManager(), "faq_popup");
        });

        faqView.setOnClickListener(v -> {
            FaqPopupDialogFragment popup = new FaqPopupDialogFragment();
            popup.show(getParentFragmentManager(), "faq_popup");
        });

        sendFeedbackView.setOnClickListener(v -> {
            FaqPopupDialogFragment popup = new FaqPopupDialogFragment();
            popup.show(getParentFragmentManager(), "faq_popup");
        });




        lougout.setOnClickListener( v -> ForceLogOut.forceLogout(getContext()));

    }


}
