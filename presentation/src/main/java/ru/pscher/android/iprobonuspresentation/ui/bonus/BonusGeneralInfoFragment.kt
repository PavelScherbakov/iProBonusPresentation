package ru.pscher.android.iprobonuspresentation.ui.bonus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.pscher.android.iprobonuspresentation.databinding.BonusGeneralInfoFragmentBinding
import ru.pscher.android.iprobonuspresentation.ui.shared.SharedViewModel
import ru.pscher.android.iprobonuspresentation.ui.showErrorDialog



class BonusGeneralInfoFragment : Fragment() {

    companion object {
        fun newInstance() = BonusGeneralInfoFragment()
    }

    private lateinit var viewModel: BonusGeneralInfoViewModel
    //private val authViewModel: SharedViewModel by activityViewModels()
    private lateinit var authViewModel: SharedViewModel

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val binding = BonusGeneralInfoFragmentBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this

        activity?.let {
            authViewModel = ViewModelProvider(it).get(SharedViewModel::class.java)
        }

        val viewModelFactory = BonusGeneralInfoViewModelFactory(authViewModel)
        viewModel = ViewModelProvider(this, viewModelFactory).get(BonusGeneralInfoViewModel::class.java)
        binding.viewModel = viewModel

        // Отображение ошибки
        viewModel.errorText.observe(viewLifecycleOwner,
            { errorText ->
                errorText?.let {
                    showErrorDialog(errorText)
                    viewModel.errorText.value = null
                }
            })

        // Отображение ошибки
        viewModel.systemErrorResId.observe(viewLifecycleOwner,
            { errorResId ->
                errorResId?.let {
                    showErrorDialog(it)
                    viewModel.systemErrorResId.value = null
                }
            })


        return binding.root
    }

}