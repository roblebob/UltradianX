package com.roblebob.ultradianx.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.roblebob.ultradianx.R;
import com.roblebob.ultradianx.ui.adapter.DetailsRVAdapter;
import com.roblebob.ultradianx.util.UtilKt;
import com.roblebob.ultradianx.viewmodel.AppViewModel;
import com.roblebob.ultradianx.viewmodel.AppViewModelFactory;

import java.time.Duration;
import java.time.Instant;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScreenSlidePageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScreenSlidePageFragment extends Fragment {
    public static final String TAG = ScreenSlidePageFragment.class.getSimpleName();
    private static final String ARG_BUNDLE = "param_bundle";
    private Bundle mBundle;
    private View mRootView;
    public ScreenSlidePageFragment() { /* Required empty public constructor */ }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param bundle Parameter 1.
     * @return A new instance of fragment ScreenSlidePageFragment.
     */
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

        return mRootView;
    }


    public void refresh(Bundle bundle) {
        mBundle = bundle;
        refresh();
    }

    public void refresh() {
        ((TextView) mRootView.findViewById( R.id.fragment_screen_slide_title_tv)) .setText( Html.fromHtml( mBundle.getString("title"), Html.FROM_HTML_MODE_COMPACT));
        ((TextView) mRootView.findViewById( R.id.fragment_screen_slide_tags_tv)) .setText(mBundle.getString("tags").replace(' ', '\n'));
        ((LinearProgressIndicator) mRootView.findViewById(R.id.progressBar)).setProgressCompat( (int) mBundle.getDouble("priority"), false);
        mDetailsRVAdapter.submit(mBundle.getStringArrayList("details"));
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "---> " + "onPause()");
        update();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "---> " + "onResume()");
        refresh();
    }

    public void update() {
        //final double GROW_RATE = 100. / (24.0 * 60.0 * 60.0);
        final double GROW_RATE = 1.0;

        Instant oldLast = Instant.parse( mBundle.getString("last"));
        Instant newLast = Instant.parse( UtilKt.getRidOfNanos( Instant.now().toString()));

        Duration duration = Duration.between(oldLast, newLast);

        double oldPriority = mBundle.getDouble("priority");
        double newPriority = oldPriority + (duration.getSeconds() * GROW_RATE);

        AppViewModelFactory appViewModelFactory = new AppViewModelFactory(requireActivity().getApplication());
        AppViewModel viewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) appViewModelFactory).get(AppViewModel.class);
        viewModel.updatePassiveAdventure(mBundle.getInt("id"), newPriority, newLast.toString());
    }
}