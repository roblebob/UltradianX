package com.roblebob.ultradianx.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roblebob.ultradianx.databinding.FragmentOverviewBinding;
import com.roblebob.ultradianx.repository.model.Adventure;
import com.roblebob.ultradianx.ui.adapter.OverviewRVAdapter;
import com.roblebob.ultradianx.repository.viewmodel.AppViewModel;
import com.roblebob.ultradianx.repository.viewmodel.AppViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class OverviewFragment extends Fragment implements OverviewRVAdapter.ItemClickListener {
    public OverviewFragment() { /* Required empty public constructor */ }

    private FragmentOverviewBinding mBinding;
    private List<Adventure> mAdventureList = new ArrayList<>();
    private GridLayoutManager overviewRVLayoutManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentOverviewBinding.inflate(inflater, container, false);

        overviewRVLayoutManager = new GridLayoutManager( this.getContext(), 1, RecyclerView.VERTICAL, false);
        OverviewRVAdapter overviewRVAdapter = new OverviewRVAdapter(this);
        mBinding.fragmentOverviewRv.setAdapter( overviewRVAdapter);
        mBinding.fragmentOverviewRv.setLayoutManager( overviewRVLayoutManager);

        AppViewModelFactory appViewModelFactory = new AppViewModelFactory(requireActivity().getApplication());
        AppViewModel mViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) appViewModelFactory).get(AppViewModel.class);
        mViewModel.getAdventureListLive().observe( getViewLifecycleOwner(), adventureList -> {
            mAdventureList = new ArrayList<>(adventureList);
            overviewRVAdapter.submit(mAdventureList);
        });

        mBinding.fragmentOverviewRv.postDelayed( () -> {
            int position = OverviewFragmentArgs.fromBundle( getArguments())  .getPosition();
            overviewRVLayoutManager.scrollToPosition(position);
        }, 100);

        return mBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    public void onItemClickListener(Adventure adventure, Integer position) {

        OverviewFragmentDirections.ActionOverviewFragmentToMainFragment action = OverviewFragmentDirections.actionOverviewFragmentToMainFragment();
        action.setPosition(position);
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(action);
    }
}