package com.swakos.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.swakos.R;
import com.swakos.helper.HelperMethods;

public class RegistrationFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_phone, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Find the required views
        EditText edTvPhoneNumber = view.findViewById(R.id.edtv_phone_number);
        Button button = view.findViewById(R.id.signup_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String countryCode = "91";
                String phoneNumber = edTvPhoneNumber.getText().toString().trim();
                if (phoneNumber.isEmpty() || phoneNumber.length() < 10){
                    edTvPhoneNumber.setError("Valid Phone Number is required");
                    edTvPhoneNumber.requestFocus();
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString("phoneNumber", "+" + countryCode + phoneNumber);
                OTPFragment otpFragment = new OTPFragment();
                otpFragment.setArguments(bundle);
                if (getActivity() != null)
                    HelperMethods.loadFragmentNew(otpFragment, getActivity());

            }
        });
    }
}
