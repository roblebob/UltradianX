package com.roblebob.ultradianx.ui.fragment;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;

import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.slider.Slider;
import com.roblebob.ultradianx.R;
import com.roblebob.ultradianx.databinding.FragmentScreenSlidePageBinding;
import com.roblebob.ultradianx.repository.model.Adventure;
import com.roblebob.ultradianx.ui.adapter.DetailsRVAdapter;
import com.roblebob.ultradianx.repository.viewmodel.AppViewModel;
import com.roblebob.ultradianx.repository.viewmodel.AppViewModelFactory;
import com.roblebob.ultradianx.ui.extra.AdventureDisplay;
import com.roblebob.ultradianx.ui.extra.MyController;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScreenSlidePageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScreenSlidePageFragment extends Fragment  implements MyController.OnCallbackListener {
    public static final String TAG = ScreenSlidePageFragment.class.getSimpleName();
    public static final int ACTIVE_PROGRESS_UPDATE_INTERVAL = 2000;
    private static final String ID = "id";
    public ScreenSlidePageFragment() { /* Required empty public constructor */ }
    private FragmentScreenSlidePageBinding mBinding;
    private AppViewModel mViewModel;
    private final DetailsRVAdapter mDetailsRVAdapter = new DetailsRVAdapter();
    private int mId;
    private boolean mIsActive = false;
    private Adventure mAdventure;
    private final Handler mHandler = new Handler();
    private Runnable mProgressUpdaterRunnable;



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param id Parameter 1
     * @return A new instance of fragment ScreenSlidePageFragment.
     */
    public static ScreenSlidePageFragment newInstance(int id) {
        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        Bundle args = new Bundle();
        args.putInt(ID, id);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mId = getArguments().getInt(ID);
        }
        if (savedInstanceState != null && savedInstanceState.containsKey("isActive")) {
            mIsActive = savedInstanceState.getBoolean("isActive");
        }

        mViewModel = new ViewModelProvider(this,
                new AppViewModelFactory( requireActivity().getApplication())  ).get(AppViewModel.class);

        mProgressUpdaterRunnable = new Runnable() {
            @Override
            public void run() {
                mViewModel.refresh( new Data.Builder() .putInt("id", mId)  .build());
                Log.e(TAG, "------>  run: mViewModel.refresh( new Data.Builder() .putInt(\"id\", mId)  .build());");
                mHandler.postDelayed(this, ACTIVE_PROGRESS_UPDATE_INTERVAL);  // 'this' is referencing the Runnable object
            }
        };
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentScreenSlidePageBinding.inflate( inflater, container, false);
        mBinding.activeSwitch.setCallBack(this);
        mBinding.detailsRv.setLayoutManager( new LinearLayoutManager(this.getContext(), RecyclerView.VERTICAL, false));
        mBinding.detailsRv.setAdapter(mDetailsRVAdapter);
        mBinding.titleTv.setMovementMethod( new ScrollingMovementMethod());
        return mBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel.getAdventureByIdLive( mId).observe( getViewLifecycleOwner(), adventure -> {
            mAdventure = new Adventure( adventure);
            // start transition (passive <-> active) if needed
            if (mIsActive != mAdventure.isActive()) {
                transition();
            }
            bind();
        });
    }




    /** elements of user interaction
     *      first:  progress slider
     *      second: target slider
     * */
    @Override
    public void onStart() {
        super.onStart();
        mBinding.progressSlider.addOnSliderTouchListener( new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                double priority = slider.getValue();
                mViewModel.refresh(new Data.Builder() .putInt("id", mId) .putDouble("priority", priority) .build());
            }
        });
        mBinding.progressSlider.addOnChangeListener( (slider, value, fromUser) -> {
            if (mAdventure != null) {
                mAdventure.setPriority((double) slider.getValue());
                bind();
            }
        });
        mBinding.progressSlider.setLabelFormatter( value -> "priority");


        mBinding.targetSlider.addOnSliderTouchListener( new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                int target = AdventureDisplay.targetFromSlider.apply(slider.getValue());
                Log.e(TAG, "onStopTrackingTouch: " + target);
                mViewModel.refresh(new Data.Builder() .putInt("id", mId) .putInt("target", target) .build());
            }
        });
        mBinding.targetSlider.addOnChangeListener( (slider, value, fromUser) -> {
            if (mAdventure != null) {
                mAdventure.setTarget(  AdventureDisplay.targetFromSlider.apply( slider.getValue()) );
                bind();
            }
        });
        mBinding.targetSlider.setLabelFormatter( value ->
                AdventureDisplay.targetFromSlider.apply( value) + "  min.");
    }









    /** Called when a transition is needed (e.g.: mAdventure has changed)
     */
    private void transition() {
        mIsActive = mAdventure.isActive();
        Log.e(TAG, "transition to " + mIsActive);
        if (mIsActive) {
            mBinding.root.setBackgroundColor( this.getResources().getColor(R.color.active_background, null));
            mHandler.postDelayed(mProgressUpdaterRunnable, ACTIVE_PROGRESS_UPDATE_INTERVAL);
        } else {
            mBinding.root.setBackgroundColor( this.getResources().getColor(R.color.passive_background, null));
            mHandler.removeCallbacks(mProgressUpdaterRunnable);
        }
    }


    /** Called whenever the ui should be updated (e.g.: mAdventure has changed) */
    private void bind() {

        if (mAdventure == null) {
            return;
        }

        AdventureDisplay mAdventureDisplay = new AdventureDisplay( mAdventure, this.getContext());

        mBinding.titleTv.setText(                    mAdventureDisplay.titleToSpannableStringBuilder());
        mBinding.tagTv.setText(                      mAdventureDisplay.tagToSpannableStringBuilder() );

        mBinding.priorityTv.setText(                 mAdventureDisplay.priorityToTv());
        mBinding.targetTv.setText(                   mAdventureDisplay.targetToTv());

        mBinding.progressBar.setProgressCompat(      mAdventure.getPriority().intValue(), true);
        mBinding.targetBar.setProgressCompat(        mAdventureDisplay.targetToBar(), true);

        mBinding.progressSlider.setValue(            mAdventure.getPriority().intValue());
        mBinding.targetSlider.setValue(              mAdventureDisplay.targetToSlider());

        mDetailsRVAdapter.submit(                    mAdventure.getDetails()  );

        int color = mAdventureDisplay.getColor();
        mBinding.titleTv.setTextColor( color);
        mBinding.tagTv.setTextColor( color);
        mBinding.priorityTv.setTextColor( color);
        mBinding.targetTv.setTextColor( color);
        mBinding.progressBar.setIndicatorColor( color);
        mBinding.targetBar.setIndicatorColor( color);
        mBinding.progressSlider.setThumbTintList( ColorStateList.valueOf( color));
        mBinding.targetSlider.setThumbTintList( ColorStateList.valueOf( color));
        mBinding.detailsRvEndBorder.setBackgroundColor( color);
        mDetailsRVAdapter.setColor( color);
    }








    /** MyController callBack:  triggering an active<->passive transition */
    @Override
    public void onTransitionSelected() {
        mViewModel.refreshAll( new Data.Builder() .putInt("id", mId) .putBoolean("active", !mIsActive) .build());
    }
    /** MyController callBack:  triggering a return to the OverviewFragment */
    @Override
    public void onOverviewSelected() {
        assert this.getParentFragment() != null;
        NavController navController = NavHostFragment.findNavController( this.getParentFragment());
        navController.navigateUp();
    }










    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "---> " + "onResume()   " + mIsActive);
        if (mIsActive) {
            mHandler.postDelayed(mProgressUpdaterRunnable, ACTIVE_PROGRESS_UPDATE_INTERVAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "---> " + "onPause()");
        mViewModel.refresh(null);
        mHandler.removeCallbacks(mProgressUpdaterRunnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean( "isActive", mIsActive);
    }
}