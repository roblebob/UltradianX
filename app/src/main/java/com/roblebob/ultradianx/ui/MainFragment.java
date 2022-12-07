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




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        pagerAdapter = new ScreenSlidePagerAdapter(this);
        binding.pager.setAdapter(pagerAdapter);


        AppViewModelFactory appViewModelFactory = new AppViewModelFactory(requireActivity().getApplication());
        mViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) appViewModelFactory).get(AppViewModel.class);
        mViewModel.start();
        mViewModel.getAdventureListLive().observe( getViewLifecycleOwner(), adventureList -> {
            if (adventureList.size() > 0) {
                mAdventureList = new ArrayList<>(adventureList);
            } else {
                Log.e(TAG, "adventureList is empty");
            }
            pagerAdapter.notifyDataSetChanged();
        });



        ViewGroup.LayoutParams params = binding.toOverviewButton.getLayoutParams();
        Log.e(TAG, "-----> " + params.width + "  " + params.height);
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


        int position = MainFragmentArgs.fromBundle( getArguments())  .getPosition();
        binding.pager.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        binding.pager.setCurrentItem(position, false);
                    }
                }, 100
        );


        binding.activeSwitch.setOnClickListener(v -> {
            active = !active;
            refreshActive();
            Log.e(TAG, "------> " + active);
        });

        return rootView;
    }

    private boolean active = false;

    public void refreshActive() {
        if (active) {
            requireActivity().getApplication().setTheme(R.style.Theme_UltradianX_active);
            binding.toOverviewButton.setVisibility(View.GONE);
            binding.activeSwitch.setBackgroundColor(getResources().getColor(R.color.active));


        } else {
            requireActivity().getApplication().setTheme(R.style.Theme_UltradianX_passive);
            binding.toOverviewButton.setVisibility(View.VISIBLE);
            binding.activeSwitch.setBackgroundColor(getResources().getColor(R.color.transparent));
        }
    }








    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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