package com.samtakoj.schedule

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ListView
import com.samtakoj.schedule.view.TimeListViewAdapter
import com.samtakoj.shedule.model.TimeCsv
import com.samtakoj.shedule.model.TimeCsv_
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar


class TimeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val time = intent.extras.getSerializable("time") as TimeCsv
        val stopPosition = intent.extras.getInt("stopPosition")
        val stopName = intent.extras.getString("stopName")

        Log.i("TIMES_SCHEDULE", "Stop TIME: ${time.workDay}")

        val test = ArrayList<StopsActivity.MyTestModel>()
        var skipCount = time.intervalCount * stopPosition
        time.workDay.forEach {
            test.add(StopsActivity.MyTestModel(it, time.timeTable.subList(skipCount, skipCount + it.countInterval)))
            skipCount += it.countInterval
        }

        verticalLayout {
            toolbar {
                backgroundColor = Color.argb(255, 99, 196, 207)
                textView {
                    textColor = Color.WHITE
                    text = stopName
                }
            }
            listView {
                id = 312
            }
        }

        val test1 = test.map {
            it.timeTable.groupBy( { it / 60 }, { it % 60} )
        }.reduce { acc, map ->  map}

//        acc.plus(map)
        val listView = find<ListView>(312)
        val adapter = TimeListViewAdapter(this, test1)
        listView.adapter = adapter
        adapter.notifyDataSetChanged()

    }

    override fun onDestroy() {
        super.onDestroy()
    }
}