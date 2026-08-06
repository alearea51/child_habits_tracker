package com.misaventuras.domain

import java.time.LocalDate

object GameLogic {
 val levels=sortedMapOf(1 to 0,2 to 100,3 to 250,4 to 500,5 to 1000)
 fun level(points:Int)=levels.filterValues { points>=it }.keys.maxOrNull()?:1
 fun isScheduled(daysMask:Int,oneOffDate:String?,date:LocalDate):Boolean = oneOffDate?.let { it==date.toString() } ?: (daysMask and (1 shl (date.dayOfWeek.value-1)) != 0)
 fun completionPercent(planned:Int,completed:Int)=if(planned==0) 0 else (completed*100/planned).coerceIn(0,100)
 fun streak(days:List<DayResult>,threshold:Int=80):Int { var count=0; for(day in days.sortedByDescending{it.date}) { if(day.planned==0) continue; if(completionPercent(day.planned,day.completed)<threshold) break; count++ }; return count }
}
data class DayResult(val date:LocalDate,val planned:Int,val completed:Int)
