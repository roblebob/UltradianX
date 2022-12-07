package com.roblebob.ultradianx.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.roblebob.ultradianx.R;
import com.roblebob.ultradianx.ui.adapter.DetailsRVAdapter;

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

    private View mRootView;

    private boolean active = false;
    public void pressActiveSwitch() {
        active = !active;
        refreshActive();
        Log.e(TAG, "------> " + active);
    }
    public void refreshActive() {
        if (active) {
            requireActivity().getApplication().setTheme(R.style.Theme_UltradianX_active);

        } else {
            requireActivity().getApplication().setTheme(R.style.Theme_UltradianX_passive);
        }
    }


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


    DetailsRVAdapter mDetailsRVAdapter = new DetailsRVAdapter();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_screen_slide_page, container, false);

        ((TextView) mRootView.findViewById(R.id.fragment_screen_slide_title_tv)).setMovementMethod( new ScrollingMovementMethod());

        RecyclerView detailsRV = mRootView.findViewById(R.id.fragment_screen_slide_details_rv);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext(), RecyclerView.VERTICAL, false);
        detailsRV.setLayoutManager(layoutManager);
        detailsRV.setAdapter(mDetailsRVAdapter);
        refresh();

        mRootView.findViewById(R.id.active_switch).setOnClickListener(v -> {
            pressActiveSwitch();
        });

        return mRootView;
    }


    public void refresh(Bundle bundle) {
        mBundle = bundle;
        refresh();
    }

    public void refresh() {
        ((TextView) mRootView.findViewById( R.id.fragment_screen_slide_title_tv)) .setText(
                //mBundle.getString("title")
                Html.fromHtml( mBundle.getString("title"))
        );

        ((TextView) mRootView.findViewById( R.id.fragment_screen_slide_tags_tv)) .setText(mBundle.getString("tags").replace(' ', '\n'));

        mDetailsRVAdapter.submit(mBundle.getStringArrayList("details"));
    }
}