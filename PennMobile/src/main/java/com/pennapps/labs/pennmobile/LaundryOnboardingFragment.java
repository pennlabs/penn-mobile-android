package com.pennapps.labs.pennmobile;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class LaundryOnboardingFragment extends Fragment {

    @Bind(R.id.laundry_onboarding_text)
    TextView mTextView;
    @Bind(R.id.laundry_onboarding_button)
    Button mButton;
    @Bind(R.id.laundry_onboarding_page0)
    View mPage0;
    @Bind(R.id.laundry_onboarding_page1)
    View mPage1;
    @Bind(R.id.laundry_onboarding_page2)
    View mPage2;

    public LaundryOnboardingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_laundry_onboarding, container, false);

        ButterKnife.bind(this, view);
        int pageNum = getArguments().getInt("page");
        mTextView.setText("Page " + pageNum);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // go to laundry activity
                Intent intent = new Intent(getActivity(), LaundryActivity.class);
                startActivity(intent);
            }
        });

        // last page
        if (pageNum == 2) {
            mButton.setText(R.string.ok);

            // move ok button to bottom right
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mButton.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            mButton.setLayoutParams(params);

            // set up page indicators
            mPage0.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.dark_circle));
            mPage1.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.dark_circle));
            mPage2.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.light_circle));
        }

        // page 0 or 1
        else {
            mButton.setText(R.string.skip);

            // move skip button to bottom left
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mButton.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            mButton.setLayoutParams(params);

            // page 0
            if (pageNum == 0) {
                // set up page indicators
                mPage0.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.light_circle));
                mPage1.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.dark_circle));
                mPage2.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.dark_circle));
            }
            // page 1
            else {
                // set up page indicators
                mPage0.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.dark_circle));
                mPage1.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.light_circle));
                mPage2.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.dark_circle));
            }
        }
        return view;
    }
}
