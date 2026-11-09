package com.proyecto.mantenimiento.vistas;
import com.proyecto.mantenimiento.datos.GestorDatos;
import com.proyecto.mantenimiento.datos.Validador;
import com.proyecto.mantenimiento.modelo.Equipo;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Equipos")
public class EquiposView extends VerticalLayout {

    private static final List<String> ESTADOS =
            List.of("Operativo", "En mantenimiento", "Fuera de servicio");
    private static final List<String> ESTADOS_FILTRO =
            List.of("Todos", "Operativo", "En mantenimiento", "Fuera de servicio");

    private Grid<Equipo> grid = new Grid<>(Equipo.class, false);
    private TextField buscador = new TextField();
    private Select<String> filtroEstado = new Select<>();

    public EquiposView() {
        setSizeFull();

        
        buscador.setPlaceholder("Buscar por código, nombre o ubicación");
        buscador.setClearButtonVisible(true);
        buscador.setValueChangeMode(ValueChangeMode.EAGER);
        buscador.addValueChangeListener(e -> aplicarFiltros());

        filtroEstado.setItems(ESTADOS_FILTRO);
        filtroEstado.setValue("Todos");
        filtroEstado.addValueChangeListener(e -> aplicarFiltros());

        Button nuevo = new Button("Nuevo equipo", e -> abrirFormulario(null));
        nuevo.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout barra = new HorizontalLayout(buscador, filtroEstado, nuevo);
        barra.getStyle().set("flex-wrap", "wrap");   // en el celular se acomoda en varias filas
        grid.addColumn(Equipo::getCodigo).setHeader("Código").setSortable(true);
        grid.addColumn(Equipo::getNombre).setHeader("Nombre").setSortable(true);
        grid.addColumn(Equipo::getUbicacion).setHeader("Ubicación");
        grid.addColumn(Equipo::getEstado).setHeader("Estado");
        grid.addComponentColumn(equipo -> {
            Button editar = new Button(VaadinIcon.EDIT.create(), e -> abrirFormulario(equipo));
            Button borrar = new Button(VaadinIcon.TRASH.create(), e -> confirmarBorrado(equipo));
            borrar.addThemeVariants(ButtonVariant.LUMO_ERROR);
            return new HorizontalLayout(editar, borrar);
        }).setHeader("Acciones");

        add(barra, grid);
        aplicarFiltros();
    }
    private void aplicarFiltros() {
        String texto = buscador.getValue().trim().toLowerCase();
        String estado = filtroEstado.getValue();
        List<Equipo> resultado = new ArrayList<>();

        for (Equipo e : GestorDatos.getEquipos()) {
            boolean coincideTexto = texto.isEmpty()
                    || e.getCodigo().toLowerCase().contains(texto)
                    || e.getNombre().toLowerCase().contains(texto)
                    || e.getUbicacion().toLowerCase().contains(texto);
            boolean coincideEstado = estado.equals("Todos") || e.getEstado().equals(estado);

            if (coincideTexto && coincideEstado) {
                resultado.add(e);
            }
        }
        grid.setItems(resultado);
    }
    private void confirmarBorrado(Equipo equipo) {
        ConfirmDialog dialogo = new ConfirmDialog();
        dialogo.setHeader("Eliminar equipo");
        dialogo.setText("¿Eliminar " + equipo.getNombre() + "? Esta acción no se puede deshacer.");
        dialogo.setCancelable(true);
        dialogo.setConfirmText("Eliminar");
        dialogo.addConfirmListener(e -> {
            GestorDatos.getEquipos().remove(equipo);
            try {
                GestorDatos.guardarEquipos();
                Notification.show("Equipo eliminado");
            } catch (IOException ex) {
                Notification.show("Error al guardar: " + ex.getMessage());
            }
            aplicarFiltros();
        });
        dialogo.open();
    }
    private void abrirFormulario(Equipo existente) {
        Dialog dialog = new Dialog();
        if (existente == null) {
            dialog.setHeaderTitle("Nuevo equipo");
        } else {
            dialog.setHeaderTitle("Editar equipo");
        }
        TextField codigo = new TextField("Código");
        TextField nombre = new TextField("Nombre");
        TextField ubicacion = new TextField("Ubicación");
        TextField modelo = new TextField("Modelo");
        DatePicker fecha = new DatePicker("Fecha de adquisición");
        Select<String> estado = new Select<>();
        estado.setLabel("Estado");
        estado.setItems(ESTADOS);
        if (existente != null) {
            codigo.setValue(existente.getCodigo());
            codigo.setReadOnly(true);   // el código no se cambia al editar
            nombre.setValue(existente.getNombre());
            ubicacion.setValue(existente.getUbicacion());
            modelo.setValue(existente.getModelo());
            fecha.setValue(existente.getFechaAdquisicion());
            estado.setValue(existente.getEstado());
        }
        FormLayout formulario = new FormLayout(codigo, nombre, ubicacion, modelo, fecha, estado);
        formulario.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2));
        Button guardar = new Button("Guardar", e -> {
            String error = validar(codigo.getValue(), nombre.getValue(), ubicacion.getValue(),
                    modelo.getValue(), fecha.getValue(), estado.getValue(), existente == null);
            if (error != null) {
                Notification.show(error);
            }
            if (existente == null) {
                Equipo nuevo = new Equipo(codigo.getValue().trim(), nombre.getValue().trim(),
                        ubicacion.getValue().trim(), modelo.getValue().trim(),
                        fecha.getValue(), estado.getValue());
                GestorDatos.getEquipos().add(nuevo);
            }
            else {
                existente.setNombre(nombre.getValue().trim());
                existente.setUbicacion(ubicacion.getValue().trim());
                existente.setModelo(modelo.getValue().trim());
                existente.setFechaAdquisicion(fecha.getValue());
                existente.setEstado(estado.getValue());
            }
            try {
                GestorDatos.guardarEquipos();
                Notification.show("Equipo guardado");
                dialog.close();
                aplicarFiltros();
            } catch (IOException ex) {
                Notification.show("Error al guardar: " + ex.getMessage());
            }
        });
        guardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancelar = new Button("Cancelar", e -> dialog.close());
        dialog.add(formulario);
        dialog.getFooter().add(cancelar, guardar);
        dialog.open();
    }
    private String validar(String codigo, String nombre, String ubicacion, String modelo,
            LocalDate fecha, String estado, boolean esNuevo) {
        String error = Validador.texto(codigo, "El código");
        if (error != null) return error;

        error = Validador.texto(nombre, "El nombre");
        if (error != null) return error;

        error = Validador.texto(ubicacion, "La ubicación");
        if (error != null) return error;

        error = Validador.texto(modelo, "El modelo");
        if (error != null) return error;

        if (fecha == null) return "La fecha es obligatoria";
        if (fecha.isAfter(LocalDate.now())) return "La fecha no puede ser futura";
        if (estado == null) return "Elige un estado";

        if (esNuevo && GestorDatos.buscarEquipo(codigo.trim()) != null) {
            return "Ya existe un equipo con ese código";
        }
        return null;
    }
}