package com.roblebob.ultradianx.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roblebob.ultradianx.databinding.FragmentMainBinding;
import com.roblebob.ultradianx.ui.adapter.ListDiffCallback;
import com.roblebob.ultradianx.ui.extra.MyController;
import com.roblebob.ultradianx.ui.adapter.ScreenSlidePagerAdapter;
import com.roblebob.ultradianx.repository.viewmodel.AppViewModel;
import com.roblebob.ultradianx.repository.viewmodel.AppViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment implements MyController.OnCallbackListener {

    public static final String TAG = MainFragment.class.getSimpleName();

    private AppViewModel mViewModel;
    private FragmentMainBinding mBinding;
    private FragmentStateAdapter mPagerAdapter;

    private final List<Integer> mAdventureIdList = new ArrayList<>();
    public List<Integer> getAdventureIdList() { return mAdventureIdList; }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mBinding = FragmentMainBinding.inflate(inflater, container, false);

        mBinding.activeSwitch.setCallBack(this);


        mPagerAdapter = new ScreenSlidePagerAdapter(this);
        mBinding.pager.setAdapter(mPagerAdapter);

        AppViewModelFactory appViewModelFactory = new AppViewModelFactory(requireActivity().getApplication());
        mViewModel = new ViewModelProvider(this, appViewModelFactory).get(AppViewModel.class);

        mViewModel.getAppStateByKeyLive("initialRun").observe( getViewLifecycleOwner(), value -> {
            if (value == null) {
                mViewModel.initialRun();
            } else {
                Log.e(TAG, "----> initial: " + value);
            }
        });


        mViewModel.getAdventureIdListLive().observe( getViewLifecycleOwner(), adventureIdList -> {
            Log.d(TAG, "---> adventureIdList has changed");

            ListDiffCallback<Integer> listDiffCallback = new ListDiffCallback<>( mAdventureIdList, adventureIdList);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff( listDiffCallback);
            mAdventureIdList.clear();
            mAdventureIdList.addAll( adventureIdList);
            diffResult.dispatchUpdatesTo( mPagerAdapter);


        });


        int position  = MainFragmentArgs.fromBundle(requireArguments()).getPosition();
        mBinding.pager.postDelayed(
                () -> mBinding.pager.setCurrentItem(position, false), 100);

        return mBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }





    @Override
    public void onActivateSelected() {
        MainFragmentDirections.ActionMainFragmentToActiveAdventureFragment action =
                MainFragmentDirections.actionMainFragmentToActiveAdventureFragment();
        action.setPosition( mBinding.pager.getCurrentItem());
        action.setAdventureId( mAdventureIdList.get(mBinding.pager.getCurrentItem()));
        NavController navController = NavHostFragment.findNavController( this);
        navController.navigate(action);
    }

    @Override
    public void onOverviewSelected() {
        MainFragmentDirections.ActionMainFragmentToOverviewFragment action =
                MainFragmentDirections.actionMainFragmentToOverviewFragment();
        action.setPosition(mBinding.pager.getCurrentItem());
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(action);
    }
}