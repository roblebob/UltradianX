package com.roblebob.ultradianx.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.roblebob.ultradianx.ui.fragment.MainFragment;
import com.roblebob.ultradianx.ui.fragment.ScreenSlidePageFragment;

/**
 * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
 * sequence.
 */
public class ScreenSlidePagerAdapter extends FragmentStateAdapter {

    MainFragment mFragment;

    public ScreenSlidePagerAdapter(MainFragment f) {
        super(f);
        mFragment = f;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return ScreenSlidePageFragment.newInstance(mFragment.getAdventureIdList().get(position));
    }

    @Override
    public int getItemCount() {
        return mFragment.getAdventureIdList().size();
    }
}
