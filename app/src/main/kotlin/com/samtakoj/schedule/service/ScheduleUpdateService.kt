package com.samtakoj.schedule.service

import com.google.android.gms.gcm.GcmNetworkManager
import com.google.android.gms.gcm.GcmTaskService
import com.google.android.gms.gcm.TaskParams

/**
 * Created by artsiom.chuiko on 19/04/2017.
 */
class ScheduleUpdateService: GcmTaskService() {
    override fun onRunTask(p0: TaskParams?): Int {
        return GcmNetworkManager.RESULT_SUCCESS
    }
}