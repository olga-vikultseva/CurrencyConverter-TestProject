package com.example.currencyconverter.ui.converter

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.currencyconverter.databinding.ActivityConverterBinding
import com.example.currencyconverter.ui.converter.model.ConverterUiState
import com.example.currencyconverter.ui.currencypicker.CurrencyPickerDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConverterActivity : AppCompatActivity() {

    private val binding by lazy { ActivityConverterBinding.inflate(layoutInflater) }
    private val viewModel: ConverterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        with(binding) {
            originalValueLayout.setEndIconOnClickListener { displayCurrencyPickerDialog(true) }
            finalValueLayout.setEndIconOnClickListener { displayCurrencyPickerDialog(false) }

            originalValueEditText.setOnActionDoneListener(viewModel::onOriginalValueChanged)
            finalValueEditText.setOnActionDoneListener(viewModel::onFinalValueChanged)

            errorTryAgainButton.setOnClickListener { viewModel.onTryAgainClicked() }
        }

        viewModel.converterUiState.observe(this) { converterUiState ->
            when (converterUiState) {
                ConverterUiState.Loading -> with(binding) {
                    hideData()
                    hideError()
                    showLoading()
                }
                is ConverterUiState.Content -> with(binding) {
                    hideLoading()
                    hideError()
                    showData(converterUiState)
                }
                is ConverterUiState.Error -> with(binding) {
                    hideLoading()
                    hideData()
                    showError(converterUiState.message)
                }
            }
        }
    }

    private fun ActivityConverterBinding.showLoading() {
        progressIndicator.isVisible = true
    }

    private fun ActivityConverterBinding.hideLoading() {
        progressIndicator.isVisible = false
    }

    private fun ActivityConverterBinding.showData(content: ConverterUiState.Content) {
        originalValueLayout.suffixText = content.originalCurrencyCode
        originalValueEditText.updateValueEditText(content.originalValue)

        finalValueLayout.suffixText = content.finalCurrencyCode
        finalValueEditText.updateValueEditText(content.finalValue)

        converterDataGroup.isVisible = true
    }

    private fun ActivityConverterBinding.hideData() {
        converterDataGroup.isVisible = false
    }

    private fun ActivityConverterBinding.showError(message: String) {
        errorTextView.text = message
        converterErrorGroup.isVisible = true
    }

    private fun ActivityConverterBinding.hideError() {
        converterErrorGroup.isVisible = false
    }

    private fun displayCurrencyPickerDialog(isOriginal: Boolean) {
        CurrencyPickerDialogFragment.newInstance(isOriginal).show(supportFragmentManager, null)
    }

    private fun EditText.updateValueEditText(value: String) {
        if (text.toString() != value) {
            setText(value, TextView.BufferType.EDITABLE)
            if (hasFocus()) {
                setSelection(length())
            }
        }
    }

    private fun EditText.setOnActionDoneListener(action: (String) -> Unit) {
        setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                action.invoke(textView.text.toString())
            }
            false
        }
    }
}