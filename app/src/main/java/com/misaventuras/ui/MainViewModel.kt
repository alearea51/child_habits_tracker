package com.misaventuras.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misaventuras.data.*
import com.misaventuras.domain.DayResult
import com.misaventuras.domain.GameLogic
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val profiles: List<ChildProfileEntity> = emptyList(),
    val selected: ChildProfileEntity? = null,
    val tasks: List<TodayTask> = emptyList(),
    val points: Int = 0,
    val level: Int = 1,
    val streak: Int = 0,
    val goals: List<GoalEntity> = emptyList(),
    val rewards: List<RewardEntity> = emptyList(),
    val completions: List<TaskCompletionEntity> = emptyList(),
    val transactions: List<PointsTransactionEntity> = emptyList(),
)
private data class ExtraState(val goals:List<GoalEntity>,val rewards:List<RewardEntity>,val completions:List<TaskCompletionEntity>,val transactions:List<PointsTransactionEntity>)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: AdventureRepository,
    private val settings: SettingsStore,
    private val imageStore: ImageStore,
) : ViewModel() {
    private val selectedId = MutableStateFlow<Long?>(null)
    private fun <T> selectedFlow(empty: T, block: (Long) -> Flow<T>) =
        selectedId.flatMapLatest { it?.let(block) ?: flowOf(empty) }

    private val extras = combine(selectedFlow(emptyList(), repository::goals), selectedFlow(emptyList(), repository::rewards),
        selectedFlow(emptyList(), repository::completions), selectedFlow(emptyList(), repository::transactions), ::ExtraState)
    val state: StateFlow<HomeState> = combine(repository.profiles(), selectedFlow(emptyList()) { repository.today(it) },
        selectedFlow(0, repository::points), extras) { profiles, tasks, points, extra ->
        val completions = extra.completions
        val days = (0..30).map { LocalDate.now().minusDays(it.toLong()) }.map { date ->
            val planned = if (date == LocalDate.now()) tasks.size else completions.count { it.date == date.toString() }
            DayResult(date, planned, completions.count { it.date == date.toString() })
        }
        HomeState(profiles, profiles.firstOrNull { it.id == selectedId.value } ?: profiles.firstOrNull(), tasks,
            points, GameLogic.level(points), GameLogic.streak(days), extra.goals,
            extra.rewards, completions, extra.transactions)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeState())

    init {
        viewModelScope.launch { selectedId.value = settings.selectedProfile.first() }
        viewModelScope.launch { repository.profiles().filter { it.isNotEmpty() }.first().also { if (selectedId.value == null) selectedId.value = it.first().id } }
    }
    fun select(id: Long) { selectedId.value = id; viewModelScope.launch { settings.select(id) } }
    fun toggle(item: TodayTask) = viewModelScope.launch { repository.toggle(item) }
    fun addTask(name: String, points: Int, type: TrackingType, target: Int, days: Int, icon: String = "star", customIcon: Uri? = null) = state.value.selected?.let { p -> viewModelScope.launch {
        val storedIcon = customIcon?.let { runCatching { imageStore.copyOptimized(it, "task_${p.id}") }.getOrNull() } ?: icon
        repository.addTask(TaskEntity(profileId=p.id,name=name.trim(),points=points,trackingType=type,targetQuantity=target,daysMask=days,icon=storedIcon))
    } }
    fun updateProfile(name: String, primaryColor: Long, avatar: Uri?) = state.value.selected?.let { profile -> viewModelScope.launch {
        val storedAvatar = avatar?.let { runCatching { imageStore.copyOptimized(it, "avatar_${profile.id}") }.getOrNull() } ?: profile.avatarPath
        repository.saveProfile(profile.copy(name=name.trim().ifEmpty { profile.name },primaryColor=primaryColor,avatarPath=storedAvatar))
    } }
    fun addGoal(name: String, required: Int, bonus: Int) = state.value.selected?.let { p -> viewModelScope.launch { repository.addGoal(GoalEntity(profileId=p.id,name=name.trim(),type=GoalType.POINTS,requiredValue=required,startDate=LocalDate.now().toString(),bonusPoints=bonus)) } }
    fun addReward(name: String, required: Int) = state.value.selected?.let { p -> viewModelScope.launch { repository.addReward(RewardEntity(profileId=p.id,name=name.trim(),requiredPoints=required,redeemable=true)) } }
    fun redeem(reward: RewardEntity) = viewModelScope.launch { repository.redeem(reward, state.value.points) }
}
