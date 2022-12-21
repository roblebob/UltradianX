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

    private Instant t_start;


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


        String adventureTitle = ActiveAdventureFragmentArgs.fromBundle(getArguments()).getAdventureTitle();
        if (adventureTitle.isEmpty()) { Log.e(TAG, "!!!!!!!!!  adventure title is empty  !!!!!!!!!!!"); }
        mViewModel.getAdventureByTitleLive( adventureTitle).observe( getViewLifecycleOwner(), adventure -> {
            mAdventure = new Adventure( adventure);
            refresh();
        });



        binding.fragmentActiveAdventurePassiveSwitch.setOnClickListener( (view) -> {

            updateAdventure();
            t_start = null;

            ActiveAdventureFragmentDirections .ActionActiveAdventureFragmentToMainFragment action =
                    ActiveAdventureFragmentDirections .actionActiveAdventureFragmentToMainFragment();
            action.setPosition( ActiveAdventureFragmentArgs.fromBundle( getArguments()).getPosition());
            NavController navController = NavHostFragment.findNavController( this);
            navController.navigate( action);
        });



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
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (t_start != null) {
            outState.putString("t_start", t_start.toString());
        }

        super.onSaveInstanceState(outState);
    }




    public void updateAdventure() {
        //final double DECAY_RATE = 100.0 / (90.0 * 60.0) ;
        final double DECAY_RATE = 100.0 / (90.0) ;

        Instant oldLast = Instant.parse( mAdventure.getLast());
        Instant newLast = Instant.parse( UtilKt.getRidOfNanos( Instant.now().toString()));

        Duration duration = Duration.between(oldLast, newLast);

        double oldPriority = mAdventure.getPriority();
        double newPriority = oldPriority - ( duration.getSeconds() * DECAY_RATE);

        mViewModel.updateAdventure( mAdventure.getId(), newPriority, newLast.toString());
        mViewModel.remoteClockify( mAdventure.getTitle(), oldLast, newLast);
    }
}