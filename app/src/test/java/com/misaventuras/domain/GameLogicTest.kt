package com.misaventuras.domain

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class GameLogicTest {
 @Test fun `levels use configured thresholds`(){assertEquals(1,GameLogic.level(99));assertEquals(2,GameLogic.level(100));assertEquals(5,GameLogic.level(1200))}
 @Test fun `weekly schedule uses ISO weekday bits`(){val monday=LocalDate.of(2026,8,3);assertTrue(GameLogic.isScheduled(1,null,monday));assertFalse(GameLogic.isScheduled(1,null,monday.plusDays(1)))}
 @Test fun `one off task only appears on its date`(){val date=LocalDate.of(2026,8,6);assertTrue(GameLogic.isScheduled(0,date.toString(),date));assertFalse(GameLogic.isScheduled(127,date.toString(),date.plusDays(1)))}
 @Test fun `streak skips days without planned work`(){val d=LocalDate.of(2026,8,6);val days=listOf(DayResult(d,5,4),DayResult(d.minusDays(1),0,0),DayResult(d.minusDays(2),2,2));assertEquals(2,GameLogic.streak(days))}
 @Test fun `streak stops below eighty percent`(){val d=LocalDate.of(2026,8,6);assertEquals(1,GameLogic.streak(listOf(DayResult(d,5,5),DayResult(d.minusDays(1),5,3),DayResult(d.minusDays(2),5,5))))}
}
