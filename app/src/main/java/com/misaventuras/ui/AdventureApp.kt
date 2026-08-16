package com.misaventuras.ui

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.misaventuras.data.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val Ink = Color(0xFF302A3A)
private val Bubblegum = Color(0xFFFF5C93)
private val Cream = Color(0xFFFFF9F3)
private val Mint = Color(0xFF45B8A5)

@Composable fun AdventureApp(vm:MainViewModel=hiltViewModel()) {
    val nav=rememberNavController(); val state by vm.state.collectAsStateWithLifecycle()
    val primary=state.selected?.primaryColor?.let{Color(it.toInt())}?:Color(0xFF7656A8)
    MaterialTheme(colorScheme=lightColorScheme(primary=primary,secondary=Bubblegum,tertiary=Mint,background=Cream,surface=Color.White,surfaceVariant=Color(0xFFF3EDF8),onBackground=Ink,onSurface=Ink),shapes=Shapes(extraLarge=RoundedCornerShape(32.dp),large=RoundedCornerShape(26.dp),medium=RoundedCornerShape(18.dp),small=RoundedCornerShape(12.dp))) {
        NavHost(nav,"welcome") {
            composable("welcome"){WelcomeScreen(state){vm.select(it);nav.navigate("home")}}
            composable("home"){HomeScreen(state,vm::toggle,{nav.navigate("welcome")}){nav.navigate(it)}}
            composable("adult"){AdultScreen(nav,state,vm)}
            composable("profile"){ProfileScreen(nav,state,vm)}
            composable("calendar"){CalendarScreen(nav,state)}
            composable("goals"){GoalsScreen(nav,state,vm)}
            composable("rewards"){RewardsScreen(nav,state,vm)}
            composable("stats"){StatsScreen(nav,state)}
            composable("settings"){SettingsScreen(nav)}
        }
    }
}

