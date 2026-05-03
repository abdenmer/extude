package com.extude.app.extude;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.*;

public class TareasActivity extends AppCompatActivity {

    private ListView lvTareas;
    private Button btnAgregarTarea;
    private TextView tvEmpty;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    private List<String[]> listaTareas; // [id, titulo, descripcion, fecha, tipo, completada, asigId, nombreAsig]
    private ArrayAdapter<String> adapter;
    private List<String> displayList;

    private List<String[]> asignaturas; // [id, nombre] para el spinner

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tareas);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        lvTareas       = findViewById(R.id.lv_tareas);
        btnAgregarTarea = findViewById(R.id.btn_agregar_tarea);
        tvEmpty        = findViewById(R.id.tv_empty_tareas);

        listaTareas = new ArrayList<>();
        displayList = new ArrayList<>();
        asignaturas = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        lvTareas.setAdapter(adapter);

        cargarAsignaturas();
        cargarTareas();

        btnAgregarTarea.setOnClickListener(v -> {
            if (asignaturas.isEmpty()) {
                Toast.makeText(this, "Primero crea una asignatura", Toast.LENGTH_SHORT).show();
            } else {
                mostrarDialogoTarea(null);
            }
        });

        lvTareas.setOnItemClickListener((parent, view, position, id) ->
                mostrarOpcionesTarea(listaTareas.get(position), position));
    }

    private void cargarAsignaturas() {
        asignaturas.clear();
        Cursor cursor = dbHelper.getAsignaturas(sessionManager.getUserId());
        while (cursor.moveToNext()) {
            asignaturas.add(new String[]{
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ASIG_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ASIG_NOMBRE))
            });
        }
        cursor.close();
    }

    private void cargarTareas() {
        listaTareas.clear();
        displayList.clear();

        Cursor cursor = dbHelper.getTareasPorUsuario(sessionManager.getUserId());
        while (cursor.moveToNext()) {
            String[] tarea = {
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TAREA_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TAREA_TITULO)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TAREA_DESCRIPCION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TAREA_FECHA)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TAREA_TIPO)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TAREA_COMPLETADA)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TAREA_ASIG_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow("nombre_asignatura"))
            };
            listaTareas.add(tarea);

            String emoji = tarea[4].equals("examen") ? "📝" : "✅";
            String check = tarea[5].equals("1") ? " ✓" : "";
            String fecha = tarea[3] != null ? " · " + tarea[3] : "";
            displayList.add(emoji + " [" + tarea[7] + "] " + tarea[1] + fecha + check);
        }
        cursor.close();

        tvEmpty.setVisibility(listaTareas.isEmpty() ? View.VISIBLE : View.GONE);
        adapter.notifyDataSetChanged();
    }

    private void mostrarDialogoTarea(String[] tareaEditar) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(tareaEditar == null ? "Nueva tarea" : "Editar tarea");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_tarea, null);
        builder.setView(view);

        EditText etTitulo  = view.findViewById(R.id.et_titulo_tarea);
        EditText etDesc    = view.findViewById(R.id.et_descripcion_tarea);
        EditText etFecha   = view.findViewById(R.id.et_fecha_tarea);
        Spinner spTipo     = view.findViewById(R.id.sp_tipo_tarea);
        Spinner spAsig     = view.findViewById(R.id.sp_asignatura_tarea);

        // Spinner tipo
        ArrayAdapter<String> tipoAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new String[]{"tarea", "examen"});
        tipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipo.setAdapter(tipoAdapter);

        // Spinner asignaturas
        List<String> nombresAsig = new ArrayList<>();
        for (String[] a : asignaturas) nombresAsig.add(a[1]);
        ArrayAdapter<String> asigAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, nombresAsig);
        asigAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAsig.setAdapter(asigAdapter);

        // Selector de fecha
        etFecha.setFocusable(false);
        etFecha.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this, (datePicker, y, m, d) -> {
                etFecha.setText(String.format("%04d-%02d-%02d", y, m + 1, d));
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        if (tareaEditar != null) {
            etTitulo.setText(tareaEditar[1]);
            etDesc.setText(tareaEditar[2]);
            etFecha.setText(tareaEditar[3]);
            spTipo.setSelection(tareaEditar[4].equals("examen") ? 1 : 0);
            for (int i = 0; i < asignaturas.size(); i++) {
                if (asignaturas.get(i)[0].equals(tareaEditar[6])) {
                    spAsig.setSelection(i);
                    break;
                }
            }
        }

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String titulo = etTitulo.getText().toString().trim();
            String desc   = etDesc.getText().toString().trim();
            String fecha  = etFecha.getText().toString().trim();
            String tipo   = spTipo.getSelectedItem().toString();
            int asigIdx   = spAsig.getSelectedItemPosition();
            int asigId    = Integer.parseInt(asignaturas.get(asigIdx)[0]);

            if (titulo.isEmpty()) {
                Toast.makeText(this, "El título es obligatorio", Toast.LENGTH_SHORT).show();
                return;
            }

            dbHelper.crearTarea(titulo, desc, fecha, tipo, asigId);
            Toast.makeText(this, "Tarea guardada", Toast.LENGTH_SHORT).show();
            cargarTareas();
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void mostrarOpcionesTarea(String[] tarea, int position) {
        boolean completada = tarea[5].equals("1");
        String[] opciones = {completada ? "Marcar incompleta" : "Marcar completada", "Eliminar"};
        new AlertDialog.Builder(this)
                .setTitle(tarea[1])
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) {
                        dbHelper.marcarTareaCompletada(Integer.parseInt(tarea[0]), !completada);
                        cargarTareas();
                    } else {
                        new AlertDialog.Builder(this)
                                .setTitle("Eliminar tarea")
                                .setMessage("¿Eliminar \"" + tarea[1] + "\"?")
                                .setPositiveButton("Eliminar", (d, w) -> {
                                    dbHelper.eliminarTarea(Integer.parseInt(tarea[0]));
                                    cargarTareas();
                                    Toast.makeText(this, "Tarea eliminada", Toast.LENGTH_SHORT).show();
                                })
                                .setNegativeButton("Cancelar", null).show();
                    }
                }).show();
    }
}