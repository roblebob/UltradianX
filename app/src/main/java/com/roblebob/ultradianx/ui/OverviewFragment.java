package com.roblebob.ultradianx.ui;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.roblebob.ultradianx.R;
import com.roblebob.ultradianx.databinding.FragmentOverviewBinding;
import com.roblebob.ultradianx.repository.model.Adventure;
import com.roblebob.ultradianx.ui.adapter.OverviewRVAdapter;
import com.roblebob.ultradianx.viewmodel.AppViewModel;
import com.roblebob.ultradianx.viewmodel.AppViewModelFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OverviewFragment extends Fragment implements OverviewRVAdapter.ItemClickListener {
    public static final String TAG = OverviewFragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OverviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OverviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OverviewFragment newInstance(String param1, String param2) {
        OverviewFragment fragment = new OverviewFragment();
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

    private FragmentOverviewBinding binding;
    public List<Adventure> mAdventureList = new ArrayList<>();
    private AppViewModel mViewModel;

    private GridLayoutManager overviewRVLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOverviewBinding.inflate(inflater, container, false);


        overviewRVLayoutManager = new GridLayoutManager(
                this.getContext(),
                1,
                RecyclerView.VERTICAL,
                false
        );
        OverviewRVAdapter overviewRVAdapter = new OverviewRVAdapter(this);
        binding.fragmentOverviewRv.setAdapter( overviewRVAdapter);
        binding.fragmentOverviewRv.setLayoutManager( overviewRVLayoutManager);

        AppViewModelFactory appViewModelFactory = new AppViewModelFactory(requireActivity().getApplication());
        mViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) appViewModelFactory).get(AppViewModel.class);
        mViewModel.start();
        mViewModel.getAdventureListLive().observe( getViewLifecycleOwner(), adventureList -> {
            if (adventureList.size() > 0) {
                mAdventureList = new ArrayList<>(adventureList);
            } else {
                Log.e(TAG, "adventureList is empty");
            }
            overviewRVAdapter.submit(mAdventureList);
        });



        int position = OverviewFragmentArgs.fromBundle( getArguments())  .getPosition();
        binding.fragmentOverviewRv.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        overviewRVLayoutManager.scrollToPosition(position);
                    }
                }, 100
        );



        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClickListener(Adventure adventure, Integer position) {

        OverviewFragmentDirections.ActionOverviewFragmentToMainFragment action =
                OverviewFragmentDirections.actionOverviewFragmentToMainFragment();


        action.setPosition(position);
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(action);
    }
}