# <img src="app/src/main/res/drawable/logo_extude.png" width="36" align="center" /> EXTUDE

> **Aplicación Android de Gestión y Planificación de Estudios**  
> Proyecto de fin de ciclo · 2ª DAM · UAX FP  
> Miguel Ángel Enríquez Menayo 

---

##  Descripción

EXTUDE es una aplicación Android orientada a ayudar a los estudiantes a organizar su tiempo de estudio de forma eficiente. Permite gestionar asignaturas, registrar tareas y exámenes, planificar sesiones de estudio con temporizador y visualizar el progreso académico mediante estadísticas.

---

##  Funcionalidades

| Módulo | Descripción |
|--------|-------------|
|  **Login / Registro** | Autenticación con email y contraseña, sesión persistente |
|  **Asignaturas** | Crear, editar y eliminar asignaturas con color identificador |
|  **Tareas y exámenes** | Registro con fecha de entrega, tipo y estado de completado |
|  **Sesión de estudio** | Temporizador Pomodoro configurable de 1 a 120 minutos |
|  **Estadísticas** | Horas estudiadas, tareas completadas y progreso académico |

---

##  Tecnologías

- **Lenguaje**: Java
- **IDE**: Android Studio
- **Base de datos local**: SQLite (DatabaseHelper)
- **Base de datos remota**  MySQL + phpMyAdmin
- **Sesión de usuario**: SharedPreferences
- **Componentes UI**: CardView, ListView, SeekBar, ProgressBar
- **Control de versiones**: Git + GitHub

---

## Estructura del proyecto

```
app/src/main/
├── java/com/extude/app/
│   ├── DatabaseHelper.java       # SQLite: tablas, CRUD y consultas
│   ├── SessionManager.java       # Gestión de sesión (SharedPreferences)
│   ├── LoginActivity.java        # Pantalla de inicio de sesión
│   ├── RegistroActivity.java     # Pantalla de registro
│   ├── DashboardActivity.java    # Panel principal con estadísticas
│   ├── AsignaturasActivity.java  # Gestión de asignaturas
│   ├── TareasActivity.java       # Gestión de tareas y exámenes
│   ├── SesionEstudioActivity.java# Temporizador Pomodoro
│   ├── EstadisticasActivity.java # Progreso académico
│   └── ApiService.java           # Conector HTTP para API PHP (opcional)
│
└── res/
    ├── layout/                   # Pantallas XML
    ├── drawable/                 # Botones, inputs y logo
    └── values/                   # Colores, strings y estilos
```

---

## Base de datos

### Tablas SQLite / MySQL

```
usuarios          → id_usuario, nombre, email, password
asignaturas       → id_asignatura, nombre, profesor, color, id_usuario
tareas            → id_tarea, titulo, descripcion, fecha_entrega, tipo, completada, id_asignatura
sesiones_estudio  → id_sesion, fecha, duracion_min, id_asignatura
```

### Relaciones

```
Usuario (1) ──── (N) Asignatura (1) ──── (N) Tarea
                              (1) ──── (N) SesionEstudio
```

---

## Instalación y uso

### Requisitos

- Android Studio Hedgehog o superior
- Android SDK API 24+
- Java 8+

### Pasos

1. Clona el repositorio:
   ```bash
   git clone https://github.com/abdenmer/extude.git
   ```

2. Abre el proyecto en **Android Studio**

3. Espera a que **Gradle sincronice** las dependencias

4. Añade la imagen del logo en `res/drawable/logo_extude.png`

5. Pulsa **▶ Run** con el emulador o dispositivo conectado

>  SQLite se crea automáticamente la base de datos en el primer arranque. 


## Conexión con phpMyAdmin 

Si quieres usar MySQL en lugar de SQLite local:

1. Importa `sql/extude_database.sql` en **phpMyAdmin**
2. Copia la carpeta `sql/php_api/` en `C:\xampp\htdocs\extude\api\`
3. Inicia **Apache** y **MySQL** en XAMPP
4. Verifica en el navegador:
   ```
   http://localhost/extude/api/asignaturas.php?user_id=1
   ```
5. En `ApiService.java` ajusta la URL base:
   ```java
   // Emulador Android Studio
   public static final String BASE_URL = "http://10.0.2.2/extude/api/";

   // Dispositivo físico (sustituye por tu IP local)
   public static final String BASE_URL = "http://192.168.1.X/extude/api/";
   ```
6. Activa el permiso en `AndroidManifest.xml`:
   ```xml
   <uses-permission android:name="android.permission.INTERNET" />
   ```

## Paleta de colores

| Token | Color | Hex |
| Fondo principal | ![#0F0A1E](https://placehold.co/15x15/0F0A1E/0F0A1E.png) | `#0F0A1E` |
| Superficie cards | ![#1E1040](https://placehold.co/15x15/1E1040/1E1040.png) | `#1E1040` |
| Color primario | ![#7C3AED](https://placehold.co/15x15/7C3AED/7C3AED.png) | `#7C3AED` |
| Acento / textos | ![#A78BFA](https://placehold.co/15x15/A78BFA/A78BFA.png) | `#A78BFA` |

---

## Requisitos del sistema (RFTP)

| Código | Requisito |
|--------|-----------|
| R01 | Acceso mediante usuario registrado |
| R02 | Gestión de asignaturas |
| R03 | Registro de tareas y exámenes |
| R04 | Estadísticas de progreso y horas de estudio |

---

## Planificación

| Semana | Fase |
| 1 – 2 | Análisis de requisitos y diseño del sistema 
| 3 – 7 | Desarrollo de funcionalidades principales 
| 7-10 | Pruebas y corrección de errores 
| 10-14 | Documentación final 

---

## Licencia

Proyecto académico — UAX FP - Desarrollo de Aplicaciones Multiplataforma · 2026
