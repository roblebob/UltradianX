package com.roblebob.ultradianx.ui.fragment;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.slider.Slider;
import com.roblebob.ultradianx.databinding.FragmentActiveAdventureBinding;
import com.roblebob.ultradianx.repository.model.Adventure;
import com.roblebob.ultradianx.ui.adapter.ActiveAdventureDetailsRVAdapter;
import com.roblebob.ultradianx.repository.viewmodel.AppViewModel;
import com.roblebob.ultradianx.repository.viewmodel.AppViewModelFactory;
import com.roblebob.ultradianx.ui.extra.AdventureDisplay;


public class ActiveAdventureFragment extends Fragment {
    public static final String TAG = ActiveAdventureFragment.class.getSimpleName();
    public static final int PROGRESS_UPDATE_INTERVAL = 2000;
    public ActiveAdventureFragment() { /* Required empty public constructor */ }
    private FragmentActiveAdventureBinding binding;
    private Adventure mAdventure;
    private final ActiveAdventureDetailsRVAdapter mAdapter = new ActiveAdventureDetailsRVAdapter();
    private int mId;
    private AppViewModel viewModel;
    private final Handler handler = new Handler();
    private Runnable progressUpdaterRunnable;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mId = ActiveAdventureFragmentArgs.fromBundle(getArguments()).getAdventureId();

        AppViewModelFactory appViewModelFactory = new AppViewModelFactory(requireActivity().getApplication());
        viewModel = new ViewModelProvider(this, appViewModelFactory).get(AppViewModel.class);
        progressUpdaterRunnable = new Runnable() {
            @Override
            public void run() {
                viewModel.refresh( new Data.Builder() .putInt("id", mId)  .build());
                handler.postDelayed(this, PROGRESS_UPDATE_INTERVAL);  // 'this' is referencing the Runnable object
            }
        };
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentActiveAdventureBinding.inflate(inflater, container, false);
        binding.detailsRv.setLayoutManager( new LinearLayoutManager( this.getContext(), RecyclerView.VERTICAL, false));
        binding.detailsRv.setAdapter( mAdapter);

        viewModel.getAdventureByIdLive(mId).observe( getViewLifecycleOwner(), adventure -> {
            mAdventure = new Adventure( adventure);
            if (!mAdventure.isActive()) {
                Log.e(TAG, "---------->   adventure is not active");
                viewModel.refresh( new Data.Builder() .putInt("id", mId) .putBoolean("active", true) .build());
            } else {
                Log.e(TAG, "---------->   adventure is active");
                bindToAdventure( mAdventure);
            }
        });




        binding.passiveSwitch.setOnClickListener( (view) -> {
            mAdventure.setActive( false);
            viewModel.refresh(mAdventure.toData());

            ActiveAdventureFragmentDirections .ActionActiveAdventureFragmentToMainFragment action =
                    ActiveAdventureFragmentDirections .actionActiveAdventureFragmentToMainFragment();
            action.setPosition( ActiveAdventureFragmentArgs.fromBundle( getArguments()).getPosition());
            NavController navController = NavHostFragment.findNavController( this);
            navController.navigate( action);
        });




        binding.fabAddDetail.setOnClickListener((v) -> {
            if (binding.textInputField.getVisibility() == View.VISIBLE) {
                binding.textInputField.setVisibility(View.INVISIBLE);
                
            } else if (binding.textInputField.getVisibility() == View.INVISIBLE || binding.textInputField.getVisibility() == View.GONE )  {
                binding.textInputField.setVisibility(View.VISIBLE);
            }
        });



        binding.progressSlider.addOnSliderTouchListener( new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                double priority = slider.getValue();
                viewModel.refresh(new Data.Builder() .putInt("id", mId) .putDouble("priority", priority) .build());
            }
        });
        binding.progressSlider.addOnChangeListener( (slider, value, fromUser) -> {
            double priority = slider.getValue();
            mAdventure.setPriority( priority);
            bindToAdventure( mAdventure);
        });





        return binding.getRoot();
    }


    private void bindToAdventure(Adventure adventure) {
        AdventureDisplay adventureDisplay = new AdventureDisplay( adventure, getContext());
        binding.titleTv.setText( adventureDisplay.titleToSpannableStringBuilder());
        binding.tagTv.setText( adventureDisplay.tagToSpannableStringBuilder());
        binding.progressSlider.setValue( adventure.getPriority().floatValue());
        mAdapter.submit( adventure.getDetails());
    }






    @Override
    public void onResume() {
        super.onResume();
        handler.post(progressUpdaterRunnable);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(progressUpdaterRunnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}