@Composable fun WelcomeScreen(state:HomeState,onSelect:(Long)->Unit){Box(Modifier.fillMaxSize().background(Cream)){SoftBackground();Column(Modifier.fillMaxSize().padding(horizontal=24.dp,vertical=40.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center){Box(Modifier.size(96.dp).background(Color.White.copy(alpha=.9f),CircleShape),contentAlignment=Alignment.Center){Text("🌈",fontSize=52.sp)};Spacer(Modifier.height(18.dp));Text("Mis Aventuras",fontSize=38.sp,fontWeight=FontWeight.ExtraBold,color=Ink);Text("¿Quién comienza su aventura?",fontSize=17.sp,color=Ink.copy(alpha=.7f),modifier=Modifier.padding(top=6.dp,bottom=24.dp));state.profiles.forEach{p->ElevatedCard(onClick={onSelect(p.id)},modifier=Modifier.fillMaxWidth().padding(vertical=7.dp),colors=CardDefaults.elevatedCardColors(containerColor=Color.White),elevation=CardDefaults.elevatedCardElevation(5.dp)){Row(Modifier.fillMaxWidth().padding(16.dp),verticalAlignment=Alignment.CenterVertically){ProfileAvatar(p,56.dp);Spacer(Modifier.width(16.dp));Text(p.name,fontSize=23.sp,fontWeight=FontWeight.Bold,modifier=Modifier.weight(1f));Box(Modifier.size(38.dp).background(Color(p.primaryColor.toInt()).copy(alpha=.12f),CircleShape),contentAlignment=Alignment.Center){Icon(Icons.Default.ArrowForward,null,tint=Color(p.primaryColor.toInt()))}}}}}}}

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun HomeScreen(state:HomeState,onToggle:(TodayTask)->Unit,onSwitch:()->Unit,onNavigate:(String)->Unit){Scaffold(topBar={TopAppBar(title={Text("¡Hola, ${state.selected?.name.orEmpty()}! 👋")},actions={IconButton(onClick=onSwitch,modifier=Modifier.semantics{contentDescription="Cambiar perfil"}){Icon(Icons.Default.SwitchAccount,null)};IconButton(onClick={onNavigate("adult")}){Icon(Icons.Default.AdminPanelSettings,"Modo adulto")}})},bottomBar={NavigationBar{listOf(Triple("home",Icons.Default.Home,"Hoy"),Triple("calendar",Icons.Default.CalendarMonth,"Calendario"),Triple("goals",Icons.Default.EmojiEvents,"Objetivos"),Triple("rewards",Icons.Default.CardGiftcard,"Premios")).forEach{(route,icon,label)->NavigationBarItem(selected=route=="home",onClick={onNavigate(route)},icon={Icon(icon,label)},label={Text(label)})}}}){pad->LazyColumn(Modifier.fillMaxSize().padding(pad).padding(horizontal=16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){item{Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp)){StatCard("⭐",state.points,"puntos",Modifier.weight(1f));StatCard("🚀",state.level,"nivel",Modifier.weight(1f));StatCard("🔥",state.streak,"racha",Modifier.weight(1f))};val done=state.tasks.count{it.completion!=null};Text("Misiones de hoy · $done/${state.tasks.size}",fontSize=22.sp,fontWeight=FontWeight.Bold);LinearProgressIndicator(progress={if(state.tasks.isEmpty())0f else done.toFloat()/state.tasks.size},Modifier.fillMaxWidth().height(10.dp))};if(state.tasks.isEmpty())item{EmptyCard()}else items(state.tasks,key={it.task.id}){TaskCard(it){onToggle(it)}};item{Spacer(Modifier.height(16.dp))}}}}
@Composable private fun StatCard(emoji:String,value:Int,label:String,modifier:Modifier){ElevatedCard(modifier,colors=CardDefaults.elevatedCardColors(containerColor=Color.White)){Column(Modifier.fillMaxWidth().padding(vertical=14.dp,horizontal=8.dp),horizontalAlignment=Alignment.CenterHorizontally){Text(emoji,fontSize=25.sp);Text("$value",fontWeight=FontWeight.ExtraBold,fontSize=21.sp,color=Ink);Text(label,color=Ink.copy(alpha=.65f),fontSize=13.sp)}}}
@Composable private fun TaskCard(item:TodayTask,onClick:()->Unit){val done=item.completion!=null;ElevatedCard(Modifier.fillMaxWidth(),colors=CardDefaults.elevatedCardColors(containerColor=if(done)Color(0xFFF1FBF6) else Color.White),elevation=CardDefaults.elevatedCardElevation(3.dp)){Row(Modifier.padding(16.dp),verticalAlignment=Alignment.CenterVertically){if(done) Box(Modifier.size(48.dp).background(Color(0xFFDCF5E6),RoundedCornerShape(15.dp)),contentAlignment=Alignment.Center){Icon(Icons.Default.CheckCircle,"Misión cumplida",tint=Color(0xFF258554),modifier=Modifier.size(32.dp))} else TaskIcon(item.task.icon);Column(Modifier.weight(1f).padding(horizontal=14.dp)){Text(item.task.name,fontSize=18.sp,fontWeight=FontWeight.Bold);Text(if(done)"¡Misión completada!" else "+${item.task.points} puntos",color=if(done)Color(0xFF258554) else MaterialTheme.colorScheme.primary,fontWeight=FontWeight.SemiBold)};FilledIconButton(onClick=onClick,modifier=Modifier.size(52.dp),colors=IconButtonDefaults.filledIconButtonColors(containerColor=if(done)Color(0xFFE1E7E5) else MaterialTheme.colorScheme.primary)){Icon(if(done)Icons.Default.Undo else Icons.Default.Check,if(done)"Deshacer" else "Completar")}}}}
@Composable private fun EmptyCard(){Card{Column(Modifier.fillMaxWidth().padding(28.dp),horizontalAlignment=Alignment.CenterHorizontally){Text("☁️",fontSize=46.sp);Text("¡Hoy no hay misiones!",fontSize=21.sp,fontWeight=FontWeight.Bold);Text("Podés descansar o elegir una aventura extra.")}}}
@Composable private fun SoftBackground(){Canvas(Modifier.fillMaxSize()){drawCircle(Color(0x22F48FB1),size.width*.45f,center=androidx.compose.ui.geometry.Offset(size.width*.1f,size.height*.15f));drawCircle(Color(0x2226C6DA),size.width*.55f,center=androidx.compose.ui.geometry.Offset(size.width*.9f,size.height*.8f))}}

