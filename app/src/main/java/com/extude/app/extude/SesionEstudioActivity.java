package com.extude.app.extude;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.*;

public class SesionEstudioActivity extends AppCompatActivity {
//No falla y no se, revisarlo con apuntes
    private TextView tvTiempoRestante, tvEstado;
    private Button btnIniciar, btnPausar, btnDetener;
    private Spinner spAsignatura;
    private SeekBar sbMinutos;
    private TextView tvMinutosSeleccionados;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    private CountDownTimer countDownTimer;
    private boolean isRunning = false;
    private long tiempoRestanteMs = 0;
    private long tiempoTotalMs = 0;
    private int minutosSeleccionados = 30;

    private List<String[]> asignaturas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sesion_estudio);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        tvTiempoRestante     = findViewById(R.id.tv_tiempo_restante);
        tvEstado             = findViewById(R.id.tv_estado_sesion);
        btnIniciar           = findViewById(R.id.btn_iniciar);
        btnPausar            = findViewById(R.id.btn_pausar);// va cuando quiere, bottarlo sino lo soluciono
        btnDetener           = findViewById(R.id.btn_detener);
        spAsignatura         = findViewById(R.id.sp_asignatura_sesion);
        sbMinutos            = findViewById(R.id.sb_minutos);
        tvMinutosSeleccionados = findViewById(R.id.tv_minutos_sel);

        asignaturas = new ArrayList<>();
        cargarAsignaturas();

        sbMinutos.setMax(120);
        sbMinutos.setProgress(30);
        tvMinutosSeleccionados.setText("30 min");

        sbMinutos.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar sb, int progress, boolean user) {
                minutosSeleccionados = Math.max(1, progress);
                tvMinutosSeleccionados.setText(minutosSeleccionados + " min");
                actualizarDisplay(minutosSeleccionados * 60 * 1000L);
            }
            @Override public void onStartTrackingTouch(SeekBar sb) {}
            @Override public void onStopTrackingTouch(SeekBar sb) {}
        });

        actualizarDisplay(30 * 60 * 1000L);

        btnIniciar.setOnClickListener(v -> iniciarSesion());
        btnPausar.setOnClickListener(v -> pausarSesion());
        btnDetener.setOnClickListener(v -> detenerSesion());

        btnPausar.setEnabled(false);
        btnDetener.setEnabled(false);
    }
      // cargar asignaturas
    private void cargarAsignaturas() {
        asignaturas.clear();
        Cursor cursor = dbHelper.getAsignaturas(sessionManager.getUserId());
        List<String> nombres = new ArrayList<>();
        while (cursor.moveToNext()) {
            asignaturas.add(new String[]{
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ASIG_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ASIG_NOMBRE))
            });
            nombres.add(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ASIG_NOMBRE)));
        }
        cursor.close();

        if (nombres.isEmpty()) nombres.add("Sin asignatura, estás libre");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, nombres);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAsignatura.setAdapter(adapter);
    }

    private void iniciarSesion() {
        if (!isRunning) {
            tiempoTotalMs = tiempoRestanteMs > 0 ? tiempoRestanteMs : minutosSeleccionados * 60 * 1000L;
            countDownTimer = new CountDownTimer(tiempoTotalMs, 1000) {
                @Override public void onTick(long msLeft) {
                    tiempoRestanteMs = msLeft;
                    actualizarDisplay(msLeft);
                }
                @Override public void onFinish() {
                    tiempoRestanteMs = 0;
                    tvTiempoRestante.setText("00:00");
                    tvEstado.setText("¡Bien Hecho, sigue asi! 🎉");
                    guardarSesion(minutosSeleccionados);
                    resetearBotones();
                }
            }.start();

            isRunning = true;
            tvEstado.setText("⏱ Estudiando...");
            btnIniciar.setEnabled(false);
            btnPausar.setEnabled(true);
            btnDetener.setEnabled(true);
            sbMinutos.setEnabled(false);
        }
    }

    private void pausarSesion() {
        if (isRunning) {
            countDownTimer.cancel();
            isRunning = false;
            tvEstado.setText("⏸ En pausa");
            btnIniciar.setEnabled(true);
            btnPausar.setEnabled(false);
        }
    }

    private void detenerSesion() {
        if (countDownTimer != null) countDownTimer.cancel();
        int minutosEstudiados = (int) ((tiempoTotalMs - tiempoRestanteMs) / 60000);
        if (minutosEstudiados > 0) guardarSesion(minutosEstudiados);
        tiempoRestanteMs = 0;
        isRunning = false;
        actualizarDisplay(minutosSeleccionados * 60 * 1000L);
        tvEstado.setText("Sesión detenida");
        resetearBotones();
    }

    private void guardarSesion(int minutos) {
        if (asignaturas.isEmpty()) return;
        int idx = spAsignatura.getSelectedItemPosition();
        if (idx < 0 || idx >= asignaturas.size()) return;
        int asigId = Integer.parseInt(asignaturas.get(idx)[0]);
        String fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        dbHelper.registrarSesion(fecha, minutos, asigId);
        Toast.makeText(this, "Sesión guardada: " + minutos + " min", Toast.LENGTH_SHORT).show();
    }

    private void actualizarDisplay(long ms) {
        long segundos = ms / 1000;
        long min = segundos / 60;
        long seg = segundos % 60;
        tvTiempoRestante.setText(String.format(Locale.getDefault(), "%02d:%02d", min, seg));
    }

    private void resetearBotones() {
        isRunning = false;
        btnIniciar.setEnabled(true);
        btnPausar.setEnabled(false);
        btnDetener.setEnabled(false);
        sbMinutos.setEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}