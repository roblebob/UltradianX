package com.roblebob.ultradianx.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roblebob.ultradianx.R;
import com.roblebob.ultradianx.databinding.FragmentMainBinding;
import com.roblebob.ultradianx.repository.model.Adventure;
import com.roblebob.ultradianx.ui.adapter.ScreenSlidePagerAdapter;
import com.roblebob.ultradianx.viewmodel.AppViewModel;
import com.roblebob.ultradianx.viewmodel.AppViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {

    private AppViewModel mViewModel;
    private FragmentMainBinding mBinding;
    private FragmentStateAdapter mPagerAdapter;
    private List<Adventure> mAdventureList = new ArrayList<>();

    public List<Adventure> getAdventureList() { return mAdventureList; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mBinding = FragmentMainBinding.inflate(inflater, container, false);

        mPagerAdapter = new ScreenSlidePagerAdapter(this);
        mBinding.pager.setAdapter(mPagerAdapter);

        AppViewModelFactory appViewModelFactory = new AppViewModelFactory(requireActivity().getApplication());
        mViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) appViewModelFactory).get(AppViewModel.class);
        mViewModel.start();
        mViewModel.getAdventureListLive().observe( getViewLifecycleOwner(), adventureList -> {
            mAdventureList = new ArrayList<>(adventureList);
            mPagerAdapter.notifyDataSetChanged();
        });

        ViewGroup.LayoutParams params = mBinding.toOverviewButton.getLayoutParams();
        Bitmap thumbImage = ThumbnailUtils.extractThumbnail(
                BitmapFactory.decodeResource(getResources(), R.drawable.bcn_04),
                params.width, params.height);
        mBinding.toOverviewButton.setImageBitmap( thumbImage);
        mBinding.toOverviewButton.setOnClickListener(v -> {
            MainFragmentDirections.ActionMainFragmentToOverviewFragment action =
                    MainFragmentDirections.actionMainFragmentToOverviewFragment();
            action.setPosition(mBinding.pager.getCurrentItem());
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(action);
        });

        int position  = MainFragmentArgs.fromBundle(getArguments()).getPosition();
        mBinding.pager.postDelayed(
                () -> mBinding.pager.setCurrentItem(position, false), 100);

        mBinding.activeSwitch.setOnClickListener(v -> {

            MainFragmentDirections.ActionMainFragmentToActiveAdventureFragment action =
                    MainFragmentDirections.actionMainFragmentToActiveAdventureFragment();
            action.setPosition( mBinding.pager.getCurrentItem());
            action.setAdventureTitle( mAdventureList.get(mBinding.pager.getCurrentItem()).getTitle());
            NavController navController = NavHostFragment.findNavController( this);
            navController.navigate(action);
        });

        return mBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}