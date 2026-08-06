package com.misaventuras.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misaventuras.data.*
import com.misaventuras.domain.GameLogic
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(val profiles:List<ChildProfileEntity> = emptyList(),val selected:ChildProfileEntity?=null,val tasks:List<TodayTask> = emptyList(),val points:Int=0,val level:Int=1)
@HiltViewModel class MainViewModel @Inject constructor(private val repository:AdventureRepository,private val settings:SettingsStore):ViewModel(){
 private val selectedId=MutableStateFlow<Long?>(null)
 val state:StateFlow<HomeState> = combine(repository.profiles(),selectedId.flatMapLatest{id->if(id==null)flowOf(emptyList()) else repository.today(id)},selectedId.flatMapLatest{id->if(id==null)flowOf(0)else repository.points(id)}){profiles,tasks,points->val chosen=profiles.firstOrNull{it.id==selectedId.value}?:profiles.firstOrNull(); HomeState(profiles,chosen,tasks,points,GameLogic.level(points))}.stateIn(viewModelScope,SharingStarted.WhileSubscribed(5_000),HomeState())
 init { viewModelScope.launch { selectedId.value=settings.selectedProfile.first() }; viewModelScope.launch { repository.profiles().filter{it.isNotEmpty()}.first().also{if(selectedId.value==null)selectedId.value=it.first().id} } }
 fun select(id:Long){selectedId.value=id;viewModelScope.launch{settings.select(id)}}
 fun toggle(item:TodayTask)=viewModelScope.launch{repository.toggle(item)}
 fun addTask(name:String,points:Int){state.value.selected?.let{p->viewModelScope.launch{repository.addTask(TaskEntity(profileId=p.id,name=name,points=points))}}}
}
