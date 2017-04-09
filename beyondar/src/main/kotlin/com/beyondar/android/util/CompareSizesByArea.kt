package com.beyondar.android.util

import android.util.Size
import java.util.Comparator

/**
 * Created by artsiom.chuiko on 09/04/2017.
 */
class CompareSizesByArea: Comparator<Size> {
    override fun compare(lhs: Size, rhs: Size): Int {
        return java.lang.Long.signum(lhs.width.toLong() * lhs.height - rhs.width.toLong() * rhs.height)
    }
}