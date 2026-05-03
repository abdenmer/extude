package com.extude.app.extude;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvBienvenida, tvTotalHoras, tvTareasCompletadas, tvTotalAsig;
    private CardView cardAsignaturas, cardTareas, cardSesion, cardEstadisticas;
    private ImageView ivLogo;
    private Button btnLogout;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        tvBienvenida       = findViewById(R.id.tv_bienvenida);
        tvTotalHoras       = findViewById(R.id.tv_total_horas);
        tvTareasCompletadas = findViewById(R.id.tv_tareas_completadas);
        tvTotalAsig        = findViewById(R.id.tv_total_asig);
        cardAsignaturas    = findViewById(R.id.card_asignaturas);
        cardTareas         = findViewById(R.id.card_tareas);
        cardSesion         = findViewById(R.id.card_sesion);
        cardEstadisticas   = findViewById(R.id.card_estadisticas);
        btnLogout          = findViewById(R.id.btn_logout);

        tvBienvenida.setText("¡Hola, " + sessionManager.getUserName() + "!");

        actualizarEstadisticas();

        cardAsignaturas.setOnClickListener(v ->
                startActivity(new Intent(this, AsignaturasActivity.class)));

        cardTareas.setOnClickListener(v ->
                startActivity(new Intent(this, TareasActivity.class)));

        cardSesion.setOnClickListener(v ->
                startActivity(new Intent(this, SesionEstudioActivity.class)));

        cardEstadisticas.setOnClickListener(v ->
                startActivity(new Intent(this, EstadisticasActivity.class)));

        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarEstadisticas();
    }

    private void actualizarEstadisticas() {
        int userId = sessionManager.getUserId();

        int totalMin = dbHelper.getTotalMinutosPorUsuario(userId);
        int horas = totalMin / 60;
        int minutos = totalMin % 60;
        tvTotalHoras.setText(horas + "h " + minutos + "m");

        int completadas = dbHelper.getTareasCompletadas(userId);
        int total = dbHelper.getTotalTareas(userId);
        tvTareasCompletadas.setText(completadas + "/" + total);

        Cursor cursor = dbHelper.getAsignaturas(userId);
        tvTotalAsig.setText(String.valueOf(cursor.getCount()));
        cursor.close();
    }
}