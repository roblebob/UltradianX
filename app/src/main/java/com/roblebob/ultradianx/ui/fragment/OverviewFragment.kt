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
    private var _binding: FragmentOverviewBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!


    private var mAdventureList: List<Adventure> = ArrayList()
    private lateinit var overviewRVLayoutManager: GridLayoutManager
    private lateinit var mOverviewRVAdapter: OverviewRVAdapter
    private lateinit var mViewModel: AppViewModel
    val args: OverviewFragmentArgs  by navArgs()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appViewModelFactory = AppViewModelFactory(requireActivity().application)
        mViewModel = ViewModelProvider(this, appViewModelFactory).get(AppViewModel::class.java)
        mViewModel.initialRun()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOverviewBinding.inflate(inflater, container, false)
        overviewRVLayoutManager = GridLayoutManager(this.context, 1, RecyclerView.VERTICAL, false)
        mOverviewRVAdapter = OverviewRVAdapter(this)
        binding.recyclerView.adapter = mOverviewRVAdapter
        binding.recyclerView.layoutManager = overviewRVLayoutManager
        binding.recyclerView.onFlingListener = object : OnFlingListener() {
            override fun onFling(velocityX: Int, velocityY: Int): Boolean {
                if (!binding.recyclerView.canScrollVertically(1) && velocityY > velocityX && velocityY > 0) {
                    mOverviewRVAdapter!!.isExtended = true
                    Log.e(TAG, "---->   set to TRUE")
                    return true
                }
                if (binding.recyclerView.canScrollVertically(1) && mOverviewRVAdapter!!.isExtended) {
                    mOverviewRVAdapter!!.isExtended = false
                    Log.e(TAG, "---->   set to FALSE")
                    return true
                }
                return false
            }
        }
        mViewModel.adventureListLive.observe(viewLifecycleOwner) { adventureList: List<Adventure>? ->
            mAdventureList = ArrayList(adventureList)
            mOverviewRVAdapter!!.submit(mAdventureList)
        }
        binding.recyclerView.postDelayed({
            val position: Int = args.position
            overviewRVLayoutManager!!.scrollToPosition(position)
        }, 100)
        binding.spiralClock.setup()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.spiralClock.submit(null, null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClickListener(adventure: Adventure, position: Int) {
        val action: OverviewFragmentDirections.ActionOverviewFragmentToMainFragment =
            OverviewFragmentDirections.actionOverviewFragmentToMainFragment()
        action.setPosition(position)
        val navController = NavHostFragment.findNavController(this)
        navController.navigate(action)
    }

    override fun onNewAdventureCreated(title: String) {
        mViewModel.addAdventure(Adventure.newAdventure(title).toData())
        mOverviewRVAdapter.isExtended = false
    }

    companion object {
        val TAG = OverviewFragment::class.java.simpleName
    }
}