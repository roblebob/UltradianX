package com.roblebob.ultradianx.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnFlingListener
import com.roblebob.ultradianx.databinding.FragmentOverviewBinding
import com.roblebob.ultradianx.repository.model.Adventure
import com.roblebob.ultradianx.repository.viewmodel.AppViewModel
import com.roblebob.ultradianx.repository.viewmodel.AppViewModelFactory
import com.roblebob.ultradianx.ui.adapter.OverviewRVAdapter

class OverviewFragment : Fragment(), OverviewRVAdapter.Callback {
    private var mBinding: FragmentOverviewBinding? = null
    private var mAdventureList: List<Adventure> = ArrayList()
    private var overviewRVLayoutManager: GridLayoutManager? = null
    private var mOverviewRVAdapter: OverviewRVAdapter? = null
    var mViewModel: AppViewModel? = null
    val args: OverviewFragmentArgs  by navArgs()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appViewModelFactory = AppViewModelFactory(requireActivity().application)
        mViewModel = ViewModelProvider(this, appViewModelFactory).get(AppViewModel::class.java)
        mViewModel!!.initialRun()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentOverviewBinding.inflate(inflater, container, false)
        overviewRVLayoutManager = GridLayoutManager(this.context, 1, RecyclerView.VERTICAL, false)
        mOverviewRVAdapter = OverviewRVAdapter(this)
        mBinding!!.recyclerView.adapter = mOverviewRVAdapter
        mBinding!!.recyclerView.layoutManager = overviewRVLayoutManager
        mBinding!!.recyclerView.onFlingListener = object : OnFlingListener() {
            override fun onFling(velocityX: Int, velocityY: Int): Boolean {
                if (!mBinding!!.recyclerView.canScrollVertically(1) && velocityY > velocityX && velocityY > 0) {
                    mOverviewRVAdapter!!.isExtended = true
                    Log.e(TAG, "---->   set to TRUE")
                    return true
                }
                if (mBinding!!.recyclerView.canScrollVertically(1) && mOverviewRVAdapter!!.isExtended) {
                    mOverviewRVAdapter!!.isExtended = false
                    Log.e(TAG, "---->   set to FALSE")
                    return true
                }
                return false
            }
        }
        mViewModel!!.adventureListLive.observe(viewLifecycleOwner) { adventureList: List<Adventure>? ->
            mAdventureList = ArrayList(adventureList)
            mOverviewRVAdapter!!.submit(mAdventureList)
        }
        mBinding!!.recyclerView.postDelayed({
            val position: Int = args.position
            overviewRVLayoutManager!!.scrollToPosition(position)
        }, 100)
        mBinding!!.spiralClock.setup()
        return mBinding!!.root
    }

    override fun onResume() {
        super.onResume()
        mBinding!!.spiralClock.submit(null, null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

    override fun onItemClickListener(adventure: Adventure, position: Int) {
        val action: OverviewFragmentDirections.ActionOverviewFragmentToMainFragment =
            OverviewFragmentDirections.actionOverviewFragmentToMainFragment()
        action.setPosition(position)
        val navController = NavHostFragment.findNavController(this)
        navController.navigate(action)
    }

    override fun onNewAdventureCreated(title: String) {
        mViewModel!!.addAdventure(Adventure.newAdventure(title).toData())
        mOverviewRVAdapter!!.isExtended = false
    }

    companion object {
        val TAG = OverviewFragment::class.java.simpleName
    }
}