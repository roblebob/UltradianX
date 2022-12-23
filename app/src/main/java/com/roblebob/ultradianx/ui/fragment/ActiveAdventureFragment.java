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

import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roblebob.ultradianx.databinding.FragmentActiveAdventureBinding;
import com.roblebob.ultradianx.repository.model.Adventure;
import com.roblebob.ultradianx.ui.adapter.ActiveAdventureDetailsRVAdapter;
import com.roblebob.ultradianx.util.UtilKt;
import com.roblebob.ultradianx.viewmodel.AppViewModel;
import com.roblebob.ultradianx.viewmodel.AppViewModelFactory;


import java.time.Duration;
import java.time.Instant;


public class ActiveAdventureFragment extends Fragment {
    public static final String TAG = ActiveAdventureFragment.class.getSimpleName();
    public ActiveAdventureFragment() { /* Required empty public constructor */ }
    private FragmentActiveAdventureBinding binding;
    private Adventure mAdventure;
    private final ActiveAdventureDetailsRVAdapter mAdapter = new ActiveAdventureDetailsRVAdapter();
    private AppViewModel mViewModel;
    int mAdventureId;


    private Instant t_start;

    private final Handler mHandler = new Handler();
    // Define the code block to be executed
    private final Runnable mProgressUpdaterRunnable = new Runnable() {
        @Override
        public void run() {
            // Do something here on the main thread
            mViewModel.updateActiveProgression(mAdventureId);
            Log.d("Handlers", "Called on main thread");
            // Repeat this the same runnable code block again another 2 seconds
            // 'this' is referencing the Runnable object
            mHandler.postDelayed(this, 2000);
        }
    };



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null || savedInstanceState.getString("t_start") == null) {
            t_start = Instant.now();
        } else {
            t_start = Instant.parse(savedInstanceState.getString("t_start"));
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentActiveAdventureBinding.inflate(inflater, container, false);
        AppViewModelFactory appViewModelFactory = new AppViewModelFactory(requireActivity().getApplication());
        mViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) appViewModelFactory).get(AppViewModel.class);


        binding.fragmentActiveAdventureDetailsRv.setLayoutManager( new LinearLayoutManager( this.getContext(), RecyclerView.VERTICAL, false));
        binding.fragmentActiveAdventureDetailsRv.setAdapter( mAdapter);


        mAdventureId = ActiveAdventureFragmentArgs.fromBundle(getArguments()).getAdventureId();
        mViewModel.getAdventureByIdLive(mAdventureId).observe( getViewLifecycleOwner(), adventure -> {
            mAdventure = new Adventure( adventure);
            refresh();
        });



        binding.fragmentActiveAdventurePassiveSwitch.setOnClickListener( (view) -> {

            updateAdventure();
            t_start = null;

            // TODO kill updating

            ActiveAdventureFragmentDirections .ActionActiveAdventureFragmentToMainFragment action =
                    ActiveAdventureFragmentDirections .actionActiveAdventureFragmentToMainFragment();
            action.setPosition( ActiveAdventureFragmentArgs.fromBundle( getArguments()).getPosition());
            NavController navController = NavHostFragment.findNavController( this);
            navController.navigate( action);
        });




        // Start the initial runnable task by posting through the handler
        mHandler.post(mProgressUpdaterRunnable);



        return binding.getRoot();
    }


    public void refresh() {
        binding.fragmentActiveAdventureTitleTv .setText( Html.fromHtml( mAdventure.getTitle(), Html.FROM_HTML_MODE_COMPACT));
        binding.fragmentActiveAdventureTagsTv .setText( mAdventure.getTags().replace(' ', '\n'));
        binding.fragmentActiveAdventureProgressBar.setProgressCompat( mAdventure.getPriority().intValue(), false);
        mAdapter.submit( mAdventure.getDetails());
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        mHandler.removeCallbacks( mProgressUpdaterRunnable);
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (t_start != null) {
            outState.putString("t_start", t_start.toString());
        }
        super.onSaveInstanceState(outState);
    }




    private void updateAdventure() {
        //final double DECAY_RATE = 100.0 / (90.0 * 60.0) ;
        final double DECAY_RATE = 100.0 / (90.0) ;

        Instant oldLast = Instant.parse( mAdventure.getLast());
        Instant newLast = Instant.parse( UtilKt.getRidOfMillis( Instant.now().toString()));

        Duration duration = Duration.between(oldLast, newLast);

        double oldPriority = mAdventure.getPriority();
        double newPriority = oldPriority - ( duration.getSeconds() * DECAY_RATE);

        mViewModel.updateAdventure( mAdventure.getId(), newPriority, newLast.toString());
        mViewModel.remoteClockify( mAdventure.getTitle(), oldLast, newLast);

        Log.e(TAG, "priority changed by: " + (newPriority - oldPriority));
        if (Duration.between(oldLast, t_start).getSeconds() != 0) { Log.e(TAG, "----> " + "Inconsistency warning !!!!!!"); }
    }
}