@Composable private fun StoredImage(path:String?,description:String,modifier:Modifier):Boolean {
    val bitmap=remember(path){path?.takeIf{it.startsWith("/")}?.let{runCatching{BitmapFactory.decodeFile(it)?.asImageBitmap()}.getOrNull()}}
    if(bitmap!=null) Image(bitmap,description,modifier,contentScale=ContentScale.Crop)
    return bitmap!=null
}
@Composable private fun ProfileAvatar(profile:ChildProfileEntity,size:Dp){
    val modifier=Modifier.size(size).background(Color(profile.primaryColor.toInt()).copy(alpha=.15f),CircleShape)
    if(!StoredImage(profile.avatarPath,"Foto de ${profile.name}",modifier)) Icon(Icons.Default.Face,"Avatar de ${profile.name}",modifier.padding(7.dp),tint=Color(profile.primaryColor.toInt()))
}
@Composable private fun TaskIcon(value:String){
    val modifier=Modifier.size(48.dp).background(MaterialTheme.colorScheme.primary.copy(alpha=.12f),RoundedCornerShape(14.dp))
    if(!StoredImage(value,"Icono personalizado de la misión",modifier)) Text(when(value){"book"->"📚";"heart"->"💗";"smile"->"😊";"sport"->"⚽";"brush"->"🪥";else->"⭐"},fontSize=30.sp,modifier=Modifier.width(48.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable private fun Page(nav:NavHostController,title:String,content:@Composable ColumnScope.()->Unit){Scaffold(topBar={TopAppBar(title={Text(title)},navigationIcon={IconButton({nav.popBackStack()}){Icon(Icons.Default.ArrowBack,"Volver")}})}){Column(Modifier.fillMaxSize().padding(it).padding(20.dp),verticalArrangement=Arrangement.spacedBy(14.dp),content=content)}}

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun AdultScreen(nav:NavHostController,state:HomeState,vm:MainViewModel){var name by remember{mutableStateOf("")};var points by remember{mutableStateOf("10")};var target by remember{mutableStateOf("1")};var type by remember{mutableStateOf(TrackingType.BOOLEAN)};var icon by remember{mutableStateOf("star")};var customIcon by remember{mutableStateOf<Uri?>(null)};val picker=rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()){customIcon=it};Scaffold(containerColor=Cream,topBar={TopAppBar(title={Text("Panel de adulto",fontWeight=FontWeight.Bold)},navigationIcon={IconButton({nav.popBackStack()}){Icon(Icons.Default.ArrowBack,"Volver")}},colors=TopAppBarDefaults.topAppBarColors(containerColor=Cream))}){padding->LazyColumn(Modifier.fillMaxSize().padding(padding),contentPadding=PaddingValues(start=18.dp,end=18.dp,bottom=32.dp),verticalArrangement=Arrangement.spacedBy(16.dp)){item{Text("Crear una misión",fontSize=26.sp,fontWeight=FontWeight.ExtraBold);Text("Prepará una nueva aventura en pocos pasos",color=Ink.copy(alpha=.65f))};item{ElevatedCard(colors=CardDefaults.elevatedCardColors(containerColor=Color.White),elevation=CardDefaults.elevatedCardElevation(2.dp)){Column(Modifier.padding(18.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){OutlinedTextField(name,{name=it.take(50)},label={Text("Nombre de la misión")},leadingIcon={Icon(Icons.Default.AutoAwesome,null)},singleLine=true,modifier=Modifier.fillMaxWidth());Text("Elegí un icono",fontWeight=FontWeight.Bold);Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(8.dp)){listOf("star" to "⭐","book" to "📚","heart" to "💗","smile" to "😊","sport" to "⚽").forEach{(key,emoji)->FilterChip(icon==key&&customIcon==null,{icon=key;customIcon=null},{Text(emoji,fontSize=22.sp)})}};OutlinedButton({picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))},Modifier.fillMaxWidth().height(50.dp)){Icon(Icons.Default.AddPhotoAlternate,null);Spacer(Modifier.width(8.dp));Text(if(customIcon==null)"Usar una imagen propia" else "Imagen lista · Cambiar")};Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){OutlinedTextField(points,{points=it.filter(Char::isDigit).take(4)},label={Text("Puntos")},singleLine=true,modifier=Modifier.weight(1f));OutlinedTextField(target,{target=it.filter(Char::isDigit).take(4)},label={Text("Meta")},singleLine=true,modifier=Modifier.weight(1f))};Text("¿Cómo se completa?",fontWeight=FontWeight.Bold);Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(8.dp)){TrackingType.entries.forEach{FilterChip(type==it,{type=it},{Text(trackingLabel(it))})}};Button({if(name.isNotBlank()){vm.addTask(name,points.toIntOrNull()?:10,type,target.toIntOrNull()?:1,127,icon,customIcon);name="";customIcon=null}},enabled=name.isNotBlank(),modifier=Modifier.fillMaxWidth().height(54.dp)){Icon(Icons.Default.AddTask,null);Spacer(Modifier.width(8.dp));Text("Crear misión",fontWeight=FontWeight.Bold)}}}};item{Text("Administrar",fontSize=21.sp,fontWeight=FontWeight.ExtraBold)};items(listOf(Triple("profile",Icons.Default.Face,"Perfil de ${state.selected?.name.orEmpty()}"),Triple("goals",Icons.Default.EmojiEvents,"Objetivos"),Triple("rewards",Icons.Default.CardGiftcard,"Premios y canjes"),Triple("stats",Icons.Default.BarChart,"Estadísticas"),Triple("settings",Icons.Default.Settings,"Privacidad y preferencias"))){(route,navIcon,label)->Card(onClick={nav.navigate(route)},colors=CardDefaults.cardColors(containerColor=Color.White)){Row(Modifier.fillMaxWidth().padding(16.dp),verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(42.dp).background(MaterialTheme.colorScheme.primary.copy(alpha=.1f),RoundedCornerShape(13.dp)),contentAlignment=Alignment.Center){Icon(navIcon,null,tint=MaterialTheme.colorScheme.primary)};Text(label,Modifier.weight(1f).padding(horizontal=14.dp),fontWeight=FontWeight.SemiBold);Icon(Icons.Default.ChevronRight,null,tint=Ink.copy(alpha=.45f))}}}}}}

