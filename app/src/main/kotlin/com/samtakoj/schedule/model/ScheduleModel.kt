package com.samtakoj.schedule.model

import com.samtakoj.schedule.data.RetrofitCsv.Position

data class Stop(@Position(1) val id: Int, @Position(4) val name: String, @Position(6) val lng: Long, @Position(7) val ltd: Long)
data class Stops(val stops: List<Stop>)