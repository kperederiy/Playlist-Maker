package com.example.playlistmaker.presentation.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_settings,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
        val themeSwitch = view.findViewById<SwitchMaterial>(R.id.themeSwitch)
        val buttonShare = view.findViewById<MaterialTextView>(R.id.share)
        val buttonSupport = view.findViewById<MaterialTextView>(R.id.support)
        val buttonUserAgreement = view.findViewById<MaterialTextView>(R.id.user_agreement)

        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->

            themeSwitch.isChecked = state.isDarkThemeEnabled

            if (state.shouldShareApp) {

                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    putExtra(Intent.EXTRA_TEXT, getString(R.string.share_url))
                    type = "text/plain"
                }

                startActivity(
                    Intent.createChooser(
                        shareIntent,
                        getString(R.string.share_text)
                    )
                )

                viewModel.onActionHandled()
            }

            if (state.shouldOpenSupport) {

                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email)))
                    putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject_email))
                    putExtra(Intent.EXTRA_TEXT, getString(R.string.text_email))
                }

                startActivity(intent)
                viewModel.onActionHandled()
            }

            if (state.shouldOpenUserAgreement) {

                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.user_agreement_url))
                    )
                )

                viewModel.onActionHandled()
            }
        }

        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onThemeSwitchClicked(isChecked)
        }

        buttonShare.setOnClickListener {
            viewModel.onShareClicked()
        }

        buttonSupport.setOnClickListener {
            viewModel.onSupportClicked()
        }

        buttonUserAgreement.setOnClickListener {
            viewModel.onUserAgreementClicked()
        }
    }
}