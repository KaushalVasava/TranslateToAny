package com.lahsuak.translatetoany

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.util.*
import kotlin.collections.ArrayList


private const val TAG = "TAG"
class MainActivity : AppCompatActivity() {

    companion object {
        val langMap = HashMap<String,String>()
        var languages = mutableListOf<String>()
    }

    private val listOfLanguage = ArrayList<Language>()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //fetch saved recently played songs list
        val langArray = arrayOf(
            "af",
            "sq",
            "ar",
            "be",
            "bg",
            "bn",
            "ca",
            "zh",
            "hr",
            "cs",
            "da",
            "nl",
            "en",
            "eo",
            "et",
            "fi",
            "fr",
            "gl",
            "ka",
            "de",
            "el",
            "gu",
            "ht",
            "he",
            "hi",
            "hu",
            "is",
            "id",
            "ga",
            "it",
            "ja",
            "kn",
            "ko",
            "lt",
            "lv",
            "mk",
            "mr",
            "ms",
            "mt",
            "no",
            "fa",
            "pl",
            "pt",
            "ro",
            "ru",
            "sk",
            "sl",
            "es",
            "sv",
            "sw",
            "tl",
            "ta",
            "te",
            "th",
            "tr",
            "uk",
            "ur",
            "vi",
            "cy"
        )
        val lang  = arrayListOf<String>()
        for(index in langArray.indices){
            lang .add(index,Locale(langArray[index]).displayLanguage)
        }
        languages = lang.sorted().toMutableList()
        for(i in langArray.indices){
            langMap[languages[i]] = langArray[i]
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)

        val translateBtn = findViewById<Button>(R.id.btnTranslateLanguage)
        val editText = findViewById<EditText>(R.id.editTextLang)
        val pasteBtn = findViewById<ImageView>(R.id.pasteBtn)
        val addLanguage = findViewById<FloatingActionButton>(R.id.add_language)

        val preference = getSharedPreferences("SAVED", MODE_PRIVATE)
        val jsonString = preference.getString("language", null)
        val typeToken = object : TypeToken<ArrayList<Language>>() {}.type
        if (jsonString != null) {
            listOfLanguage.clear()
            val data1: ArrayList<Language> =
                GsonBuilder().create().fromJson(jsonString, typeToken)
            listOfLanguage.addAll(data1)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)

        if (listOfLanguage.size == 0) {
            listOfLanguage.add(Language("Hindi", ""))
            listOfLanguage.add(Language("Gujarati", ""))
        }
        var langAdapter = LanguageAdapter(this, listOfLanguage, editText.text.toString())
        recyclerView.adapter = langAdapter

        translateBtn.setOnClickListener {
            langAdapter = LanguageAdapter(this, listOfLanguage, editText.text.toString())
            recyclerView.adapter = langAdapter
            langAdapter.notifyDataSetChanged()
        }
        pasteBtn.setOnClickListener {
            editText.setText(pasteClicked())
        }


        addLanguage.setOnClickListener {
            var chL = ""
            //val lang = langArray.sortedArray()
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, languages)


            val dialog = Dialog(this)
            dialog.setContentView(R.layout.add_language_dialog)
            val langChoose = dialog.findViewById<Spinner>(R.id.dialog_spinner)
            val addBtn = dialog.findViewById<Button>(R.id.btn_add)
            val cancelBtn = dialog.findViewById<Button>(R.id.btn_cancel)
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            langChoose.adapter = adapter
            langChoose.setSelection(0)
            langChoose.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    item: Int,
                    id: Long
                ) {
                    chL = languages[item]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
            addBtn.setOnClickListener {
                listOfLanguage.add(Language(chL, ""))
                langAdapter.notifyDataSetChanged()
                dialog.dismiss()
            }
            cancelBtn.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }
    private fun pasteClicked():String{
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val abc = clipboard.primaryClip
        val item = abc?.getItemAt(0)
        return item!!.text.toString()
    }

    private fun storeLanguage(){
        val historyEditor = getSharedPreferences("SAVED", MODE_PRIVATE).edit()
        val json3 = GsonBuilder().create().toJson(listOfLanguage)
        historyEditor.putString("language", json3)
        historyEditor.apply()
    }
    override fun onPause() {
        super.onPause()
        //store recently played songs list into shared preference
        storeLanguage()
    }
}