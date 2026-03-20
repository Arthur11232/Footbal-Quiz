package com.arthuralexandryan.footballquiz.utils

import com.arthuralexandryan.footballquiz.models.DHM
import java.util.Calendar
import java.util.GregorianCalendar

fun getDHM(year: Int, month: Int, dayOfMonth: Int): DHM {

    val current = GregorianCalendar()
    val future = GregorianCalendar(year, month - 1, dayOfMonth)
    var days = 0
    val hours: Int
    val minutes: Int

    if (isFutureTime(current, future)) {

        if (current.get(Calendar.MONTH) < future.get(Calendar.MONTH)) {
            days = current.getMaximum(Calendar.DATE) - current.get(Calendar.DATE) + future.get(Calendar.DATE)
        } else if (current.get(Calendar.MONTH) == future.get(Calendar.MONTH)) {
            days = future.get(Calendar.DATE) - current.get(Calendar.DATE)
        }

        hours = current.getMaximum(Calendar.HOUR) - current.get(Calendar.HOUR)
        minutes = current.getMaximum(Calendar.MINUTE) - current.get(Calendar.MINUTE)

    } else return DHM(-1, -1, -1, false)

    return DHM(days, hours, minutes, true)
}

fun isFutureTime(current: GregorianCalendar, future: GregorianCalendar): Boolean {
    if (current.get(Calendar.YEAR) > future.get(Calendar.YEAR)) return false
    if (current.get(Calendar.MONTH) > future.get(Calendar.MONTH)) return false
    else if (current.get(Calendar.MONTH) == future.get(Calendar.MONTH)) if (current.get(Calendar.DATE) > future.get(Calendar.DATE)) return false
    return true
}
