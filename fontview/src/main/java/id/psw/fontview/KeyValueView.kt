package id.psw.fontview

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.View.inflate
import android.widget.LinearLayout
import android.widget.TextView

class KeyValueView @JvmOverloads constructor(ctx: Context, style: AttributeSet? = null, defStyle:Int = 0) : LinearLayout(ctx, style, defStyle){
    private val baseView = inflate(ctx, R.layout.key_value_view, this)
    private val vKey = baseView.findViewById<TextView>(R.id.kvv_key)
    private val vVal = baseView.findViewById<TextView>(R.id.kvv_value)
    private val cColor = vVal.currentTextColor

    private fun checkSetIsUrl(value: String){
        val isLink =value.startsWith("http")
        vVal.setTextColor(if(isLink) Color.BLUE else cColor)

        if(isLink){
            vVal.paintFlags = vVal.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        }else{
            vVal.paintFlags = vVal.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
        }

        vVal.setOnClickListener {
            val act = context
            if(isLink && act is MainActivity) act.openLink(vVal.text.toString())
        }
    }

    fun set(key:String, value:String){
        vKey.text = key
        vVal.text = value
        checkSetIsUrl(value)
    }

    fun set(key:Int, value:String){
        vKey.text = context.getString(key)
        vVal.text = value
        checkSetIsUrl(value)
    }
}