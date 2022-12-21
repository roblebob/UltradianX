package com.roblebob.ultradianx.ui.fragment;

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

import android.util.Log;
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
    public static final String TAG = MainFragment.class.getSimpleName();

    private AppViewModel mViewModel;
    private FragmentMainBinding mBinding;
    private FragmentStateAdapter mPagerAdapter;

//    private List<Adventure> mAdventureList = new ArrayList<>();
//    public List<Adventure> getAdventureList() { return mAdventureList; }

    private List<Integer> mAdventureIdList = new ArrayList<>();
    public List<Integer> getAdventureIdList() { return mAdventureIdList; }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mBinding = FragmentMainBinding.inflate(inflater, container, false);

        mPagerAdapter = new ScreenSlidePagerAdapter(this);
        mBinding.pager.setAdapter(mPagerAdapter);

        AppViewModelFactory appViewModelFactory = new AppViewModelFactory(requireActivity().getApplication());
        mViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) appViewModelFactory).get(AppViewModel.class);

        mViewModel.getAppStateByKeyLive("initialRun").observe( getViewLifecycleOwner(), value -> {
            if (value == null) {
                mViewModel.initialRun();
            } else {
                Log.e(MainFragment.class.getSimpleName(), "----> initial: " + value);
            }
        });



        mViewModel.getAdventureIdListLive().observe( getViewLifecycleOwner(), adventureIdList -> {
            mAdventureIdList = new ArrayList<>(adventureIdList);
            mPagerAdapter.notifyDataSetChanged();

            //Log.e(TAG, "---> adventurelist has changed:\n" + UtilKt.adventureList2Titles(mAdventureList));
            Log.e(TAG, "---> adventureIdList has changed");
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
            action.setAdventureId( mAdventureIdList.get(mBinding.pager.getCurrentItem()));
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