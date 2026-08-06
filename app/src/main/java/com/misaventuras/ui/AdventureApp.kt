package com.misaventuras.ui

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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.misaventuras.data.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable fun AdventureApp(vm:MainViewModel=hiltViewModel()) {
    val nav=rememberNavController(); val state by vm.state.collectAsStateWithLifecycle()
    val primary=state.selected?.primaryColor?.let{Color(it.toInt())}?:Color(0xFF7656A8)
    MaterialTheme(colorScheme=lightColorScheme(primary=primary,secondary=Color(0xFFF5B5D2),background=Color(0xFFFFF8FC)),shapes=Shapes(large=RoundedCornerShape(28.dp),medium=RoundedCornerShape(20.dp))) {
        NavHost(nav,"welcome") {
            composable("welcome"){WelcomeScreen(state){vm.select(it);nav.navigate("home")}}
            composable("home"){HomeScreen(state,vm::toggle,{nav.navigate("welcome")}){nav.navigate(it)}}
            composable("adult"){AdultScreen(nav,state,vm)}
            composable("calendar"){CalendarScreen(nav,state)}
            composable("goals"){GoalsScreen(nav,state,vm)}
            composable("rewards"){RewardsScreen(nav,state,vm)}
            composable("stats"){StatsScreen(nav,state)}
            composable("settings"){SettingsScreen(nav)}
        }
    }
}

@Composable fun WelcomeScreen(state:HomeState,onSelect:(Long)->Unit){Box(Modifier.fillMaxSize()){SoftBackground();Column(Modifier.fillMaxSize().padding(24.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center){Icon(Icons.Default.AutoAwesome,null,tint=MaterialTheme.colorScheme.primary,modifier=Modifier.size(72.dp));Text("Mis Aventuras",fontSize=36.sp,fontWeight=FontWeight.Bold);Text("¿Quién comienza su aventura?",modifier=Modifier.padding(12.dp));state.profiles.forEach{p->ElevatedButton(onClick={onSelect(p.id)},modifier=Modifier.fillMaxWidth().height(88.dp).padding(vertical=6.dp)){Icon(Icons.Default.Face,"Avatar de ${p.name}",Modifier.size(44.dp));Spacer(Modifier.width(16.dp));Text(p.name,fontSize=24.sp)}}}}}

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun HomeScreen(state:HomeState,onToggle:(TodayTask)->Unit,onSwitch:()->Unit,onNavigate:(String)->Unit){Scaffold(topBar={TopAppBar(title={Text("¡Hola, ${state.selected?.name.orEmpty()}! 👋")},actions={IconButton(onClick=onSwitch,modifier=Modifier.semantics{contentDescription="Cambiar perfil"}){Icon(Icons.Default.SwitchAccount,null)};IconButton(onClick={onNavigate("adult")}){Icon(Icons.Default.AdminPanelSettings,"Modo adulto")}})},bottomBar={NavigationBar{listOf(Triple("home",Icons.Default.Home,"Hoy"),Triple("calendar",Icons.Default.CalendarMonth,"Calendario"),Triple("goals",Icons.Default.EmojiEvents,"Objetivos"),Triple("rewards",Icons.Default.CardGiftcard,"Premios")).forEach{(route,icon,label)->NavigationBarItem(selected=route=="home",onClick={onNavigate(route)},icon={Icon(icon,label)},label={Text(label)})}}}){pad->LazyColumn(Modifier.fillMaxSize().padding(pad).padding(horizontal=16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){item{Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp)){StatCard("⭐",state.points,"puntos",Modifier.weight(1f));StatCard("🚀",state.level,"nivel",Modifier.weight(1f));StatCard("🔥",state.streak,"racha",Modifier.weight(1f))};val done=state.tasks.count{it.completion!=null};Text("Misiones de hoy · $done/${state.tasks.size}",fontSize=22.sp,fontWeight=FontWeight.Bold);LinearProgressIndicator(progress={if(state.tasks.isEmpty())0f else done.toFloat()/state.tasks.size},Modifier.fillMaxWidth().height(10.dp))};if(state.tasks.isEmpty())item{EmptyCard()}else items(state.tasks,key={it.task.id}){TaskCard(it){onToggle(it)}};item{Spacer(Modifier.height(16.dp))}}}}
@Composable private fun StatCard(emoji:String,value:Int,label:String,modifier:Modifier){ElevatedCard(modifier){Column(Modifier.padding(12.dp),horizontalAlignment=Alignment.CenterHorizontally){Text(emoji,fontSize=24.sp);Text("$value",fontWeight=FontWeight.Bold,fontSize=20.sp);Text(label)}}}
@Composable private fun TaskCard(item:TodayTask,onClick:()->Unit){val done=item.completion!=null;ElevatedCard(Modifier.fillMaxWidth()){Row(Modifier.padding(16.dp),verticalAlignment=Alignment.CenterVertically){Icon(if(done)Icons.Default.CheckCircle else Icons.Default.Star,if(done)"Misión cumplida" else "Misión pendiente",tint=if(done)Color(0xFF2E7D32)else MaterialTheme.colorScheme.primary,modifier=Modifier.size(44.dp));Column(Modifier.weight(1f).padding(horizontal=12.dp)){Text(item.task.name,fontSize=19.sp,fontWeight=FontWeight.SemiBold);Text("+${item.task.points} puntos · ${item.task.trackingType.name.lowercase()}")};FilledIconButton(onClick=onClick,modifier=Modifier.size(56.dp)){Icon(if(done)Icons.Default.Undo else Icons.Default.Check,if(done)"Deshacer" else "Completar")}}}}
@Composable private fun EmptyCard(){Card{Column(Modifier.fillMaxWidth().padding(28.dp),horizontalAlignment=Alignment.CenterHorizontally){Text("☁️",fontSize=46.sp);Text("¡Hoy no hay misiones!",fontSize=21.sp,fontWeight=FontWeight.Bold);Text("Podés descansar o elegir una aventura extra.")}}}
@Composable private fun SoftBackground(){Canvas(Modifier.fillMaxSize()){drawCircle(Color(0x22F48FB1),size.width*.45f,center=androidx.compose.ui.geometry.Offset(size.width*.1f,size.height*.15f));drawCircle(Color(0x2226C6DA),size.width*.55f,center=androidx.compose.ui.geometry.Offset(size.width*.9f,size.height*.8f))}}

