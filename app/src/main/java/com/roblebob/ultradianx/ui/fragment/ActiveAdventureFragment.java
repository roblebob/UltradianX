package com.roblebob.ultradianx.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.roblebob.ultradianx.repository.viewmodel.AppViewModel;
import com.roblebob.ultradianx.repository.viewmodel.AppViewModelFactory;


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
        final AppViewModel viewModel = new ViewModelProvider(this, appViewModelFactory).get(AppViewModel.class);


        binding.fragmentActiveAdventureDetailsRv.setLayoutManager( new LinearLayoutManager( this.getContext(), RecyclerView.VERTICAL, false));
        binding.fragmentActiveAdventureDetailsRv.setAdapter( mAdapter);


        final int adventureId = ActiveAdventureFragmentArgs.fromBundle(getArguments()).getAdventureId();
        viewModel.getAdventureByIdLive(adventureId).observe( getViewLifecycleOwner(), adventure -> {
            mAdventure = new Adventure( adventure);
            binding.fragmentActiveAdventureTitleTv .setText( Html.fromHtml( mAdventure.getTitle(), Html.FROM_HTML_MODE_COMPACT));
            binding.fragmentActiveAdventureTagsTv .setText( mAdventure.getTags().replace(' ', '\n'));
            binding.fragmentActiveAdventureProgressBar.setProgressCompat( mAdventure.getPriority().intValue(), false);
            mAdapter.submit( mAdventure.getDetails());
        });



        final Handler handler = new Handler();
        // Define the code block to be executed
        final Runnable progressUpdaterRunnable = new Runnable() {
            @Override
            public void run() {
                // Do something here on the main thread
                viewModel.updateActiveProgression(adventureId);
                Log.d("Handlers", "Called on main thread");
                // Repeat this the same runnable code block again another 2 seconds
                // 'this' is referencing the Runnable object
                handler.postDelayed(this, 2000);
            }
        };
        // Start the initial runnable task by posting through the handler
        handler.post(progressUpdaterRunnable);



        binding.fragmentActiveAdventurePassiveSwitch.setOnClickListener( (view) -> {

            handler.removeCallbacks( progressUpdaterRunnable);

            viewModel.updateActiveAdventure(mAdventure.toData());

            ActiveAdventureFragmentDirections .ActionActiveAdventureFragmentToMainFragment action =
                    ActiveAdventureFragmentDirections .actionActiveAdventureFragmentToMainFragment();
            action.setPosition( ActiveAdventureFragmentArgs.fromBundle( getArguments()).getPosition());
            NavController navController = NavHostFragment.findNavController( this);
            navController.navigate( action);
        });


        binding.fabAddDetail.setOnClickListener((v) -> {
            if (binding.textField.getVisibility() == View.VISIBLE) {
                binding.textField.setVisibility(View.INVISIBLE);
                
            } else if (binding.textField.getVisibility() == View.INVISIBLE || binding.textField.getVisibility() == View.GONE )  {
                binding.textField.setVisibility(View.VISIBLE);
            }
        });


        return binding.getRoot();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}