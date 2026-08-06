package com.misaventuras.ui

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.*
import com.misaventuras.data.TodayTask

@Composable fun AdventureApp(vm:MainViewModel=hiltViewModel()){ val nav=rememberNavController(); val state by vm.state.collectAsStateWithLifecycle(); val primary=state.selected?.primaryColor?.let{Color(it.toULong())}?:Color(0xFF7656A8); MaterialTheme(colorScheme=lightColorScheme(primary=primary,secondary=Color(0xFFF5B5D2),background=Color(0xFFFFF8FC)),shapes=Shapes(large=RoundedCornerShape(28.dp),medium=RoundedCornerShape(20.dp))){NavHost(nav,"welcome"){composable("welcome"){WelcomeScreen(state,{vm.select(it);nav.navigate("home")})};composable("home"){HomeScreen(state,vm::toggle,{nav.navigate("welcome")},{nav.navigate("adult")})};composable("adult"){AdultScreen({n,p->vm.addTask(n,p)},{nav.popBackStack()})}}}}

@Composable fun WelcomeScreen(state:HomeState,onSelect:(Long)->Unit){Box(Modifier.fillMaxSize()){SoftBackground();Column(Modifier.fillMaxSize().padding(24.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center){Icon(Icons.Default.AutoAwesome,null,tint=MaterialTheme.colorScheme.primary,modifier=Modifier.size(72.dp));Text("Mis Aventuras",fontSize=36.sp,fontWeight=FontWeight.Bold);Text("¿Quién comienza su aventura?",modifier=Modifier.padding(12.dp));state.profiles.forEach{p->ElevatedButton(onClick={onSelect(p.id)},modifier=Modifier.fillMaxWidth().height(88.dp).padding(vertical=6.dp)){Icon(Icons.Default.Face,"Avatar de ${p.name}",Modifier.size(44.dp));Spacer(Modifier.width(16.dp));Text(p.name,fontSize=24.sp)}}}}}

@Composable fun HomeScreen(state:HomeState,onToggle:(TodayTask)->Unit,onSwitch:()->Unit,onAdult:()->Unit){Scaffold(topBar={TopAppBar(title={Text("¡Hola, ${state.selected?.name.orEmpty()}! 👋")},actions={IconButton(onClick=onSwitch,modifier=Modifier.semantics{contentDescription="Cambiar perfil"}){Icon(Icons.Default.SwitchAccount,null)};IconButton(onClick=onAdult,modifier=Modifier.semantics{contentDescription="Modo adulto"}){Icon(Icons.Default.Settings,null)}})}){pad->LazyColumn(Modifier.fillMaxSize().padding(pad).padding(horizontal=16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){item{Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp)){StatCard("⭐",state.points,"puntos",Modifier.weight(1f));StatCard("🚀",state.level,"nivel",Modifier.weight(1f));StatCard("🔥",0,"racha",Modifier.weight(1f))};val done=state.tasks.count{it.completion!=null};Text("Misiones de hoy · $done/${state.tasks.size}",fontSize=22.sp,fontWeight=FontWeight.Bold);LinearProgressIndicator(progress={if(state.tasks.isEmpty())0f else done.toFloat()/state.tasks.size},Modifier.fillMaxWidth().height(10.dp))};if(state.tasks.isEmpty())item{EmptyCard()}else items(state.tasks,key={it.task.id}){TaskCard(it){onToggle(it)}};item{Spacer(Modifier.height(32.dp))}}}}
@Composable private fun StatCard(emoji:String,value:Int,label:String,modifier:Modifier){ElevatedCard(modifier){Column(Modifier.padding(12.dp),horizontalAlignment=Alignment.CenterHorizontally){Text(emoji,fontSize=24.sp);Text("$value",fontWeight=FontWeight.Bold,fontSize=20.sp);Text(label)}}}
@Composable private fun TaskCard(item:TodayTask,onClick:()->Unit){val done=item.completion!=null;ElevatedCard(Modifier.fillMaxWidth()){Row(Modifier.padding(16.dp),verticalAlignment=Alignment.CenterVertically){Icon(if(done)Icons.Default.CheckCircle else Icons.Default.Star,if(done)"Misión cumplida" else "Misión pendiente",tint=if(done)Color(0xFF2E7D32)else MaterialTheme.colorScheme.primary,modifier=Modifier.size(44.dp));Column(Modifier.weight(1f).padding(horizontal=12.dp)){Text(item.task.name,fontSize=19.sp,fontWeight=FontWeight.SemiBold);Text("+${item.task.points} puntos")};FilledIconButton(onClick=onClick,modifier=Modifier.size(56.dp)){Icon(if(done)Icons.Default.Undo else Icons.Default.Check,if(done)"Deshacer" else "Completar")}}}}
@Composable private fun EmptyCard(){Card{Column(Modifier.fillMaxWidth().padding(28.dp),horizontalAlignment=Alignment.CenterHorizontally){Text("☁️",fontSize=46.sp);Text("¡Hoy no hay misiones!",fontSize=21.sp,fontWeight=FontWeight.Bold);Text("Podés descansar o elegir una aventura extra.")}}}
@Composable private fun SoftBackground(){Canvas(Modifier.fillMaxSize()){drawCircle(Color(0x22F48FB1),size.width*.45f,center=androidx.compose.ui.geometry.Offset(size.width*.1f,size.height*.15f));drawCircle(Color(0x2226C6DA),size.width*.55f,center=androidx.compose.ui.geometry.Offset(size.width*.9f,size.height*.8f))}}

@Composable fun AdultScreen(onAdd:(String,Int)->Unit,onBack:()->Unit){var name by remember{mutableStateOf("")};var points by remember{mutableStateOf("10")};Scaffold(topBar={TopAppBar(title={Text("Panel de adulto")},navigationIcon={IconButton(onClick=onBack){Icon(Icons.Default.ArrowBack,"Volver")}})}){pad->Column(Modifier.padding(pad).padding(20.dp),verticalArrangement=Arrangement.spacedBy(16.dp)){Text("Nueva tarea",fontSize=24.sp,fontWeight=FontWeight.Bold);OutlinedTextField(name,{name=it},label={Text("Nombre")},modifier=Modifier.fillMaxWidth());OutlinedTextField(points,{points=it.filter(Char::isDigit)},label={Text("Puntos")},modifier=Modifier.fillMaxWidth());Button(onClick={if(name.isNotBlank()){onAdd(name,points.toIntOrNull()?:10);name=""}},modifier=Modifier.fillMaxWidth().height(56.dp)){Text("Crear misión")};HorizontalDivider();Text("Objetivos, premios, estadísticas y respaldo",fontWeight=FontWeight.Bold);Text("Los accesos del panel están preparados para la ampliación del MVP. Toda eliminación importante requerirá confirmación.")}}}
@Preview(showBackground=true) @Composable private fun WelcomePreview(){MaterialTheme{WelcomeScreen(HomeState(),{})}}
