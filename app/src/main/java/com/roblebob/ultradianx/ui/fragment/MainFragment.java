package com.roblebob.ultradianx.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roblebob.ultradianx.databinding.FragmentMainBinding;
import com.roblebob.ultradianx.ui.adapter.ListDiffCallback;
import com.roblebob.ultradianx.ui.adapter.ScreenSlidePagerAdapter;
import com.roblebob.ultradianx.repository.viewmodel.AppViewModel;
import com.roblebob.ultradianx.repository.viewmodel.AppViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment  {

    public static final String TAG = MainFragment.class.getSimpleName();

    public FragmentMainBinding mBinding;
    private FragmentStateAdapter mPagerAdapter;

    private final List<Integer> mAdventureIdList = new ArrayList<>();
    public List<Integer> getAdventureIdList() { return mAdventureIdList; }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mBinding = FragmentMainBinding.inflate(inflater, container, false);

        mPagerAdapter = new ScreenSlidePagerAdapter(this);
        mBinding.pager.setAdapter(mPagerAdapter);

        AppViewModelFactory appViewModelFactory = new AppViewModelFactory(requireActivity().getApplication());
        AppViewModel mViewModel = new ViewModelProvider(this, appViewModelFactory).get(AppViewModel.class);



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
}