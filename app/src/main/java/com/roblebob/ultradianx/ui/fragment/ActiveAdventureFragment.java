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
    public ActiveAdventureFragment() { /* Required empty public constructor */ }
    private FragmentActiveAdventureBinding binding;
    private Adventure mAdventure;
    private final ActiveAdventureDetailsRVAdapter mAdapter = new ActiveAdventureDetailsRVAdapter();


    int mId;

    AppViewModel viewModel;
    Handler handler = new Handler();
    Runnable progressUpdaterRunnable;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mId = ActiveAdventureFragmentArgs.fromBundle(getArguments()).getAdventureId();

        AppViewModelFactory appViewModelFactory = new AppViewModelFactory(requireActivity().getApplication());
        viewModel = new ViewModelProvider(this, appViewModelFactory).get(AppViewModel.class);
        progressUpdaterRunnable = new Runnable() {
            @Override
            public void run() {
                // Do something here on the main thread
                viewModel.refresh( new Data.Builder() .putInt("id", mId)  .build());
                Log.d("Handlers", "Called on main thread");
                // Repeat this the same runnable code block again another 2 seconds
                // 'this' is referencing the Runnable object
                handler.postDelayed(this, 2000);
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

                AdventureDisplay adventureDisplay = new AdventureDisplay( mAdventure, getContext());
                binding.titleTv .setText( adventureDisplay.titleToSpannableStringBuilder());
                binding.tagsTv .setText( adventureDisplay.tagToSpannableStringBuilder());
                float value =  mAdventure.getPriority().floatValue() ;
                binding.progressSlider.setValue( value);
                Log.e(TAG, "---------->   progressSlider value: " + value);
                mAdapter.submit( mAdventure.getDetails());
            }
        });




        // Define the code block to be executed

        // Start the initial runnable task by posting through the handler
        handler.post(progressUpdaterRunnable);










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
                Log.e(TAG, "onStopTrackingTouch: " + priority);
                viewModel.refresh(new Data.Builder() .putInt("id", mId) .putDouble("priority", priority) .build());
            }
        });

//        binding.progressSlider.addOnChangeListener( (slider, value, fromUser) -> {
//            double priority = slider.getValue() * 100.0;
//            Log.e(TAG, "onStopTrackingTouch: " + priority);
//            mAdventure.setPriority( priority);
//            viewModel.updateActiveAdventure(mAdventure.toData());
//        });





        return binding.getRoot();
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(progressUpdaterRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.post(progressUpdaterRunnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}