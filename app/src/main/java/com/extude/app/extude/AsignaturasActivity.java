package com.extude.app.extude;
import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class AsignaturasActivity extends AppCompatActivity {

    private ListView lvAsignaturas;
    private Button btnAgregar;
    private TextView tvEmpty;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private List<String[]> listaAsignaturas; // id, nombre, profesor, color
    private ArrayAdapter<String> adapter;
    private List<String> nombresDisplay;

    // Colores de las assignaturas, creo que no se ven consultar si los colores estan bien
    private final String[] colores = {
            "#7C3AED", "#2563EB", "#DC2626", "#059669",
            "#D97706", "#DB2777", "#0891B2", "#65A30D"
    };
    private final String[] nombresColores = {
            "Violeta", "Azul", "Rojo", "Verde",
            "Naranja", "Rosa", "Cian", "Lima"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asignaturas);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        lvAsignaturas = findViewById(R.id.lv_asignaturas);
        btnAgregar    = findViewById(R.id.btn_agregar_asignatura);
        tvEmpty       = findViewById(R.id.tv_empty_asig);

        listaAsignaturas = new ArrayList<>();
        nombresDisplay   = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nombresDisplay);
        lvAsignaturas.setAdapter(adapter);

        cargarAsignaturas();

        btnAgregar.setOnClickListener(v -> mostrarDialogoAsignatura(null));

        lvAsignaturas.setOnItemClickListener((parent, view, position, id) -> {
            String[] asig = listaAsignaturas.get(position);
            mostrarOpcionesAsignatura(asig, position);
        });
    }

    //introducir las asignaturas
    private void cargarAsignaturas() {
        listaAsignaturas.clear();
        nombresDisplay.clear();

        Cursor cursor = dbHelper.getAsignaturas(sessionManager.getUserId());
        while (cursor.moveToNext()) {
            String[] asig = {
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ASIG_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ASIG_NOMBRE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ASIG_PROFESOR)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ASIG_COLOR))
            };
            listaAsignaturas.add(asig);
            String profesor = asig[2] != null && !asig[2].isEmpty() ? " · " + asig[2] : "";
            nombresDisplay.add(asig[1] + profesor);
        }
        cursor.close();

        tvEmpty.setVisibility(listaAsignaturas.isEmpty() ? View.VISIBLE : View.GONE);
        adapter.notifyDataSetChanged();
    }

    private void mostrarDialogoAsignatura(String[] asigEditar) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(asigEditar == null ? "Nueva asignatura" : "Editar asignatura");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_asignatura, null);
        builder.setView(view);

        EditText etNombre   = view.findViewById(R.id.et_nombre_asig);
        EditText etProfesor = view.findViewById(R.id.et_profesor);
        Spinner spColor     = view.findViewById(R.id.sp_color);

        ArrayAdapter<String> colorAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, nombresColores);
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spColor.setAdapter(colorAdapter);

        if (asigEditar != null) {
            etNombre.setText(asigEditar[1]);
            etProfesor.setText(asigEditar[2]);
            for (int i = 0; i < colores.length; i++) {
                if (colores[i].equals(asigEditar[3])) {
                    spColor.setSelection(i);
                    break;
                }
            }
        }

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nombre   = etNombre.getText().toString().trim();
            String profesor = etProfesor.getText().toString().trim();
            String color    = colores[spColor.getSelectedItemPosition()];

            if (nombre.isEmpty()) {
                Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
                return;
            }

            if (asigEditar == null) {
                dbHelper.crearAsignatura(nombre, profesor, color, sessionManager.getUserId());
                Toast.makeText(this, "Asignatura creada", Toast.LENGTH_SHORT).show();
            } else {
                dbHelper.editarAsignatura(Integer.parseInt(asigEditar[0]), nombre, profesor, color);
                Toast.makeText(this, "Asignatura actualizada", Toast.LENGTH_SHORT).show();
            }
            cargarAsignaturas();
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    //Editar y borrar asignaturas( intentar ponerle puntuacion)
    private void mostrarOpcionesAsignatura(String[] asig, int position) {
        String[] opciones = {"Editar", "Eliminar"};
        new AlertDialog.Builder(this)
                .setTitle(asig[1])
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) {
                        mostrarDialogoAsignatura(asig);
                    } else {
                        new AlertDialog.Builder(this)
                                .setTitle("Eliminar asignatura")
                                .setMessage("¿Eliminar \"" + asig[1] + "\" y todas sus tareas?")
                                .setPositiveButton("Eliminar", (d, w) -> {
                                    dbHelper.eliminarAsignatura(Integer.parseInt(asig[0]));
                                    cargarAsignaturas();
                                    Toast.makeText(this, "Asignatura eliminada", Toast.LENGTH_SHORT).show();
                                })
                                .setNegativeButton("Cancelar", null)
                                .show();
                    }
                }).show();
    }
}