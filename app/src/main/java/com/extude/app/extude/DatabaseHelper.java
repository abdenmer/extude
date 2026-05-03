package com.extude.app.extude;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "extude.db";
    private static final int DATABASE_VERSION = 1;

    // La tabla de los usuarios
    public static final String TABLE_USUARIOS = "usuarios";
    public static final String COL_USER_ID = "id_usuario";
    public static final String COL_USER_NOMBRE = "nombre";
    public static final String COL_USER_EMAIL = "email";
    public static final String COL_USER_PASSWORD = "password";

    //LA tablade las asignaturas:ampliarla si me da tiempo
    public static final String TABLE_ASIGNATURAS = "asignaturas";
    public static final String COL_ASIG_ID = "id_asignatura";
    public static final String COL_ASIG_NOMBRE = "nombre";
    public static final String COL_ASIG_PROFESOR = "profesor";
    public static final String COL_ASIG_COLOR = "color";
    public static final String COL_ASIG_USER_ID = "id_usuario";

    // Latabla de las tareas
    public static final String TABLE_TAREAS = "tareas";
    public static final String COL_TAREA_ID = "id_tarea";
    public static final String COL_TAREA_TITULO = "titulo";
    public static final String COL_TAREA_DESCRIPCION = "descripcion";
    public static final String COL_TAREA_FECHA = "fecha_entrega";
    public static final String COL_TAREA_TIPO = "tipo"; // tarea / examen
    public static final String COL_TAREA_COMPLETADA = "completada";
    public static final String COL_TAREA_ASIG_ID = "id_asignatura";

    // Las tablas de las sesiones de estudio
    public static final String TABLE_SESIONES = "sesiones_estudio";
    public static final String COL_SES_ID = "id_sesion";
    public static final String COL_SES_FECHA = "fecha";
    public static final String COL_SES_DURACION = "duracion_min";
    public static final String COL_SES_ASIG_ID = "id_asignatura";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear usuarios
        String createUsuarios = "CREATE TABLE " + TABLE_USUARIOS + " ("
                + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USER_NOMBRE + " TEXT NOT NULL, "
                + COL_USER_EMAIL + " TEXT UNIQUE NOT NULL, "
                + COL_USER_PASSWORD + " TEXT NOT NULL)";
        db.execSQL(createUsuarios);

        // Crear asignaturas
        String createAsignaturas = "CREATE TABLE " + TABLE_ASIGNATURAS + " ("
                + COL_ASIG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_ASIG_NOMBRE + " TEXT NOT NULL, "
                + COL_ASIG_PROFESOR + " TEXT, "
                + COL_ASIG_COLOR + " TEXT DEFAULT '#7C3AED', "
                + COL_ASIG_USER_ID + " INTEGER, "
                + "FOREIGN KEY(" + COL_ASIG_USER_ID + ") REFERENCES " + TABLE_USUARIOS + "(" + COL_USER_ID + "))";
        db.execSQL(createAsignaturas);

        // Crear tareas
        String createTareas = "CREATE TABLE " + TABLE_TAREAS + " ("
                + COL_TAREA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TAREA_TITULO + " TEXT NOT NULL, "
                + COL_TAREA_DESCRIPCION + " TEXT, "
                + COL_TAREA_FECHA + " TEXT, "
                + COL_TAREA_TIPO + " TEXT DEFAULT 'tarea', "
                + COL_TAREA_COMPLETADA + " INTEGER DEFAULT 0, "
                + COL_TAREA_ASIG_ID + " INTEGER, "
                + "FOREIGN KEY(" + COL_TAREA_ASIG_ID + ") REFERENCES " + TABLE_ASIGNATURAS + "(" + COL_ASIG_ID + "))";
        db.execSQL(createTareas);

        // Crear sesiones de estudio
        String createSesiones = "CREATE TABLE " + TABLE_SESIONES + " ("
                + COL_SES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_SES_FECHA + " TEXT NOT NULL, "
                + COL_SES_DURACION + " INTEGER DEFAULT 0, "
                + COL_SES_ASIG_ID + " INTEGER, "
                + "FOREIGN KEY(" + COL_SES_ASIG_ID + ") REFERENCES " + TABLE_ASIGNATURAS + "(" + COL_ASIG_ID + "))";
        db.execSQL(createSesiones);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESIONES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAREAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASIGNATURAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        onCreate(db);
    }

    // USUARIOS

    public long registrarUsuario(String nombre, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_NOMBRE, nombre);
        values.put(COL_USER_EMAIL, email);
        values.put(COL_USER_PASSWORD, password);
        return db.insert(TABLE_USUARIOS, null, values);
    }

    public Cursor loginUsuario(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USUARIOS, null,
                COL_USER_EMAIL + "=? AND " + COL_USER_PASSWORD + "=?",
                new String[]{email, password}, null, null, null);
    }

    public boolean emailExiste(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USUARIOS, new String[]{COL_USER_ID},
                COL_USER_EMAIL + "=?", new String[]{email}, null, null, null);
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        return existe;
    }

    // ASIGNATURAS

    public long crearAsignatura(String nombre, String profesor, String color, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ASIG_NOMBRE, nombre);
        values.put(COL_ASIG_PROFESOR, profesor);
        values.put(COL_ASIG_COLOR, color);
        values.put(COL_ASIG_USER_ID, userId);
        return db.insert(TABLE_ASIGNATURAS, null, values);
    }

    public Cursor getAsignaturas(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_ASIGNATURAS, null,
                COL_ASIG_USER_ID + "=?",
                new String[]{String.valueOf(userId)}, null, null, COL_ASIG_NOMBRE + " ASC");
    }

    public int editarAsignatura(int id, String nombre, String profesor, String color) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ASIG_NOMBRE, nombre);
        values.put(COL_ASIG_PROFESOR, profesor);
        values.put(COL_ASIG_COLOR, color);
        return db.update(TABLE_ASIGNATURAS, values, COL_ASIG_ID + "=?", new String[]{String.valueOf(id)});
    }

    public void eliminarAsignatura(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TAREAS, COL_TAREA_ASIG_ID + "=?", new String[]{String.valueOf(id)});
        db.delete(TABLE_SESIONES, COL_SES_ASIG_ID + "=?", new String[]{String.valueOf(id)});
        db.delete(TABLE_ASIGNATURAS, COL_ASIG_ID + "=?", new String[]{String.valueOf(id)});
    }

    // TAREAS

    public long crearTarea(String titulo, String descripcion, String fecha, String tipo, int asigId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TAREA_TITULO, titulo);
        values.put(COL_TAREA_DESCRIPCION, descripcion);
        values.put(COL_TAREA_FECHA, fecha);
        values.put(COL_TAREA_TIPO, tipo);
        values.put(COL_TAREA_ASIG_ID, asigId);
        return db.insert(TABLE_TAREAS, null, values);
    }

    public Cursor getTareasPorAsignatura(int asigId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_TAREAS, null,
                COL_TAREA_ASIG_ID + "=?",
                new String[]{String.valueOf(asigId)}, null, null, COL_TAREA_FECHA + " ASC");
    }

    public Cursor getTareasPorUsuario(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT t.*, a." + COL_ASIG_NOMBRE + " AS nombre_asignatura, a." + COL_ASIG_COLOR + " AS color_asignatura "
                + "FROM " + TABLE_TAREAS + " t "
                + "INNER JOIN " + TABLE_ASIGNATURAS + " a ON t." + COL_TAREA_ASIG_ID + " = a." + COL_ASIG_ID + " "
                + "WHERE a." + COL_ASIG_USER_ID + " = ? "
                + "ORDER BY t." + COL_TAREA_FECHA + " ASC";
        return db.rawQuery(query, new String[]{String.valueOf(userId)});
    }

    public void marcarTareaCompletada(int tareaId, boolean completada) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TAREA_COMPLETADA, completada ? 1 : 0);
        db.update(TABLE_TAREAS, values, COL_TAREA_ID + "=?", new String[]{String.valueOf(tareaId)});
    }

    public void eliminarTarea(int tareaId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TAREAS, COL_TAREA_ID + "=?", new String[]{String.valueOf(tareaId)});
    }

    // SESIONES DE ESTUDIO

    public long registrarSesion(String fecha, int duracionMin, int asigId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_SES_FECHA, fecha);
        values.put(COL_SES_DURACION, duracionMin);
        values.put(COL_SES_ASIG_ID, asigId);
        return db.insert(TABLE_SESIONES, null, values);
    }

    public int getTotalMinutosPorUsuario(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(s." + COL_SES_DURACION + ") FROM " + TABLE_SESIONES + " s "
                + "INNER JOIN " + TABLE_ASIGNATURAS + " a ON s." + COL_SES_ASIG_ID + " = a." + COL_ASIG_ID + " "
                + "WHERE a." + COL_ASIG_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        int total = 0;
        if (cursor.moveToFirst()) total = cursor.getInt(0);
        cursor.close();
        return total;
    }

    public Cursor getSesionesPorAsignatura(int asigId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_SESIONES, null,
                COL_SES_ASIG_ID + "=?",
                new String[]{String.valueOf(asigId)}, null, null, COL_SES_FECHA + " DESC");
    }

    public int getTareasCompletadas(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_TAREAS + " t "
                + "INNER JOIN " + TABLE_ASIGNATURAS + " a ON t." + COL_TAREA_ASIG_ID + " = a." + COL_ASIG_ID + " "
                + "WHERE a." + COL_ASIG_USER_ID + " = ? AND t." + COL_TAREA_COMPLETADA + " = 1";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public int getTotalTareas(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_TAREAS + " t "
                + "INNER JOIN " + TABLE_ASIGNATURAS + " a ON t." + COL_TAREA_ASIG_ID + " = a." + COL_ASIG_ID + " "
                + "WHERE a." + COL_ASIG_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        return count;
    }
}
