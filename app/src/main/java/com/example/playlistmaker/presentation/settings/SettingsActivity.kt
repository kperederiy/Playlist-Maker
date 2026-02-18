package com.example.playlistmaker.presentation.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.playlistmaker.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val rootView = findViewById<View>(R.id.root)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                top = systemBars.top,
                bottom = systemBars.bottom
            )
            insets
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val themeSwitch = findViewById<SwitchMaterial>(R.id.themeSwitch)

        viewModel.state.observe(this) { state ->

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

        val buttonShare = findViewById<MaterialTextView>(R.id.share)
        buttonShare.setOnClickListener {
            viewModel.onShareClicked()
        }

        val buttonSupport = findViewById<MaterialTextView>(R.id.support)
        buttonSupport.setOnClickListener {
            viewModel.onSupportClicked()
        }

        val buttonUserAgreement = findViewById<MaterialTextView>(R.id.user_agreement)
        buttonUserAgreement.setOnClickListener {
            viewModel.onUserAgreementClicked()
        }

    }
}