@OptIn(ExperimentalMaterial3Api::class)
@Composable private fun Page(nav:NavHostController,title:String,content:@Composable ColumnScope.()->Unit){Scaffold(topBar={TopAppBar(title={Text(title)},navigationIcon={IconButton({nav.popBackStack()}){Icon(Icons.Default.ArrowBack,"Volver")}})}){Column(Modifier.fillMaxSize().padding(it).padding(20.dp),verticalArrangement=Arrangement.spacedBy(14.dp),content=content)}}

@Composable fun AdultScreen(nav:NavHostController,state:HomeState,vm:MainViewModel){var name by remember{mutableStateOf("")};var points by remember{mutableStateOf("10")};var target by remember{mutableStateOf("1")};var type by remember{mutableStateOf(TrackingType.BOOLEAN)};Page(nav,"Panel de adulto"){Text("Nueva misión",fontSize=24.sp,fontWeight=FontWeight.Bold);OutlinedTextField(name,{name=it},label={Text("Nombre")},modifier=Modifier.fillMaxWidth());Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){OutlinedTextField(points,{points=it.filter(Char::isDigit)},label={Text("Puntos")},modifier=Modifier.weight(1f));OutlinedTextField(target,{target=it.filter(Char::isDigit)},label={Text("Meta")},modifier=Modifier.weight(1f))};LazyColumn(Modifier.height(52.dp),horizontalAlignment=Alignment.Start){item{Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){TrackingType.entries.forEach{FilterChip(type==it,{type=it},{Text(it.name.lowercase().replaceFirstChar(Char::uppercase))})}}}};Button({if(name.isNotBlank()){vm.addTask(name,points.toIntOrNull()?:10,type,target.toIntOrNull()?:1,127);name=""}},Modifier.fillMaxWidth()){Text("Crear misión")};HorizontalDivider();Text("Administrar",fontWeight=FontWeight.Bold);listOf("goals" to "Objetivos","rewards" to "Premios y canjes","stats" to "Estadísticas","settings" to "Privacidad y preferencias").forEach{(route,label)->OutlinedButton({nav.navigate(route)},Modifier.fillMaxWidth()){Text(label)}};Text("${state.tasks.size} misiones activas · ${state.transactions.size} movimientos",style=MaterialTheme.typography.bodySmall)}}

@Composable fun GoalsScreen(nav:NavHostController,state:HomeState,vm:MainViewModel){var name by remember{mutableStateOf("")};var required by remember{mutableStateOf("100")};Page(nav,"Objetivos"){LazyColumn(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(10.dp)){items(state.goals){g->ElevatedCard(Modifier.fillMaxWidth()){Column(Modifier.padding(16.dp)){Text("🏆 ${g.name}",fontWeight=FontWeight.Bold);Text("${state.points.coerceAtMost(g.requiredValue)} / ${g.requiredValue} puntos");LinearProgressIndicator({(state.points.toFloat()/g.requiredValue).coerceIn(0f,1f)},Modifier.fillMaxWidth())}}}};Text("Nuevo objetivo",fontWeight=FontWeight.Bold);OutlinedTextField(name,{name=it},label={Text("Nombre")},modifier=Modifier.fillMaxWidth());OutlinedTextField(required,{required=it.filter(Char::isDigit)},label={Text("Puntos necesarios")},modifier=Modifier.fillMaxWidth());Button({if(name.isNotBlank()){vm.addGoal(name,required.toIntOrNull()?:100,25);name=""}},Modifier.fillMaxWidth()){Text("Guardar objetivo")}}}

