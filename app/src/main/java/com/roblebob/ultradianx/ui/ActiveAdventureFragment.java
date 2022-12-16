package com.roblebob.ultradianx.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.roblebob.ultradianx.viewmodel.AppViewModel;
import com.roblebob.ultradianx.viewmodel.AppViewModelFactory;



public class ActiveAdventureFragment extends Fragment {
    public static final String TAG = ActiveAdventureFragment.class.getSimpleName();
    public ActiveAdventureFragment() { /* Required empty public constructor */ }
    private FragmentActiveAdventureBinding binding;
    private Adventure mAdventure;
    private final ActiveAdventureDetailsRVAdapter mAdapter = new ActiveAdventureDetailsRVAdapter();


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentActiveAdventureBinding.inflate(inflater, container, false);
        AppViewModelFactory appViewModelFactory = new AppViewModelFactory(requireActivity().getApplication());
        AppViewModel mViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) appViewModelFactory).get(AppViewModel.class);


        binding.fragmentActiveAdventureDetailsRv.setLayoutManager(
                new LinearLayoutManager( this.getContext(), RecyclerView.VERTICAL, false)
        );
        binding.fragmentActiveAdventureDetailsRv.setAdapter( mAdapter);


        String adventureTitle = ActiveAdventureFragmentArgs.fromBundle(getArguments()).getAdventureTitle();
        if (adventureTitle.isEmpty()) { Log.e(TAG, "!!!!!!!!!  adventure title is empty  !!!!!!!!!!!"); }
        mViewModel.getAdventureLive( adventureTitle).observe( getViewLifecycleOwner(), adventure -> {
            mAdventure = new Adventure( adventure);
            refresh();
        });


        binding.fragmentActiveAdventurePassiveSwitch.setOnClickListener( (view) -> {
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
}