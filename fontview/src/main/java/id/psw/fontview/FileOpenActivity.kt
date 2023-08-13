package id.psw.fontview

import android.app.Activity
import android.content.Intent
import android.os.Bundle

class FileOpenActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val i = Intent(this, MainActivity::class.java)
        i.action = intent.action
        if(intent.categories != null){
            for(cat in intent.categories){
                i.addCategory(cat)
            }
        }
        i.data = intent.data
        i.putExtras(intent)
        startActivity(i)
        finish()
    }
}