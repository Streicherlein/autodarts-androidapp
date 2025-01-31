package com.example.autodarts

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

class MainActivity : AppCompatActivity() {
        private var viewPager: ViewPager2? = null
        private var urls: MutableList<String>? = null
        private var url1: String = "https://play.autodarts.io" // Festgelegte URL

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            hideSystemUI()
            setContentView(R.layout.activity_main)

            val prefs: SharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            var url2 = prefs.getString(KEY_URL2, null)

            // Wenn url2 null ist, frage nach der URL
            if (url2 == null) {
                askForUrl2()  // Zeige den Dialog an, um die zweite URL einzugeben
            } else {
                // Wenn url2 bereits gesetzt ist, initialisiere den ViewPager mit den URLs
                setupViewPager(url1, url2)
            }
        }

        private fun askForUrl2() {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Gib die zweite URL ein")

            val view: View = layoutInflater.inflate(R.layout.dialog_urls, null)
            val input2 = view.findViewById<EditText>(R.id.url2)
            builder.setView(view)

            builder.setPositiveButton("Speichern") { dialog: DialogInterface?, which: Int ->
                val url2 = input2.text.toString().trim()
                if (url2.isNotEmpty()) {
                    val editor: SharedPreferences.Editor = getSharedPreferences(
                        PREFS_NAME,
                        Context.MODE_PRIVATE
                    ).edit()
                    editor.putString(KEY_URL2, url2)
                    editor.apply()

                    // Nach erfolgreichem Setzen von URL2, den ViewPager mit den URLs initialisieren
                    setupViewPager(url1, url2)
                } else {
                    Toast.makeText(this, "Die URL ist erforderlich!", Toast.LENGTH_SHORT).show()
                    askForUrl2()  // Fehlerhafte Eingabe erneut abfragen
                }
            }

            builder.setCancelable(false)
            builder.show()
        }

        private fun setupViewPager(url1: String, url2: String?) {
            viewPager = findViewById(R.id.viewPager) ?: return

            val urls = mutableListOf(url1)  // Beginne nur mit der ersten URL
            url2?.let { urls.add(it) }  // Wenn URL2 vorhanden ist, hinzufügen

            val adapter = WebPagerAdapter(this, urls)
            viewPager?.adapter = adapter

            // Listener für das Swipen zwischen den Seiten
            viewPager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    // Wenn die zweite Seite erreicht wird und URL2 noch nicht gesetzt ist, zeige den Dialog
                    if (position == 1 && url2 == null) {
                        askForUrl2()
                    }
                }
            })
        }

        private fun hideSystemUI() {
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
        }

        companion object {
            private const val PREFS_NAME = "WebPrefs"
            private const val KEY_URL2 = "url2"
        }
    }


