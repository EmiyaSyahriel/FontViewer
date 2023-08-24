package id.psw.fontview

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TabHost
import android.widget.TextView
import android.widget.Toast
import android.window.OnBackInvokedCallback
import android.window.OnBackInvokedDispatcher
import id.psw.fontview.MainActivity.Companion.RQ_REQUEST_OPEN
import net.java.dev.typecast.ot.TTFont
import java.io.File
import kotlin.concurrent.thread

class MainActivity : Activity() {
    private var font : Typeface? = null
    private lateinit var tab_details : View
    private lateinit var tab_testparam : View
    private lateinit var testText : TextView
    private var ttfInfo = TTFInfo()

    class ProgressWindow(val ctx: Activity){
        @SuppressLint("InflateParams")
        private val baseView : View = ctx.layoutInflater.inflate(R.layout.progress_view, null)
        private val dialog : AlertDialog

         init {
             val b = AlertDialog.Builder(ctx)
             b.setView(baseView)
             dialog = b.create()
             dialog.show()
         }

        fun setProgress(status:String, progressText:String, progress: Int)
        {
            ctx.runOnUiThread {
                baseView.findViewById<TextView>(R.id.pb_status).text = status
                baseView.findViewById<TextView>(R.id.pb_progress_text).text = progressText
                val pBar =baseView.findViewById<ProgressBar>(R.id.pb_progress)
                pBar.isIndeterminate = progress < 0
                pBar.progress = progress
            }
        }

        fun close(){
            dialog.dismiss()
        }
    }

    companion object {
        const val RQ_REQUEST_OPEN = 0x77F
    }