private fun trackingLabel(type:TrackingType)=when(type){TrackingType.BOOLEAN->"Sí / No";TrackingType.QUANTITY->"Cantidad";TrackingType.STEPS->"Pasos";TrackingType.RATING->"Valoración";TrackingType.TIMER->"Tiempo"}

@Composable fun ProfileScreen(nav:NavHostController,state:HomeState,vm:MainViewModel){val profile=state.selected?:return;var name by remember(profile.id){mutableStateOf(profile.name)};var color by remember(profile.id){mutableLongStateOf(profile.primaryColor)};var avatar by remember{mutableStateOf<Uri?>(null)};val picker=rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()){avatar=it};Page(nav,"Personalizar perfil"){Row(verticalAlignment=Alignment.CenterVertically){ProfileAvatar(profile,88.dp);Column(Modifier.padding(start=18.dp)){Text("Esta cuenta es única",fontSize=22.sp,fontWeight=FontWeight.Bold);Text("Elegí su nombre, foto y color")}};OutlinedTextField(name,{name=it.take(30)},label={Text("Nombre")},singleLine=true,modifier=Modifier.fillMaxWidth());OutlinedButton({picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))},Modifier.fillMaxWidth()){Icon(Icons.Default.PhotoCamera,null);Spacer(Modifier.width(8.dp));Text(if(avatar==null)"Elegir foto" else "Nueva foto seleccionada")};Text("Color de su aventura",fontWeight=FontWeight.SemiBold);Row(horizontalArrangement=Arrangement.spacedBy(14.dp)){listOf(0xFF7656A8,0xFF168C88,0xFFE05D87,0xFF3973B7,0xFFF07F3C).forEach{value->Box(Modifier.size(46.dp).background(Color(value),CircleShape).clickable{color=value}.semantics{contentDescription="Elegir color"},contentAlignment=Alignment.Center){if(color==value)Icon(Icons.Default.Check,null,tint=Color.White)}}};Spacer(Modifier.weight(1f));Button({vm.updateProfile(name,color,avatar);nav.popBackStack()},enabled=name.isNotBlank(),modifier=Modifier.fillMaxWidth().height(54.dp)){Text("Guardar cambios")};Text("La foto se optimiza y queda guardada únicamente en este dispositivo.",style=MaterialTheme.typography.bodySmall)} }

