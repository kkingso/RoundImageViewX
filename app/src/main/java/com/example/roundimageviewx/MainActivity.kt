package com.example.roundimageviewx

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.roundimageview.RoundImageView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        image1.setType(RoundImageView.TYPE_ROUND)
            .setLeftTopCornerRadius(12)
            .setLeftBottomCornerRadius(20)
            .setRightBottomCornerRadius(5)
            .setRightTopCornerRadius(30)
            .setBorderWidth(10)
            .setBorderColor(Color.BLUE)
            .invalidate()

    }
}