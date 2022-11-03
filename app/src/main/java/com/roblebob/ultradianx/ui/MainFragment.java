package com.roblebob.ultradianx.ui;



import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roblebob.ultradianx.R;
import com.roblebob.ultradianx.databinding.FragmentMainBinding;
import com.roblebob.ultradianx.model.Adventure;
import com.roblebob.ultradianx.repository.AppViewModel;
import com.roblebob.ultradianx.repository.AppViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {
    public static final String TAG = MainFragment.class.getSimpleName();

    private AppViewModel mViewModel;
    private FragmentMainBinding binding;

    private FragmentStateAdapter pagerAdapter;

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
        });


        return rootView;
    }

    public List<Adventure> mAdventureList = new ArrayList<>();


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