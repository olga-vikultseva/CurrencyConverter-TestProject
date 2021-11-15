package com.example.currencyconverter.ui.currencypicker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.currencyconverter.databinding.DialogFragmentCurrencyPickerBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CurrencyPickerDialogFragment : BottomSheetDialogFragment() {

    private var _binding: DialogFragmentCurrencyPickerBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var assistedFactory: CurrencyPickerViewModel.AssistedFactory

    private val viewModel: CurrencyPickerViewModel by viewModels {
        CurrencyPickerViewModel.provideFactory(
            assistedFactory,
            requireArguments().getBoolean(IS_ORIGINAL_CURRENCY_KEY)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = DialogFragmentCurrencyPickerBinding.inflate(inflater, container, false)
        .also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currencyAdapter = CurrencyAdapter { currencyCode ->
            viewModel.onCurrencyClicked(currencyCode)
            dismiss()
        }

        viewModel.currencyPickerItems.observe(viewLifecycleOwner) { currencyList ->
            currencyAdapter.currencyList = currencyList
        }

        binding.currencyList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = currencyAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val IS_ORIGINAL_CURRENCY_KEY = "IS_ORIGINAL_CURRENCY_KEY"

        fun newInstance(isOriginal: Boolean): CurrencyPickerDialogFragment =
            CurrencyPickerDialogFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(IS_ORIGINAL_CURRENCY_KEY, isOriginal)
                }
            }
    }
}