package com.example.weatherforecastapp.ui.favourite.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.weatherforecastapp.R
import com.example.weatherforecastapp.data.models.FavAddress
import com.example.weatherforecastapp.data.repository.Repo
import com.example.weatherforecastapp.data.source.local.LocalSource
import com.example.weatherforecastapp.data.source.local.LocalState
import com.example.weatherforecastapp.data.source.remote.RemoteSource
import com.example.weatherforecastapp.databinding.FragmentFavouriteBinding
import com.example.weatherforecastapp.ui.favourite.viewModel.FavouriteViewModel
import com.example.weatherforecastapp.ui.favourite.viewModel.FavouriteViewModelFactory
import com.example.weatherforecastapp.ui.home.view.HomeFragment
import com.example.weatherforecastapp.utils.Constants
import com.example.weatherforecastapp.utils.Extensions
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest


class FavouriteFragment : Fragment() {

    private var binding: FragmentFavouriteBinding? = null
    private lateinit var favViewModelFactory: FavouriteViewModelFactory
    private lateinit var favViewModel: FavouriteViewModel
    private lateinit var favAdapter: FavAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFavouriteBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        favAdapter = FavAdapter()
        setUpViewModel()
        favViewModel.getFavAddresses()
        observeData()
        onClicks()
    }

    private fun onClicks() {
        binding?.apply {
            btnAddFav.setOnClickListener {
                val action = FavouriteFragmentDirections.actionFavouriteFragmentToMapFragment("fav")
                findNavController().navigate(action)
            }

            swipeRefreshLayoutFav.setOnRefreshListener {
                favViewModel.getFavAddresses()
                swipeRefreshLayoutFav.isRefreshing = false
            }

            favAdapter.setOnItemFavClickListener(object : FavAdapter.SetOnItemFavClickListener {
                override fun onDeleteClickListener(favAddress: FavAddress) {
                    showDialog(favAddress)
                }

                override fun onItemFavClickListener(favAddress: FavAddress) {
                    Toast.makeText(
                        requireContext(),
                        favAddress.lat.toString().plus(favAddress.lon.toString()),
                        Toast.LENGTH_SHORT
                    ).show()
                    val isFromFavourite = true
                    val bundle = Bundle()
                    bundle.putDouble(Constants.LATITUDE, favAddress.lat)
                    bundle.putDouble(Constants.LONGITUDE, favAddress.lon)
                    bundle.putBoolean("fav", isFromFavourite)
                    val home = HomeFragment()
                    home.arguments = bundle
                    findNavController().navigate(
                        R.id.action_favouriteFragment_to_homeFragment,
                        bundle
                    )
//                    val action = FavouriteFragmentDirections.actionFavouriteFragmentToHomeFragment2(isFromFavourite, favAddress)
                }

            })

        }
    }

    private fun showDialog(favAddress: FavAddress) {
        Extensions.showConfirmationDialog(
            requireContext(), getString(R.string.sure_to_delet),
            //onYes
            {
                deleteItem(favAddress)
                favViewModel.getFavAddresses()
            },
            // on No
            {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.cance_delete),
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    private fun deleteItem(favAddress: FavAddress) {
        favViewModel.deletePlace(favAddress)
    }

    private fun setUpViewModel() {
        favViewModelFactory = FavouriteViewModelFactory(
            Repo.getRepoInstance(
                RemoteSource.getRemoteSourceInstance(),
                LocalSource.getLocalSourceInstance(),
            )
        )
        favViewModel =
            ViewModelProvider(
                requireActivity(),
                favViewModelFactory
            )[FavouriteViewModel::class.java]

    }

    private fun observeData() {
        lifecycleScope.launchWhenCreated {
            favViewModel.mutableStateFlow.collect {
                when (it) {
                    is LocalState.OnSuccess -> {
                        val list = it.list
                        favAdapter.setList(list)
                        binding?.apply {
                            swipeRefreshLayoutFav.visibility = View.VISIBLE
                            progressBarFav.visibility = View.GONE
                            recyclerFavPlaces.adapter = favAdapter
                        }
                    }
                    is LocalState.Loading -> {
                        // progress bar
                        binding?.apply {
                            swipeRefreshLayoutFav.visibility = View.GONE
                            progressBarFav.visibility = View.VISIBLE
                        }
                    }
                    else -> {
                        binding?.apply {
                            swipeRefreshLayoutFav.visibility = View.VISIBLE
                            progressBarFav.visibility = View.GONE
                        }

                        Toast.makeText(
                            requireContext(),
                            getString(R.string.failed_get_data),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}