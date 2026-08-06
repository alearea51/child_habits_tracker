package com.misaventuras.data

import androidx.room.*

enum class TrackingType { BOOLEAN, QUANTITY, STEPS, RATING, TIMER }
enum class TimeOfDay { ANY, MORNING, AFTERNOON, NIGHT }
enum class TransactionType { TASK, GOAL_BONUS, MANUAL, REDEMPTION, REVERSAL }
enum class GoalType { POINTS, COMPLETION_DAYS, TASK_COUNT, PERFECT_DAYS }
enum class GoalStatus { ACTIVE, COMPLETED, EXPIRED, ARCHIVED }

@Entity(tableName="profiles") data class ChildProfileEntity(@PrimaryKey val id: Long, val name: String, val avatarPath: String? = null, val primaryColor: Long, val secondaryColor: Long, val background: String = "clouds", val completionImagePath: String? = null)
@Entity(tableName="tasks", foreignKeys=[ForeignKey(entity=ChildProfileEntity::class,parentColumns=["id"],childColumns=["profileId"],onDelete=ForeignKey.CASCADE)], indices=[Index("profileId")])
data class TaskEntity(@PrimaryKey(autoGenerate=true) val id: Long=0, val profileId: Long, val name: String, val description: String?=null, val icon: String="star", val daysMask: Int=127, val oneOffDate: String?=null, val timeOfDay: TimeOfDay=TimeOfDay.ANY, val time: String?=null, val points: Int=10, val active: Boolean=true, val archived: Boolean=false, val createdAt: Long=System.currentTimeMillis(), val displayOrder: Int=0, val color: Long=0xFFFFD166, val trackingType: TrackingType=TrackingType.BOOLEAN, val targetQuantity: Int=1, val goalId: Long?=null)
@Entity(tableName="completions", foreignKeys=[ForeignKey(entity=TaskEntity::class,parentColumns=["id"],childColumns=["taskId"],onDelete=ForeignKey.CASCADE)], indices=[Index("taskId"),Index(value=["taskId","date"],unique=true)])
data class TaskCompletionEntity(@PrimaryKey(autoGenerate=true) val id:Long=0,val taskId:Long,val profileId:Long,val date:String,val quantity:Int,val completedAt:Long=System.currentTimeMillis())
@Entity(tableName="point_transactions", indices=[Index("profileId"),Index(value=["sourceType","sourceId"],unique=true)])
data class PointsTransactionEntity(@PrimaryKey(autoGenerate=true) val id:Long=0,val profileId:Long,val amount:Int,val type:TransactionType,val sourceType:String,val sourceId:String,val note:String,val createdAt:Long=System.currentTimeMillis())
@Entity(tableName="goals",indices=[Index("profileId")]) data class GoalEntity(@PrimaryKey(autoGenerate=true) val id:Long=0,val profileId:Long,val name:String,val description:String="",val type:GoalType,val requiredValue:Int,val startDate:String,val deadline:String?=null,val consecutive:Boolean=false,val bonusPoints:Int=0,val rewardId:Long?=null,val icon:String="trophy",val status:GoalStatus=GoalStatus.ACTIVE,val completedAt:Long?=null)
@Entity(tableName="goal_tasks",primaryKeys=["goalId","taskId"]) data class GoalTaskRelationEntity(val goalId:Long,val taskId:Long)
@Entity(tableName="rewards",indices=[Index("profileId")]) data class RewardEntity(@PrimaryKey(autoGenerate=true) val id:Long=0,val profileId:Long,val name:String,val description:String="",val imagePath:String?=null,val requiredPoints:Int=0,val quantity:Int?=null,val unlocked:Boolean=false,val claimed:Boolean=false,val unlockedAt:Long?=null,val claimedAt:Long?=null,val repeatable:Boolean=false,val redeemable:Boolean=false)
@Entity(tableName="redemptions",indices=[Index("rewardId")]) data class RewardRedemptionEntity(@PrimaryKey(autoGenerate=true) val id:Long=0,val rewardId:Long,val profileId:Long,val pointsSpent:Int,val requestedAt:Long=System.currentTimeMillis(),val confirmedAt:Long?=null)
@Entity(tableName="achievements") data class AchievementEntity(@PrimaryKey(autoGenerate=true) val id:Long=0,val profileId:Long,val title:String,val icon:String,val earnedAt:Long=System.currentTimeMillis())

class Converters { @TypeConverter fun tracking(v:String)=TrackingType.valueOf(v); @TypeConverter fun tracking(v:TrackingType)=v.name; @TypeConverter fun time(v:String)=TimeOfDay.valueOf(v); @TypeConverter fun time(v:TimeOfDay)=v.name; @TypeConverter fun transaction(v:String)=TransactionType.valueOf(v); @TypeConverter fun transaction(v:TransactionType)=v.name; @TypeConverter fun goalType(v:String)=GoalType.valueOf(v); @TypeConverter fun goalType(v:GoalType)=v.name; @TypeConverter fun goalStatus(v:String)=GoalStatus.valueOf(v); @TypeConverter fun goalStatus(v:GoalStatus)=v.name }
