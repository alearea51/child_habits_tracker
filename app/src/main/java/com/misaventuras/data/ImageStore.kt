package com.misaventuras.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class ImageStore @Inject constructor(@ApplicationContext private val context:Context){
 fun copyOptimized(uri:Uri,name:String):String { val bytes=context.contentResolver.openInputStream(uri)?.use{it.readBytes()}?:error("No se pudo leer la imagen"); val decoded=BitmapFactory.decodeByteArray(bytes,0,bytes.size)?:error("Imagen no válida"); val scale=minOf(1f,1024f/maxOf(decoded.width,decoded.height)); val bitmap=if(scale<1) Bitmap.createScaledBitmap(decoded,(decoded.width*scale).toInt(),(decoded.height*scale).toInt(),true) else decoded; val dir=File(context.filesDir,"images").apply{mkdirs()}; val file=File(dir,"${name}_${System.currentTimeMillis()}.webp"); file.outputStream().use{bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY,82,it)}; if(bitmap!==decoded)bitmap.recycle(); decoded.recycle(); return file.absolutePath }
}