@Composable fun RewardsScreen(nav:NavHostController,state:HomeState,vm:MainViewModel){var name by remember{mutableStateOf("")};var cost by remember{mutableStateOf("50")};Page(nav,"Premios"){LazyColumn(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(10.dp)){items(state.rewards){r->ElevatedCard(Modifier.fillMaxWidth()){Row(Modifier.padding(16.dp),verticalAlignment=Alignment.CenterVertically){Text("🎁",fontSize=32.sp);Column(Modifier.weight(1f).padding(10.dp)){Text(r.name,fontWeight=FontWeight.Bold);Text("${r.requiredPoints} puntos${if(r.claimed)" · Canjeado" else ""}")};Button({vm.redeem(r)},enabled=state.points>=r.requiredPoints&&!r.claimed){Text("Canjear")}}}}};Text("Nuevo premio",fontWeight=FontWeight.Bold);OutlinedTextField(name,{name=it},label={Text("Premio")},modifier=Modifier.fillMaxWidth());OutlinedTextField(cost,{cost=it.filter(Char::isDigit)},label={Text("Costo")},modifier=Modifier.fillMaxWidth());Button({if(name.isNotBlank()){vm.addReward(name,cost.toIntOrNull()?:50);name=""}},Modifier.fillMaxWidth()){Text("Guardar premio")}}}

@Composable fun CalendarScreen(nav:NavHostController,state:HomeState){Page(nav,"Calendario"){Text(LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es"))).replaceFirstChar(Char::uppercase),fontSize=24.sp,fontWeight=FontWeight.Bold);LazyColumn{items((0..30).map{LocalDate.now().minusDays(it.toLong())}){date->val count=state.completions.count{it.date==date.toString()};ListItem(headlineContent={Text(date.format(DateTimeFormatter.ofPattern("EEEE d",Locale("es"))).replaceFirstChar(Char::uppercase))},supportingContent={Text(if(count==0)"Sin actividad" else "$count misiones completadas")},leadingContent={Icon(if(count>0)Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,null,tint=if(count>0)Color(0xFF2E7D32)else Color.Gray)})}}}}

@Composable fun StatsScreen(nav:NavHostController,state:HomeState){Page(nav,"Estadísticas"){Text("Tu progreso",fontSize=24.sp,fontWeight=FontWeight.Bold);Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){StatCard("⭐",state.points,"puntos",Modifier.weight(1f));StatCard("🔥",state.streak,"racha",Modifier.weight(1f))};Text("Últimos 7 días",fontWeight=FontWeight.Bold);val values=(6 downTo 0).map{i->state.completions.count{it.date==LocalDate.now().minusDays(i.toLong()).toString()}};Canvas(Modifier.fillMaxWidth().height(180.dp)){val max=(values.maxOrNull()?:1).coerceAtLeast(1);values.forEachIndexed{i,v->val x=size.width*(i+.5f)/7;drawLine(Color(0xFF7656A8),start=androidx.compose.ui.geometry.Offset(x,size.height),end=androidx.compose.ui.geometry.Offset(x,size.height-size.height*v/max),strokeWidth=size.width/14,cap=StrokeCap.Round)}};Text("Historial de puntos",fontWeight=FontWeight.Bold);LazyColumn{items(state.transactions.take(10)){t->ListItem(headlineContent={Text(t.note)},supportingContent={Text("${if(t.amount>0)"+" else ""}${t.amount} puntos")})}}}}

@Composable fun SettingsScreen(nav:NavHostController){var sound by remember{mutableStateOf(true)};var vibration by remember{mutableStateOf(true)};var celebration by remember{mutableStateOf(true)};var dark by remember{mutableStateOf(false)};Page(nav,"Preferencias"){Text("Celebraciones",fontSize=22.sp,fontWeight=FontWeight.Bold);SettingSwitch("Sonidos",sound){sound=it};SettingSwitch("Vibración",vibration){vibration=it};SettingSwitch("Confeti",celebration){celebration=it};SettingSwitch("Tema oscuro",dark){dark=it};HorizontalDivider();Text("Privacidad",fontSize=22.sp,fontWeight=FontWeight.Bold);Text("Todos los datos viven únicamente en este dispositivo. La app no usa internet, anuncios ni rastreadores.");OutlinedButton({},Modifier.fillMaxWidth()){Icon(Icons.Default.FileUpload,null);Spacer(Modifier.width(8.dp));Text("Exportar respaldo")};OutlinedButton({},Modifier.fillMaxWidth()){Icon(Icons.Default.FileDownload,null);Spacer(Modifier.width(8.dp));Text("Importar respaldo")};Text("El PIN adulto es una barrera de uso, no un mecanismo de seguridad.",style=MaterialTheme.typography.bodySmall)}}
@Composable private fun SettingSwitch(label:String,value:Boolean,onChange:(Boolean)->Unit){Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){Text(label,Modifier.weight(1f));Switch(value,onChange)}}
