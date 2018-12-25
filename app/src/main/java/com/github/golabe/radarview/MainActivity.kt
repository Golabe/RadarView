package com.github.golabe.radarview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.github.golabe.radarview.library.RadarView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnStart.setOnClickListener {
            radarView.start()
        }
        btnStop.setOnClickListener {
            radarView.stop()
        }
        btnScanModel.setOnClickListener {
            radarView.setModel(RadarView.SCAN)
        }
        btnProgressModel.setOnClickListener {
            radarView.setModel(RadarView.PROGRESS)
        }
    }
}
