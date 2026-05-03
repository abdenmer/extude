package com.extude.app.extude;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegistroActivity extends AppCompatActivity {

    private EditText etNombre, etEmail, etPassword, etConfirmar;
    private Button btnRegistrar;
    private TextView tvLogin;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        dbHelper = new DatabaseHelper(this);

        etNombre    = findViewById(R.id.et_nombre);
        etEmail     = findViewById(R.id.et_email);
        etPassword  = findViewById(R.id.et_password);
        etConfirmar = findViewById(R.id.et_confirmar);
        btnRegistrar = findViewById(R.id.btn_registrar);
        tvLogin     = findViewById(R.id.tv_login);

        btnRegistrar.setOnClickListener(v -> {
            String nombre    = etNombre.getText().toString().trim();
            String email     = etEmail.getText().toString().trim();
            String password  = etPassword.getText().toString().trim();
            String confirmar = etConfirmar.getText().toString().trim();

            if (TextUtils.isEmpty(nombre)) { etNombre.setError("Campo obligatorio"); return; }
            if (TextUtils.isEmpty(email))  { etEmail.setError("Campo obligatorio"); return; }
            if (TextUtils.isEmpty(password)) { etPassword.setError("Campo obligatorio"); return; }
            if (!password.equals(confirmar)) {
                etConfirmar.setError("Las contraseñas no coinciden");
                return;
            }
            if (dbHelper.emailExiste(email)) {
                etEmail.setError("Este email ya está registrado");
                return;
            }

            long id = dbHelper.registrarUsuario(nombre, email, password);
            if (id != -1) {
                Toast.makeText(this, "¡Registrado! Inicia sesión", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegistroActivity.this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Error al registrar.", Toast.LENGTH_SHORT).show();
            }
        });

        tvLogin.setOnClickListener(v -> finish());
    }
}

