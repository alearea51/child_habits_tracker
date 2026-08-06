package com.misaventuras.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao interface AdventureDao {
 @Query("SELECT * FROM profiles ORDER BY id") fun profiles():Flow<List<ChildProfileEntity>>
 @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun saveProfile(value:ChildProfileEntity)
 @Query("SELECT * FROM tasks WHERE profileId=:profileId AND active=1 AND archived=0 ORDER BY displayOrder,name") fun tasks(profileId:Long):Flow<List<TaskEntity>>
 @Insert suspend fun insertTask(value:TaskEntity):Long
 @Update suspend fun updateTask(value:TaskEntity)
 @Query("DELETE FROM tasks WHERE id=:id") suspend fun deleteTask(id:Long)
 @Query("SELECT * FROM completions WHERE profileId=:profileId") fun completions(profileId:Long):Flow<List<TaskCompletionEntity>>
 @Query("SELECT * FROM completions WHERE taskId=:taskId AND date=:date LIMIT 1") suspend fun completion(taskId:Long,date:String):TaskCompletionEntity?
 @Insert(onConflict=OnConflictStrategy.IGNORE) suspend fun insertCompletion(value:TaskCompletionEntity):Long
 @Query("DELETE FROM completions WHERE id=:id") suspend fun deleteCompletion(id:Long)
 @Insert(onConflict=OnConflictStrategy.IGNORE) suspend fun insertTransaction(value:PointsTransactionEntity):Long
 @Query("SELECT COALESCE(SUM(amount),0) FROM point_transactions WHERE profileId=:profileId") fun points(profileId:Long):Flow<Int>
 @Query("SELECT * FROM goals WHERE profileId=:profileId ORDER BY status,id DESC") fun goals(profileId:Long):Flow<List<GoalEntity>>
 @Insert suspend fun insertGoal(value:GoalEntity):Long
 @Query("SELECT * FROM rewards WHERE profileId=:profileId ORDER BY claimed,unlocked DESC") fun rewards(profileId:Long):Flow<List<RewardEntity>>
 @Insert suspend fun insertReward(value:RewardEntity):Long
 @Query("SELECT * FROM point_transactions ORDER BY createdAt") suspend fun allTransactions():List<PointsTransactionEntity>
 @Transaction suspend fun complete(task:TaskEntity,date:String,quantity:Int) { val id=insertCompletion(TaskCompletionEntity(taskId=task.id,profileId=task.profileId,date=date,quantity=quantity)); if(id>0 && quantity>=task.targetQuantity) insertTransaction(PointsTransactionEntity(profileId=task.profileId,amount=task.points,type=TransactionType.TASK,sourceType="completion",sourceId=id.toString(),note=task.name)) }
 @Transaction suspend fun undo(taskId:Long,date:String) { completion(taskId,date)?.let { deleteCompletion(it.id); insertTransaction(PointsTransactionEntity(profileId=it.profileId,amount=-transactionAmount(it.id),type=TransactionType.REVERSAL,sourceType="reversal",sourceId=it.id.toString(),note="Cumplimiento deshecho")) } }
 @Query("SELECT COALESCE(amount,0) FROM point_transactions WHERE sourceType='completion' AND sourceId=:id LIMIT 1") suspend fun transactionAmount(id:Long):Int
}

@Database(entities=[ChildProfileEntity::class,TaskEntity::class,TaskCompletionEntity::class,PointsTransactionEntity::class,GoalEntity::class,GoalTaskRelationEntity::class,RewardEntity::class,RewardRedemptionEntity::class,AchievementEntity::class],version=1,exportSchema=true)
@TypeConverters(Converters::class) abstract class AppDatabase:RoomDatabase(){ abstract fun dao():AdventureDao }
