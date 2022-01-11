package com.lahsuak.translatetoany

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.lahsuak.translatetoany.MainActivity.Companion.langMap
import com.lahsuak.translatetoany.MainActivity.Companion.languages
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "Adapter"

class LanguageAdapter(
    private var context: Context,
    private var list: ArrayList<Language>,private var editText: String
) :
    RecyclerView.Adapter<LanguageAdapter.MyViewHolder>() {
    private var chL=""
    private val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1,languages)

    private fun findLangPosition(text: String): Int {
        var positionOfLang = 0
        for (pos in 0..languages.size-1) {
            if (languages[pos]== text) {
                positionOfLang = pos
            }
        }
        return positionOfLang
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtLang = view.findViewById<TextView>(R.id.lang_txt)
        var selectedLang = view.findViewById<Spinner>(R.id.spinner)
        var copybtn = view.findViewById<ImageView>(R.id.copyBtn)
        var deleteBtn = view.findViewById<ImageView>(R.id.deleteBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.language_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.selectedLang.adapter = adapter

        holder.copybtn.setOnClickListener {
            copyClicked(holder.txtLang.text.toString())
        }
        holder.deleteBtn.setOnClickListener {
            list.removeAt(position)
            notifyItemRemoved(position)
        }
        val getPos = findLangPosition(list[position].langName)

        holder.selectedLang.setSelection(getPos)
        holder.selectedLang.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                item: Int,
                id: Long
            ) {
                chL = langMap[languages[item]]!!
                //langCode[item]
                //languages[item]
                Log.d(TAG, "onItemSelected: #$chL ${Locale(chL).displayLanguage}")
                list[holder.adapterPosition].langName= Locale(chL).displayLanguage
                val options = TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.ENGLISH)
                    .setTargetLanguage(chL)
                    .build()
                val englishGujTranslator = Translation.getClient(options)
                val conditions = DownloadConditions.Builder()
                .requireWifi()
                .build()
                  englishGujTranslator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener {
                    englishGujTranslator.translate(editText)
                        .addOnSuccessListener { translatedText ->
                            // Translation successful.
                            holder.txtLang.text = translatedText
                            Log.d(TAG, " $translatedText")
                        }
                        .addOnFailureListener {
                            // Error.
                        }
                    // Model downloaded successfully. Okay to start translating.
                    // (Set a flag, unhide the translation UI, etc.)
                }
                .addOnFailureListener {
                    // Model couldn’t be downloaded or other internal error.
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    private fun copyClicked(text: String){
        val clipboard =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText("simple text", text)
        clipboard.setPrimaryClip(clip)
    }
}
