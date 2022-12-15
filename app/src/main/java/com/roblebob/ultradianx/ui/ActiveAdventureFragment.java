package com.roblebob.ultradianx.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.roblebob.ultradianx.R;
import com.roblebob.ultradianx.databinding.FragmentActiveAdventureBinding;
import com.roblebob.ultradianx.repository.model.Adventure;
import com.roblebob.ultradianx.repository.model.AdventureDao;
import com.roblebob.ultradianx.viewmodel.AppViewModel;
import com.roblebob.ultradianx.viewmodel.AppViewModelFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ActiveAdventureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActiveAdventureFragment extends Fragment {
    public static final String TAG = ActiveAdventureFragment.class.getSimpleName();


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ActiveAdventureFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ActiveAdventureFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ActiveAdventureFragment newInstance(String param1, String param2) {
        ActiveAdventureFragment fragment = new ActiveAdventureFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private AppViewModel mViewModel;
    private FragmentActiveAdventureBinding binding;

    private Adventure mAdventure;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentActiveAdventureBinding.inflate(inflater, container, false);

        String adventureTitle = ActiveAdventureFragmentArgs.fromBundle(getArguments()).getAdventureTitle();
        if (adventureTitle.isEmpty()) { Log.e(TAG, "!!!!!!!!!  adventure title is empty  !!!!!!!!!!!"); }


        AppViewModelFactory appViewModelFactory = new AppViewModelFactory(requireActivity().getApplication());
        mViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) appViewModelFactory).get(AppViewModel.class);


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
        binding.fragmentActiveAdventureTitleTv .setText( Html.fromHtml( mAdventure.getTitle()));
        binding.fragmentActiveAdventureTagsTv .setText( mAdventure.getTags().replace(' ', '\n'));
        binding.fragmentActiveAdventureProgressBar.setProgressCompat( mAdventure.getPriority().intValue(), false);

        //mDetailsRVAdapter.submit(mBundle.getStringArrayList("details"));
    }
}