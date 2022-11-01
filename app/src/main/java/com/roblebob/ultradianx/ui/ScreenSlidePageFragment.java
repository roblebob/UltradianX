package com.roblebob.ultradianx.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.roblebob.ultradianx.R;
import com.roblebob.ultradianx.model.Adventure;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScreenSlidePageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScreenSlidePageFragment extends Fragment {

    public static final String TAG = ScreenSlidePageFragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_BUNDLE = "param_bundle";

    // TODO: Rename and change types of parameters
    private Bundle mBundle;


    public ScreenSlidePageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param bundle Parameter 1.
     * @return A new instance of fragment ScreenSlidePageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScreenSlidePageFragment newInstance(Bundle bundle) {
        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        Bundle args = new Bundle();
        args.putBundle(ARG_BUNDLE, bundle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBundle = getArguments().getBundle(ARG_BUNDLE);
        }
    }


    private TextView mTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_screen_slide_page, container, false);

        mTextView = rootView.findViewById(R.id.fragment_screen_slide_textview);

        mTextView.setText(mBundle.getString("title"));

        Log.e(TAG, "onCreateView(...)");
        return rootView;
    }




}