    private val _selector = object : OnItemSelectedListener {
        val typefaces = arrayOf(
            Typeface.NORMAL,
            Typeface.BOLD,
            Typeface.ITALIC,
            Typeface.BOLD_ITALIC
        )

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            testText.paint.typeface = Typeface.create(testText.paint.typeface, typefaces[position])
            testText.invalidate()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            // no way!
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        populateTab()
        bindViewCallbacks()

        if(intent.action == Intent.ACTION_VIEW && intent.data != null){
            loadFont(intent.data!!)
        } else {
            openFilePicker()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(OnBackInvokedDispatcher.PRIORITY_DEFAULT, object : OnBackInvokedCallback {
                override fun onBackInvoked() {
                    onBackPressed()
                }
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val m = menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onMenuItemSelected(featureId: Int, item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.menu_about -> {
                showAboutAlertDialog()
                true
            }
            R.id.menu_open -> {
                openFilePicker()
                true
            }
            else -> super.onMenuItemSelected(featureId, item)
        }
    }

    @SuppressLint("InflateParams") // It's fine
    private fun showAboutAlertDialog() {
        val vw = layoutInflater.inflate(R.layout.about_page, null)
        val rt = vw.findViewById<LinearLayout>(R.id.about_list)
        val entries = mapOf(
            R.string.about_main to "licenses/about.txt",
            R.string.about_license_self to "licenses/fontviewer.mit",
            R.string.about_license_typecast to "licenses/typecast.apache2"
        )

        for(entry in entries){
            val o = assets.open(entry.value)
            val txt = o.bufferedReader().readText()
            o.close()
            rt.addView(TextView(this).apply {
                setText(entry.key)
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24.0f)
                setPadding(20, 20, 20, 20)
                gravity = Gravity.END or Gravity.CENTER_VERTICAL
            })
            rt.addView(TextView(this).apply {
                text = txt
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14.0f)
            })
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.about_dlg_title)
            .setView(vw)
            .setNegativeButton(android.R.string.ok) { a, _ -> a.dismiss() }
            .show()
    }

    private fun bindViewCallbacks() {
        testText = findViewById(R.id.test_text)
        findViewById<Spinner>(R.id.item_style_selector).onItemSelectedListener = _selector
        findViewById<EditText>(R.id.test_text_item).addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                testText.text = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                testText.text = s.toString()
            }
        })

        val cText = findViewById<TextView>(R.id.size_selection_text)
        val skb = findViewById<SeekBar>(R.id.size_selection)

        skb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val p = progress + 8
                cText.text = getString(R.string.fmt_pt).format(p)
                testText.setTextSize(TypedValue.COMPLEX_UNIT_PT, p * 1.0f)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) { } // Unused
            override fun onStopTrackingTouch(seekBar: SeekBar?) { } // Unused
        })

        skb.progress = 14 - 8
    }

    @Suppress("DEPRECATION") // No Additional Dependency Please
    private fun populateTab(){
        val host = findViewById<TabHost>(R.id.tab_main)
        val dt = host.newTabSpec("tab0")
        val st = host.newTabSpec("tab1")

        host.setup()
        host.clearAllTabs()

        tab_details = layoutInflater.inflate(R.layout.tab_details, null)
        tab_testparam = layoutInflater.inflate(R.layout.tab_test_param, null)

        dt.setIndicator(getString(R.string.tab_detail))
        st.setIndicator(getString(R.string.tab_test_view))
        dt.setContent { tab_details }
        st.setContent { tab_testparam }

        host.addTab(st)
        host.addTab(dt)
        host.currentTab = 0
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == RQ_REQUEST_OPEN){
            val dd = data?.data
            if(dd != null){
                loadFontImpl(dd)
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun openFilePicker(){
        val intent =
            Intent(Intent.ACTION_OPEN_DOCUMENT)
            .apply {
                type = "font/ttf"
            }
        try {
            startActivityForResult(intent, RQ_REQUEST_OPEN)
        }catch(_:Exception){}
    }

    private fun loadFont(data: Uri) {
        thread(start = true, name = "Font Loader") {
            loadFontImpl(data)
        }
    }

    private fun onLoadFinished() {
        val l = tab_details.findViewById<LinearLayout>(R.id.details_root)
        l.removeAllViews()


        val keys = mapOf(
            TTFInfo.NK_FONT_FAMILY_NAME to R.string.detailskvp_font_family_name,
            TTFInfo.NK_FONT_SUBFAMILY_NAME to R.string.detailskvp_font_subfamily_name,
            TTFInfo.NK_UUID to R.string.detailskvp_uuid,
            TTFInfo.NK_FULL_FONT_NAME to R.string.detailskvp_full_font_name,
            TTFInfo.NK_VERSION to R.string.detailskvp_version,
            TTFInfo.NK_POST_SCRIPT_NAME to R.string.detailskvp_post_script_name,
            TTFInfo.NK_TRADEMARK to R.string.detailskvp_trademark,
            TTFInfo.NK_MANUFACTURER_NAME to R.string.detailskvp_manufacturer_name,
            TTFInfo.NK_DESIGNER_NAME to R.string.detailskvp_designer_name,
            TTFInfo.NK_URL_VENDOR to R.string.detailskvp_url_vendor,
            TTFInfo.NK_URL_DESIGNER to R.string.detailskvp_url_designer,
            TTFInfo.NK_TYPO_FAMILY_NAME to R.string.detailskvp_typo_family_name,
            TTFInfo.NK_TYPO_SUBFAMILY_NAME to R.string.detailskvp_typo_subfamily_name,
            TTFInfo.NK_MAC_COMPAT to R.string.detailskvp_mac_compat,
            TTFInfo.NK_SAMPLE_TEXT to R.string.detailskvp_sample_text,
            TTFInfo.NK_POSTSCRIPT_CID_NAME to R.string.detailskvp_postscript_cid_name,
            TTFInfo.NK_WWS_FAMILY_NAME to R.string.detailskvp_wws_family_name,
            TTFInfo.NK_WWS_SUBFAMILY_NAME to R.string.detailskvp_wws_subfamily_name,
            TTFInfo.NK_LIGHT_PALETTE to R.string.detailskvp_light_palette,
            TTFInfo.NK_DARK_PALETTE to R.string.detailskvp_dark_palette,
            TTFInfo.NK_VARIATION_POSTSCRIPT_NAME to R.string.detailskvp_variation_postscript_name,
            TTFInfo.NK_COPYRIGHT_NOTICE to R.string.detailkvp_copyright_notice,
            TTFInfo.NK_DESCRIPTION to R.string.detailskvp_description,
            TTFInfo.NK_LICENSE_DESCRIPTION to R.string.detailskvp_license_description,
            TTFInfo.NK_URL_LICENSE_INFO to R.string.detailskvp_url_license_info,
        )

        for (key in keys) {
            val kvv = KeyValueView(this)
            val info = ttfInfo[key.key]
            if (info.isNullOrEmpty()) continue
            kvv.set(key.value, info)
            l.addView(kvv)
        }
        l.requestLayout()

        testText.paint.typeface = font

        val k = ttfInfo[TTFInfo.NK_SAMPLE_TEXT]
        if (!k.isNullOrEmpty()) {
            findViewById<EditText>(R.id.test_text_item).setText(k)
            testText.text = k
            val num = testText.textSize
            testText.textSize = num + 1
            testText.text = k
            testText.textSize = num
            testText.text = k
        }else{
            val t = findViewById<EditText>(R.id.test_text_item).text
            testText.text = t
        }
        testText.invalidate()

        title = ttfInfo[TTFInfo.NK_FONT_FAMILY_NAME] ?: getString(R.string.app_name)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        finish()
    }

    private fun loadFontImpl(data: Uri){
        var prog : MainActivity.ProgressWindow? = null

        runOnUiThread {
            prog = ProgressWindow(this)
        }

        try {
            val cis = contentResolver.openInputStream(data)
            if (cis == null) {
                runOnUiThread {
                    Toast.makeText(this, getString(R.string.err_input_stream_invalid), Toast.LENGTH_SHORT).show()
                    prog?.close()
                    finish()
                }
                return
            }
            val tmp = File.createTempFile("kttfv", ".tmp", cacheDir) // IDK, Typeface only supports file and asset, not stream
            tmp.deleteOnExit()

            val ttfBytes = ByteArray(cis.available())
            cis.read(ttfBytes)

            val cos = tmp.outputStream()
            cos.write(ttfBytes)

            cos.flush()
            cos.close()
            cis.close()

            val ttf =  TTFont(ttfBytes, 0)

            for(i in 0 .. 25){
                prog?.setProgress(getString(R.string.progress_get_name_info), "${i+1}/25", ((i / 25.0f) * 100).toInt())
                ttfInfo.set(i, ttf.nameTable.getRecordString(i.toShort()))
            }

            font = Typeface.createFromFile(tmp)
        }catch(e:Exception){
            runOnUiThread {
                Toast.makeText(this, getString(R.string.err_font_failed_to_load).format(e.message), Toast.LENGTH_SHORT).show()
                prog?.close()
                finish()
            }
            return
        }

        runOnUiThread {
            prog?.close()
            onLoadFinished()
        }
    }

    fun toast(string: String){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
    }

    fun openLink(link: String) {
        try {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(link)
            startActivity(Intent.createChooser(i, getString(R.string.dlg_open_link)))
        } catch(e:Exception){
            toast(e.toString())
        }
    }
}