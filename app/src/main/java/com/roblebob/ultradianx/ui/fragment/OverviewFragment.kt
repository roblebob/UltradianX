package com.roblebob.ultradianx.ui.fragment

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnFlingListener
import com.roblebob.ultradianx.R
import com.roblebob.ultradianx.databinding.FragmentOverviewBinding
import com.roblebob.ultradianx.repository.model.Adventure
import com.roblebob.ultradianx.repository.viewmodel.AppViewModel
import com.roblebob.ultradianx.ui.adapter.OverviewRVAdapter
import kotlin.math.abs
import kotlin.math.roundToInt

class OverviewFragment : Fragment(), OverviewRVAdapter.Callback {

    private val args: OverviewFragmentArgs  by navArgs()


    private var _binding: FragmentOverviewBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!


    private lateinit var dragHelper: ItemTouchHelper
    private lateinit var swipeHelper: ItemTouchHelper



    private val mViewModel: AppViewModel by viewModels { AppViewModel.Factory }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel.initialRun()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOverviewBinding.inflate(inflater, container, false)

        binding.recyclerView.adapter = OverviewRVAdapter(this)
        binding.recyclerView.layoutManager = GridLayoutManager(this.context, 1, RecyclerView.VERTICAL, false)
        binding.recyclerView.onFlingListener = object : OnFlingListener() {
            override fun onFling(velocityX: Int, velocityY: Int): Boolean {
                if (!binding.recyclerView.canScrollVertically(1) && velocityY > velocityX && velocityY > 0) {
                    (binding.recyclerView.adapter as OverviewRVAdapter).isExtended = true
                    Log.e(TAG, "---->   set to TRUE")
                    return true
                }
                if (binding.recyclerView.canScrollVertically(1) && (binding.recyclerView.adapter as OverviewRVAdapter).isExtended) {
                    (binding.recyclerView.adapter as OverviewRVAdapter).isExtended = false
                    Log.e(TAG, "---->   set to FALSE")
                    return true
                }
                return false
            }
        }
        mViewModel.adventureListLive.observe(viewLifecycleOwner) { adventureList: List<Adventure> ->
            (binding.recyclerView.adapter as OverviewRVAdapter).submit(adventureList)
        }
        binding.recyclerView.postDelayed({
            val position: Int = args.position
            (binding.recyclerView.layoutManager as GridLayoutManager).scrollToPosition(position)
        }, 100)
        binding.spiralClock.setup()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        swipeHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
            0
        ) {


            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = true

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position: Int = viewHolder.adapterPosition
                val id: Int = (binding.recyclerView.adapter as OverviewRVAdapter).getAdventureId(position)
                //mViewModel.deleteAdventure(id)
            }


            override fun onChildDraw(
                canvas: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val position: Int = viewHolder.adapterPosition
                val id: Int = (binding.recyclerView.adapter as OverviewRVAdapter).getAdventureId(position)


                Log.e(TAG, "$position ($id)    ${if (isCurrentlyActive) " " else "          " }   ${- (dX / 10).roundToInt()} ")

                super.onChildDraw(
                    canvas,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }



        })

        swipeHelper.attachToRecyclerView(binding.recyclerView)
    }



    override fun onResume() {
        super.onResume()
        binding.spiralClock.submit(null, null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    override fun onNewAdventureCreated(title: String) {
        mViewModel.addAdventure(Adventure.newAdventure(title).toData())
        (binding.recyclerView.adapter as OverviewRVAdapter).isExtended = false
    }

    companion object {
        val TAG: String = OverviewFragment::class.java.simpleName
    }






    override fun onItemClickListener(id: Int, position: Int) {
        val action: OverviewFragmentDirections.ActionOverviewFragmentToMainFragment =
            OverviewFragmentDirections.actionOverviewFragmentToMainFragment()
        action.position = position
        action.id = id
        val navController = NavHostFragment.findNavController(this)
        navController.navigate(action)
    }


}