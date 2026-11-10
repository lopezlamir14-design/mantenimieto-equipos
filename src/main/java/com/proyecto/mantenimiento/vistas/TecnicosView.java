package com.proyecto.mantenimiento.vistas;
import com.proyecto.mantenimiento.datos.GestorDatos;
import com.proyecto.mantenimiento.datos.Validador;
import com.proyecto.mantenimiento.modelo.Tecnico;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Route(value = "tecnicos", layout = MainLayout.class)
@PageTitle("Técnicos")
public class TecnicosView extends VerticalLayout {

    private Grid<Tecnico> grid = new Grid<>(Tecnico.class, false);
    private TextField buscador = new TextField();

    public TecnicosView() {
        setSizeFull();

        buscador.setPlaceholder("Buscar por nombre o especialidad");
        buscador.setClearButtonVisible(true);
        buscador.setValueChangeMode(ValueChangeMode.EAGER);
        buscador.addValueChangeListener(e -> aplicarFiltros());

        Button nuevo = new Button("Nuevo técnico", e -> abrirFormulario(null));
        nuevo.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout barra = new HorizontalLayout(buscador, nuevo);
        barra.getStyle().set("flex-wrap", "wrap");

        grid.addColumn(Tecnico::getNombre).setHeader("Nombre").setSortable(true);
        grid.addColumn(Tecnico::getTelefono).setHeader("Teléfono");
        grid.addColumn(Tecnico::getEspecialidad).setHeader("Especialidad").setSortable(true);
        grid.addComponentColumn(tecnico -> {
            Button editar = new Button(VaadinIcon.EDIT.create(), e -> abrirFormulario(tecnico));
            Button borrar = new Button(VaadinIcon.TRASH.create(), e -> confirmarBorrado(tecnico));
            borrar.addThemeVariants(ButtonVariant.LUMO_ERROR);
            return new HorizontalLayout(editar, borrar);
        }).setHeader("Acciones");

        add(barra, grid);
        aplicarFiltros();
    }

    private void aplicarFiltros() {
        String texto = buscador.getValue().trim().toLowerCase();
        List<Tecnico> resultado = new ArrayList<>();

        for (Tecnico t : GestorDatos.getTecnicos()) {
            if (texto.isEmpty()
                    || t.getNombre().toLowerCase().contains(texto)
                    || t.getEspecialidad().toLowerCase().contains(texto)) {
                resultado.add(t);
            }
        }
        grid.setItems(resultado);
    }

    private void confirmarBorrado(Tecnico tecnico) {
        // No se puede borrar un técnico que ya tiene órdenes
        if (GestorDatos.tecnicoTieneOrdenes(tecnico.getId())) {
            Notification.show("No se puede eliminar: el técnico tiene órdenes de trabajo");
            return;
        }

        ConfirmDialog dialogo = new ConfirmDialog();
        dialogo.setHeader("Eliminar técnico");
        dialogo.setText("¿Eliminar a " + tecnico.getNombre() + "? Esta acción no se puede deshacer.");
        dialogo.setCancelable(true);
        dialogo.setConfirmText("Eliminar");
        dialogo.addConfirmListener(e -> {
            GestorDatos.getTecnicos().remove(tecnico);
            try {
                GestorDatos.guardarTecnicos();
                Notification.show("Técnico eliminado");
            } 
            catch (IOException ex) {
                Notification.show("Error al guardar: " + ex.getMessage());
            }
            aplicarFiltros();
        });
        dialogo.open();
    }

    private void abrirFormulario(Tecnico existente) {
        Dialog dialog = new Dialog();
        if (existente == null) {
            dialog.setHeaderTitle("Nuevo técnico");
        } 
        else {
            dialog.setHeaderTitle("Editar técnico");
        }

        TextField nombre = new TextField("Nombre");
        TextField telefono = new TextField("Teléfono");
        TextField especialidad = new TextField("Especialidad");

        if (existente != null) {
            nombre.setValue(existente.getNombre());
            telefono.setValue(existente.getTelefono());
            especialidad.setValue(existente.getEspecialidad());
        }

        FormLayout formulario = new FormLayout(nombre, telefono, especialidad);
        formulario.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2));

        Button guardar = new Button("Guardar", e -> {
            String error = validar(nombre.getValue(), telefono.getValue(), especialidad.getValue());
            if (error != null) {
                Notification.show(error);
                return;
            }

            if (existente == null) {
                Tecnico nuevo = new Tecnico(GestorDatos.siguienteIdTecnico(), nombre.getValue().trim(),
                        telefono.getValue().trim(), especialidad.getValue().trim());
                GestorDatos.getTecnicos().add(nuevo);
            } 
            else {
                existente.setNombre(nombre.getValue().trim());
                existente.setTelefono(telefono.getValue().trim());
                existente.setEspecialidad(especialidad.getValue().trim());
            }

            try {
                GestorDatos.guardarTecnicos();
                Notification.show("Técnico guardado");
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

    private String validar(String nombre, String telefono, String especialidad) {
        String error = Validador.texto(nombre, "El nombre");
        if (error != null) return error;

        error = Validador.texto(telefono, "El teléfono");
        if (error != null) return error;

        if (!telefono.trim().matches("[0-9]{7,10}")) {
            return "El teléfono debe tener entre 7 y 10 dígitos";
        }

        return Validador.texto(especialidad, "La especialidad");
    }
}