@Composable fun GoalsScreen(nav:NavHostController,state:HomeState,vm:MainViewModel){var name by remember{mutableStateOf("")};var required by remember{mutableStateOf("100")};Page(nav,"Objetivos"){LazyColumn(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(10.dp)){items(state.goals){g->ElevatedCard(Modifier.fillMaxWidth()){Column(Modifier.padding(16.dp)){Text("🏆 ${g.name}",fontWeight=FontWeight.Bold);Text("${state.points.coerceAtMost(g.requiredValue)} / ${g.requiredValue} puntos");LinearProgressIndicator({(state.points.toFloat()/g.requiredValue).coerceIn(0f,1f)},Modifier.fillMaxWidth())}}}};Text("Nuevo objetivo",fontWeight=FontWeight.Bold);OutlinedTextField(name,{name=it},label={Text("Nombre")},modifier=Modifier.fillMaxWidth());OutlinedTextField(required,{required=it.filter(Char::isDigit)},label={Text("Puntos necesarios")},modifier=Modifier.fillMaxWidth());Button({if(name.isNotBlank()){vm.addGoal(name,required.toIntOrNull()?:100,25);name=""}},Modifier.fillMaxWidth()){Text("Guardar objetivo")}}}

@Composable fun RewardsScreen(nav:NavHostController,state:HomeState,vm:MainViewModel){var name by remember{mutableStateOf("")};var cost by remember{mutableStateOf("50")};Page(nav,"Premios"){LazyColumn(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(10.dp)){items(state.rewards){r->ElevatedCard(Modifier.fillMaxWidth()){Row(Modifier.padding(16.dp),verticalAlignment=Alignment.CenterVertically){Text("🎁",fontSize=32.sp);Column(Modifier.weight(1f).padding(10.dp)){Text(r.name,fontWeight=FontWeight.Bold);Text("${r.requiredPoints} puntos${if(r.claimed)" · Canjeado" else ""}")};Button({vm.redeem(r)},enabled=state.points>=r.requiredPoints&&!r.claimed){Text("Canjear")}}}}};Text("Nuevo premio",fontWeight=FontWeight.Bold);OutlinedTextField(name,{name=it},label={Text("Premio")},modifier=Modifier.fillMaxWidth());OutlinedTextField(cost,{cost=it.filter(Char::isDigit)},label={Text("Costo")},modifier=Modifier.fillMaxWidth());Button({if(name.isNotBlank()){vm.addReward(name,cost.toIntOrNull()?:50);name=""}},Modifier.fillMaxWidth()){Text("Guardar premio")}}}

