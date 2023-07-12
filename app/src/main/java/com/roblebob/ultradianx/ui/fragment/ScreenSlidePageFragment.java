package com.roblebob.ultradianx.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roblebob.ultradianx.databinding.FragmentScreenSlidePageBinding;
import com.roblebob.ultradianx.repository.model.Adventure;
import com.roblebob.ultradianx.ui.adapter.DetailsRVAdapter;
import com.roblebob.ultradianx.repository.viewmodel.AppViewModel;
import com.roblebob.ultradianx.repository.viewmodel.AppViewModelFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScreenSlidePageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScreenSlidePageFragment extends Fragment {
    public static final String TAG = ScreenSlidePageFragment.class.getSimpleName();
    private static final String ID = "id";
    public ScreenSlidePageFragment() { /* Required empty public constructor */ }
    private FragmentScreenSlidePageBinding mBinding;
    private AppViewModel mViewModel;
    private final DetailsRVAdapter mDetailsRVAdapter = new DetailsRVAdapter();
    private int mId;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param id Parameter 1
     * @return A new instance of fragment ScreenSlidePageFragment.
     */
    public static ScreenSlidePageFragment newInstance(int id) {
        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        Bundle args = new Bundle();
        args.putInt(ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mId = getArguments().getInt(ID);
        }
        mViewModel = new ViewModelProvider(this,
                new AppViewModelFactory( requireActivity().getApplication())
        ).get(AppViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentScreenSlidePageBinding.inflate( inflater, container, false);

        mBinding.fragmentScreenSlideDetailsRv.setLayoutManager( new LinearLayoutManager(this.getContext(), RecyclerView.VERTICAL, false));
        mBinding.fragmentScreenSlideDetailsRv.setAdapter(mDetailsRVAdapter);

        mViewModel.getAdventureByIdLive( mId).observe( getViewLifecycleOwner(), adventure -> {
            // TODO check if new assignment is necessary
            //Adventure adventure = new Adventure( advent);

            mBinding.fragmentScreenSlideTitleTv.setText( Html.fromHtml(         adventure.getTitle(),   Html.FROM_HTML_MODE_COMPACT));
            mBinding.fragmentScreenSlideTagsTv.setText(                         adventure.getTags()     .replace(" ", "\n"));
            mBinding.fragmentScreenSlidePageProgressBar.setProgressCompat(      adventure.getPriority() .intValue(), true);
            mDetailsRVAdapter.submit(                                           adventure.getDetails()  );
        });

        mBinding.fragmentScreenSlideTitleTv.setMovementMethod( new ScrollingMovementMethod());

        return mBinding.getRoot();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "---> " + "onPause()");
        mViewModel.updatePassiveAdventureList();
    }
}