package com.extude.app.extude;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegistro;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        // Si ya está logueado, ir al Dashboard
        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegistro = findViewById(R.id.tv_registro);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Introduce tu email");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                etPassword.setError("Introduce tu contraseña");
                return;
            }

            Cursor cursor = dbHelper.loginUsuario(email, password);
            if (cursor != null && cursor.moveToFirst()) {
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_ID));
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_NOMBRE));
                cursor.close();

                sessionManager.createSession(userId, nombre, email);
                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
        });

        tvRegistro.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegistroActivity.class));
        });
    }
}
