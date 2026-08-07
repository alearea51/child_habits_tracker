package com.misaventuras.data

import com.misaventuras.domain.GameLogic
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

data class TodayTask(val task:TaskEntity,val completion:TaskCompletionEntity?)
@Singleton class AdventureRepository @Inject constructor(private val dao:AdventureDao) {
 fun profiles()=dao.profiles(); fun points(id:Long)=dao.points(id); fun goals(id:Long)=dao.goals(id); fun rewards(id:Long)=dao.rewards(id); fun completions(id:Long)=dao.completions(id); fun transactions(id:Long)=dao.transactions(id)
 fun today(profileId:Long,date:LocalDate=LocalDate.now()):Flow<List<TodayTask>> = combine(dao.tasks(profileId),dao.completions(profileId)){tasks,done -> tasks.filter{GameLogic.isScheduled(it.daysMask,it.oneOffDate,date)}.map{ t->TodayTask(t,done.firstOrNull{it.taskId==t.id&&it.date==date.toString()})}}
 suspend fun toggle(item:TodayTask,date:LocalDate=LocalDate.now()) { if(item.completion==null) dao.complete(item.task,date.toString(),item.task.targetQuantity) else dao.undo(item.task.id,date.toString()) }
 suspend fun addTask(task:TaskEntity)=dao.insertTask(task)
 suspend fun saveProfile(profile:ChildProfileEntity)=dao.saveProfile(profile)
 suspend fun addGoal(goal:GoalEntity)=dao.insertGoal(goal)
 suspend fun addReward(reward:RewardEntity)=dao.insertReward(reward)
 suspend fun redeem(reward:RewardEntity,points:Int)=dao.redeem(reward,points)
 suspend fun seed() { dao.saveProfile(ChildProfileEntity(1,"Lola",primaryColor=0xFF7656A8,secondaryColor=0xFFF5B5D2)); dao.saveProfile(ChildProfileEntity(2,"Olivia",primaryColor=0xFF168C88,secondaryColor=0xFFFFD876)); if(dao.insertTask(TaskEntity(profileId=1,name="Leer 15 minutos",icon="book",points=15))>0) { dao.insertTask(TaskEntity(profileId=1,name="Preparar la mochila",icon="star",points=10)); dao.insertTask(TaskEntity(profileId=2,name="Ordenar los juguetes",icon="heart",points=10)); dao.insertTask(TaskEntity(profileId=2,name="Cepillarse los dientes",icon="smile",points=10)) } }
}
