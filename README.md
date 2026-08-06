# Mis Aventuras

Aplicación Android local para acompañar las tareas y hábitos de Lola y Olivia mediante misiones, puntos y recompensas. El diseño es propio, infantil, claro y sin recursos de marcas comerciales.

## Solución y alcance

El MVP implementa perfiles persistentes, selector sin autenticación, temas iniciales, agenda semanal o puntual, seguimientos booleano y por cantidad en el modelo, registro diario idempotente, historial contable de puntos, reversión, niveles y la base de objetivos y premios. Incluye un panel adulto inicial para crear tareas. Los modelos reservan `STEPS`, `RATING` y `TIMER` para una evolución compatible.

Las siguientes iteraciones ampliarán los editores de objetivos/premios, calendario, estadísticas Canvas, PIN, canjes, celebraciones configurables y la interfaz SAF del respaldo. Esta separación evita fingir que una pantalla incompleta forma parte del MVP terminado.

## Arquitectura

Proyecto de un módulo, deliberadamente sencillo:

* **UI:** Compose + Material 3, navegación y ViewModels con estado inmutable (`StateFlow`).
* **Dominio:** reglas puras para programación, porcentajes, niveles y rachas.
* **Datos:** repositorio, Room como fuente de verdad y DataStore para selección/preferencias.
* **DI:** Hilt. Coroutines/Flow mantienen lecturas reactivas y transacciones Room hacen atómico el cumplimiento.

```text
app/src/main/java/com/misaventuras/
├── data/       # entidades, DAO, Room, repositorio, preferencias e imágenes
├── di/         # módulo Hilt
├── domain/     # reglas puras de gamificación
├── ui/         # navegación, pantallas y ViewModel
├── MainActivity.kt
└── MisAventurasApp.kt
```

## Modelo de datos

`ChildProfile` posee tareas, objetivos, premios, logros y transacciones. Una `Task` posee cumplimientos; el índice único `(taskId,date)` impide duplicados. El DAO escribe cumplimiento y transacción de puntos en una sola transacción. Deshacer elimina el cumplimiento y agrega una transacción inversa, conservando auditoría. `GoalTaskRelation` representa objetivos de tareas múltiples y `RewardRedemption` registra canjes. Los puntos visibles se derivan de la suma del libro mayor, nunca de un contador del perfil.

La agenda usa una máscara ISO lunes-domingo y una fecha puntual opcional. La racha predeterminada exige 80 % y omite días sin tareas. Los niveles están desacoplados en una tabla de umbrales.

## Navegación

`Bienvenida → Inicio de perfil → Panel adulto`. Inicio ofrece cambio inmediato de perfil y cumplimiento/deshacer. Las rutas previstas para el siguiente incremento son detalle, calendario, objetivos, premios, personalización, estadísticas, PIN y respaldo.

## Imágenes y privacidad

`ImageStore` copia la imagen seleccionada a almacenamiento interno, limita su lado mayor a 1024 px y la comprime como WebP. La integración visual debe invocarlo desde el Photo Picker oficial (`PickVisualMedia`); no se solicita permiso general de archivos.

La aplicación no declara permiso de Internet, ubicación, contactos ni almacenamiento. No integra anuncios, analytics, trackers, cuentas, backend, claves, compras ni funciones sociales. Ningún dato o imagen abandona el dispositivo. Android Backup está desactivado para reforzar el comportamiento estrictamente local.

## Compilar

Requisitos: Android Studio Ladybug o posterior, JDK 17/21, Android SDK 35 y Gradle 8.9 o posterior.

> **Nota sobre el wrapper:** este repositorio no incluye `gradle-wrapper.jar` porque algunos revisores de pull requests rechazan archivos binarios. `gradlew` es un lanzador de texto que delega en Gradle instalado. En Android Studio seleccione **Settings → Build Tools → Gradle → Gradle distribution: Local installation** (o use el Gradle incluido por el IDE). Para regenerar el wrapper estándar localmente: `gradle wrapper --gradle-version 8.9`; no es necesario subir el JAR generado.

```bash
./gradlew test
./gradlew assembleDebug
./gradlew connectedDebugAndroidTest # con emulador/dispositivo API 26+
```

Abra la raíz del repositorio en Android Studio, espere la sincronización y ejecute la configuración `app`.

## Decisiones, riesgos y supuestos

* Fechas locales ISO son suficientes para hábitos diarios; un cambio de zona horaria no duplica el índice del día.
* Room es la autoridad y el historial inmutable permite auditoría; las reversiones nunca borran puntos silenciosamente.
* El PIN es solo una barrera accidental y no se presentará como seguridad real.
* Importar un ZIP requerirá validar versión/esquema, rutas y tamaños antes de una sustitución transaccional, con confirmación explícita.
* Photo Picker puede devolver contenido grande o dañado; la copia valida el bitmap y limita resolución.
* Las eliminaciones destructivas deben usar confirmación y archivado cuando sea posible.

## Plan por etapas

1. **Base:** Gradle Kotlin DSL/catalog, Compose, Hilt, Room, DataStore y perfiles.
2. **MVP:** agenda, cantidad/booleano, ledger idempotente, reversión, racha, niveles y CRUD progresivo.
3. **Personalización:** Photo Picker, temas editables, iconos genéricos, preferencias de sonido/vibración/confeti y reducción de movimiento.
4. **Información:** calendario, objetivos/premios completos y gráficos Canvas.
5. **Respaldo:** ZIP versionado (JSON + `images/`) con SAF, validación y reemplazo confirmado.
6. **Calidad:** pruebas DAO/instrumentadas, TalkBack, escalado tipográfico, estados de error y pruebas de restauración.

## Funcionalidades futuras

Editores completos, objetivo evaluado automáticamente una sola vez, solicitudes de canje confirmadas por adulto, PIN opcional, estadísticas mensual/semanal, insignias, temporizador, pasos, valoración visual, exportación/importación ZIP, tema oscuro, selector de símbolos y datos demo exclusivos de `debug`.
