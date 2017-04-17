package com.samtakoj.schedule

import android.R
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.ListView
import com.samtakoj.schedule.model.StopCsv
import com.samtakoj.schedule.model.TestModel
import org.jetbrains.anko.*

/**
 * Created by Александр on 11.04.2017.
 */

class StopsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val testModel = intent.getSerializableExtra("test") as TestModel

        verticalLayout {
            padding = dip(5)
            textView {
                text = "${testModel.route.num}-${testModel.route.transportType}: ${testModel.route.name}"
            }
            listView {
                id = 321
            }
        }

        val arrStirng = ArrayList<String>()
        val listView = find<ListView>(321)
        val adapter = ArrayAdapter<String>(this,
                R.layout.simple_list_item_1,
                arrStirng)
        listView.adapter = adapter
        adapter.notifyDataSetChanged()

        for((index, stop) in testModel.stops.withIndex()) {
            adapter.add("${index + 1}: ${stop.name}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}