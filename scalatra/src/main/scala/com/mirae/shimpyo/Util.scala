package com.mirae.shimpyo

import java.time.{LocalDate, Year}
import java.util.{Calendar, Date}
import java.time.temporal.TemporalAdjusters

object Util {
  def getDaysWithMonth(month:Int) = {
    val firstAdjuster = TemporalAdjusters.firstDayOfMonth
    val lastAdjuster = TemporalAdjusters.lastDayOfMonth

    // using adjuster for local date-time
    val localDate = LocalDate.of(Year.now.getValue, month, 1)
    val firstDayOfMonth = localDate.`with`(firstAdjuster)
    val lastDayOfMonth = localDate.`with`(lastAdjuster)

    List(convertLocalDateToDate(firstDayOfMonth), convertLocalDateToDate(lastDayOfMonth))
  }

  def getDaysWithNow() = {
    val firstAdjuster = TemporalAdjusters.firstDayOfMonth
    val lastAdjuster = TemporalAdjusters.lastDayOfMonth

    // using adjuster for local date-time
    val localDate = LocalDate.now()
    val firstDayOfMonth = localDate.`with`(firstAdjuster)
    val lastDayOfMonth = localDate.`with`(lastAdjuster)

    List(convertLocalDateToDate(firstDayOfMonth), convertLocalDateToDate(lastDayOfMonth))
  }

  def convertLocalDateToDate(localDate: LocalDate) = {
    import java.time.ZoneId

    val defaultZoneId = ZoneId.systemDefault
    val cal = Calendar.getInstance()
    cal.setTime(Date.from(localDate.atStartOfDay(defaultZoneId).toInstant()))
    cal.get(Calendar.DAY_OF_YEAR)
  }

}
