package com.extude.app.extude;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EstadisticasActivity extends AppCompatActivity {

    private TextView tvTotalHoras, tvTareasCompletadas, tvTotalTareas,
            tvTotalAsignaturas, tvProgresoTexto, tvExamenesPendientes;
    private ProgressBar pbProgreso;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        tvTotalHoras         = findViewById(R.id.tv_stat_horas);
        tvTareasCompletadas  = findViewById(R.id.tv_stat_completadas);
        tvTotalTareas        = findViewById(R.id.tv_stat_total_tareas);
        tvTotalAsignaturas   = findViewById(R.id.tv_stat_asignaturas);
        tvProgresoTexto      = findViewById(R.id.tv_progreso_texto);
        tvExamenesPendientes = findViewById(R.id.tv_examenes_pendientes);
        pbProgreso           = findViewById(R.id.pb_progreso);

        cargarEstadisticas();
    }
    // intentar poner estadisticas en grafica o estrellas
    private void cargarEstadisticas() {
        int userId = sessionManager.getUserId();

        // Total horas
        int totalMin = dbHelper.getTotalMinutosPorUsuario(userId);
        int horas = totalMin / 60;
        int minutos = totalMin % 60;
        tvTotalHoras.setText(horas + "h " + minutos + "m");

        // Tareas
        int completadas = dbHelper.getTareasCompletadas(userId);
        int total = dbHelper.getTotalTareas(userId);
        tvTareasCompletadas.setText(String.valueOf(completadas));
        tvTotalTareas.setText("de " + total);

        // Progreso
        int progreso = total > 0 ? (int) ((completadas * 100.0) / total) : 0;
        pbProgreso.setProgress(progreso);
        tvProgresoTexto.setText(progreso + "% completado");

        // Asignaturas
        Cursor asigCursor = dbHelper.getAsignaturas(userId);
        tvTotalAsignaturas.setText(String.valueOf(asigCursor.getCount()));
        asigCursor.close();

        // Examenes pendientes
        Cursor tareasCursor = dbHelper.getTareasPorUsuario(userId);
        int examenesPendientes = 0;
        while (tareasCursor.moveToNext()) {
            String tipo = tareasCursor.getString(
                    tareasCursor.getColumnIndexOrThrow(DatabaseHelper.COL_TAREA_TIPO));
            String completada = tareasCursor.getString(
                    tareasCursor.getColumnIndexOrThrow(DatabaseHelper.COL_TAREA_COMPLETADA));
            if ("examen".equals(tipo) && "0".equals(completada)) {
                examenesPendientes++;
            }
        }
        tareasCursor.close();
        tvExamenesPendientes.setText(String.valueOf(examenesPendientes));
    }   //Añadir apartado de notas si da tiempo
}