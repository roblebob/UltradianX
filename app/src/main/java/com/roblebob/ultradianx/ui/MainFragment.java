package com.roblebob.ultradianx.ui;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roblebob.ultradianx.R;
import com.roblebob.ultradianx.databinding.FragmentMainBinding;
import com.roblebob.ultradianx.repository.Util;
import com.roblebob.ultradianx.repository.model.Adventure;
import com.roblebob.ultradianx.viewmodel.AppViewModel;
import com.roblebob.ultradianx.viewmodel.AppViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {
    public static final String TAG = MainFragment.class.getSimpleName();

    private AppViewModel mViewModel;
    private FragmentMainBinding binding;
    private FragmentStateAdapter pagerAdapter;
    public List<Adventure> mAdventureList = new ArrayList<>();
    public static MainFragment newInstance() {
        return new MainFragment();
    }



    SharedPreferences mSharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        mSharedPreferences = requireActivity().getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);

        pagerAdapter = new ScreenSlidePagerAdapter(this);
        binding.pager.setAdapter(pagerAdapter);



        AppViewModelFactory appViewModelFactory = new AppViewModelFactory(requireActivity().getApplication());
        mViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) appViewModelFactory).get(AppViewModel.class);
        mViewModel.start();
        mViewModel.getAdventureListLive().observe( getViewLifecycleOwner(), adventureList -> {
            mAdventureList = new ArrayList<>(adventureList);
            pagerAdapter.notifyDataSetChanged();
        });



        ViewGroup.LayoutParams params = binding.toOverviewButton.getLayoutParams();
        Bitmap thumbImage = ThumbnailUtils.extractThumbnail(
                BitmapFactory.decodeResource(getResources(), R.drawable.bcn_04),
                params.width, params.height);
        binding.toOverviewButton.setImageBitmap( thumbImage);
        binding.toOverviewButton.setOnClickListener(v -> {
            MainFragmentDirections.ActionMainFragmentToOverviewFragment action =
                    MainFragmentDirections.actionMainFragmentToOverviewFragment();
            action.setPosition(binding.pager.getCurrentItem());
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(action);
        });



        int position;
        if (false/* mSharedPreferences.getBoolean("active", false) */) {
            position = mSharedPreferences.getInt("pos", 0);
        } else {
            position = MainFragmentArgs.fromBundle(getArguments()).getPosition();
        }
        final boolean active = mSharedPreferences.getBoolean("active", false);

        Log.e(TAG, "=====> active=" + active + "   " + "pos=" + position);




        binding.pager.postDelayed(
                () -> binding.pager.setCurrentItem(position, false), 100);

        binding.activeSwitch.setBackgroundColor(
                active ? getResources().getColor(R.color.active)
                        : getResources().getColor(R.color.transparent)
        );
        binding.toOverviewButton.setVisibility( active ? View.GONE : View.VISIBLE);
        binding.pager.setUserInputEnabled( !active);
        binding.activeSwitch.setOnClickListener(v -> {

            mViewModel.remoteClockify( mAdventureList.get(binding.pager.getCurrentItem()).getTitle());
            mSharedPreferences.edit().putBoolean("active", !active).apply();
            mSharedPreferences.edit().putInt("pos", binding.pager.getCurrentItem()).apply();
            requireActivity().recreate();
        });





        return rootView;
    }






    @Override
    public void onDestroyView() {
        super.onDestroyView();
        saveState();
        binding = null;
        Log.e(TAG, "---> onDestroyView:");
    }

    @Override
    public void onStop() {
        super.onStop();
        saveState();
        Log.e(TAG, "---> onStop");
    }

    private void saveState() {
        int pos = binding.pager.getCurrentItem();
        mSharedPreferences.edit().putInt("pos", pos).apply();

        Log.e(TAG, "---> saveState:  pos = " + pos);
    }






    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    class ScreenSlidePagerAdapter extends FragmentStateAdapter {

        MainFragment mFragment;

        public ScreenSlidePagerAdapter(MainFragment f) {
            super(f);
            mFragment = f;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            ScreenSlidePageFragment screenSlidePageFragment = ScreenSlidePageFragment.newInstance(mAdventureList.get(position).toBundle());
            return screenSlidePageFragment;
        }

        @Override
        public int getItemCount() {
            return mFragment.mAdventureList.size();
        }
    }
}