@Composable fun CalendarScreen(nav:NavHostController,state:HomeState){Page(nav,"Calendario"){Text(LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es"))).replaceFirstChar(Char::uppercase),fontSize=24.sp,fontWeight=FontWeight.Bold);LazyColumn{items((0..30).map{LocalDate.now().minusDays(it.toLong())}){date->val count=state.completions.count{it.date==date.toString()};ListItem(headlineContent={Text(date.format(DateTimeFormatter.ofPattern("EEEE d",Locale("es"))).replaceFirstChar(Char::uppercase))},supportingContent={Text(if(count==0)"Sin actividad" else "$count misiones completadas")},leadingContent={Icon(if(count>0)Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,null,tint=if(count>0)Color(0xFF2E7D32)else Color.Gray)})}}}}

@Composable fun StatsScreen(nav:NavHostController,state:HomeState){Page(nav,"Estadísticas"){Text("Tu progreso",fontSize=24.sp,fontWeight=FontWeight.Bold);Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){StatCard("⭐",state.points,"puntos",Modifier.weight(1f));StatCard("🔥",state.streak,"racha",Modifier.weight(1f))};Text("Últimos 7 días",fontWeight=FontWeight.Bold);val values=(6 downTo 0).map{i->state.completions.count{it.date==LocalDate.now().minusDays(i.toLong()).toString()}};Canvas(Modifier.fillMaxWidth().height(180.dp)){val max=(values.maxOrNull()?:1).coerceAtLeast(1);values.forEachIndexed{i,v->val x=size.width*(i+.5f)/7;drawLine(Color(0xFF7656A8),start=androidx.compose.ui.geometry.Offset(x,size.height),end=androidx.compose.ui.geometry.Offset(x,size.height-size.height*v/max),strokeWidth=size.width/14,cap=StrokeCap.Round)}};Text("Historial de puntos",fontWeight=FontWeight.Bold);LazyColumn{items(state.transactions.take(10)){t->ListItem(headlineContent={Text(t.note)},supportingContent={Text("${if(t.amount>0)"+" else ""}${t.amount} puntos")})}}}}

@Composable fun SettingsScreen(nav:NavHostController){var sound by remember{mutableStateOf(true)};var vibration by remember{mutableStateOf(true)};var celebration by remember{mutableStateOf(true)};var dark by remember{mutableStateOf(false)};Page(nav,"Preferencias"){Text("Celebraciones",fontSize=22.sp,fontWeight=FontWeight.Bold);SettingSwitch("Sonidos",sound){sound=it};SettingSwitch("Vibración",vibration){vibration=it};SettingSwitch("Confeti",celebration){celebration=it};SettingSwitch("Tema oscuro",dark){dark=it};HorizontalDivider();Text("Privacidad",fontSize=22.sp,fontWeight=FontWeight.Bold);Text("Todos los datos viven únicamente en este dispositivo. La app no usa internet, anuncios ni rastreadores.");OutlinedButton({},Modifier.fillMaxWidth()){Icon(Icons.Default.FileUpload,null);Spacer(Modifier.width(8.dp));Text("Exportar respaldo")};OutlinedButton({},Modifier.fillMaxWidth()){Icon(Icons.Default.FileDownload,null);Spacer(Modifier.width(8.dp));Text("Importar respaldo")};Text("El PIN adulto es una barrera de uso, no un mecanismo de seguridad.",style=MaterialTheme.typography.bodySmall)}}
@Composable private fun SettingSwitch(label:String,value:Boolean,onChange:(Boolean)->Unit){Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){Text(label,Modifier.weight(1f));Switch(value,onChange)}}
