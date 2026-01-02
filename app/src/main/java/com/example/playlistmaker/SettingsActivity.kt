package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {
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

        themeSwitch.isChecked = (applicationContext as App).darkTheme

        themeSwitch.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
        }

        val buttonShare = findViewById<MaterialTextView>(R.id.share)
        buttonShare.setOnClickListener {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT,
                    getString(R.string.share_url)
                )
                type = "text/plain"
            }
            val chooser = Intent.createChooser(shareIntent, getString(R.string.share_text))
            startActivity(chooser)
        }

        val buttonSupport = findViewById<MaterialTextView>(R.id.support)
        buttonSupport.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(
                    Intent.EXTRA_EMAIL,
                    arrayOf(getString(R.string.email))
                )
                putExtra(
                    Intent.EXTRA_SUBJECT,
                    getString(R.string.subject_email)

                )
                putExtra(
                    Intent.EXTRA_TEXT,
                    getString(R.string.text_email)
                )
            }
            startActivity(intent)
        }

        val buttonUserAgreement = findViewById<MaterialTextView>(R.id.user_agreement)
        buttonUserAgreement.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.user_agreement_url)))
            startActivity(intent)
        }

    }
}