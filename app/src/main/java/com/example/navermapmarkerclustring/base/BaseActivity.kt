package com.example.navermapmarkerclustring.base

import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<T: ViewBinding>(val bindingFactory:(LayoutInflater)->T): AppCompatActivity() {
    protected lateinit var binding:T
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        binding=bindingFactory(layoutInflater)
        setContentView(binding.root)
    }
}