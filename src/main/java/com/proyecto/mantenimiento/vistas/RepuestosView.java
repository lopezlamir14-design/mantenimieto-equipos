package com.proyecto.mantenimiento.vistas;

import com.proyecto.mantenimiento.datos.GestorDatos;
import com.proyecto.mantenimiento.datos.Validador;
import com.proyecto.mantenimiento.modelo.Repuesto;
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
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Route(value = "repuestos", layout = MainLayout.class)
@PageTitle("Repuestos")
public class RepuestosView extends VerticalLayout {

    private Grid<Repuesto> grid = new Grid<>(Repuesto.class, false);
    private TextField buscador = new TextField();

    public RepuestosView() {
        setSizeFull();

        buscador.setPlaceholder("Buscar repuesto por nombre");
        buscador.setClearButtonVisible(true);
        buscador.setValueChangeMode(ValueChangeMode.EAGER);
        buscador.addValueChangeListener(e -> aplicarFiltros());

        Button nuevo = new Button("Nuevo repuesto", e -> abrirFormulario(null));
        nuevo.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout barra = new HorizontalLayout(buscador, nuevo);
        barra.getStyle().set("flex-wrap", "wrap");

        grid.addColumn(Repuesto::getNombre).setHeader("Nombre").setSortable(true);
        grid.addColumn(Repuesto::getStock).setHeader("Stock").setSortable(true);
        grid.addColumn(r -> "$ " + String.format("%.0f", r.getPrecio())).setHeader("Precio");
        grid.addComponentColumn(repuesto -> {
            Button editar = new Button(VaadinIcon.EDIT.create(), e -> abrirFormulario(repuesto));
            Button borrar = new Button(VaadinIcon.TRASH.create(), e -> confirmarBorrado(repuesto));
            borrar.addThemeVariants(ButtonVariant.LUMO_ERROR);
            return new HorizontalLayout(editar, borrar);
        }).setHeader("Acciones");

        add(barra, grid);
        aplicarFiltros();
    }

    private void aplicarFiltros() {
        String texto = buscador.getValue().trim().toLowerCase();
        List<Repuesto> resultado = new ArrayList<>();

        for (Repuesto r : GestorDatos.getRepuestos()) {
            if (texto.isEmpty() || r.getNombre().toLowerCase().contains(texto)) {
                resultado.add(r);
            }
        }
        grid.setItems(resultado);
    }

    private void confirmarBorrado(Repuesto repuesto) {
        if (GestorDatos.repuestoTieneOrdenes(repuesto.getId())) {
            Notification.show("No se puede eliminar: el repuesto se usa en órdenes de trabajo");
            return;
        }

        ConfirmDialog dialogo = new ConfirmDialog();
        dialogo.setHeader("Eliminar repuesto");
        dialogo.setText("¿Eliminar " + repuesto.getNombre() + "? Esta acción no se puede deshacer.");
        dialogo.setCancelable(true);
        dialogo.setConfirmText("Eliminar");
        dialogo.addConfirmListener(e -> {
            GestorDatos.getRepuestos().remove(repuesto);
            try {
                GestorDatos.guardarRepuestos();
                Notification.show("Repuesto eliminado");
            } catch (IOException ex) {
                Notification.show("Error al guardar: " + ex.getMessage());
            }
            aplicarFiltros();
        });
        dialogo.open();
    }

    private void abrirFormulario(Repuesto existente) {
        Dialog dialog = new Dialog();
        if (existente == null) {
            dialog.setHeaderTitle("Nuevo repuesto");
        } 
        else {
            dialog.setHeaderTitle("Editar repuesto");
        }

        TextField nombre = new TextField("Nombre");
        IntegerField stock = new IntegerField("Stock");
        stock.setMin(0);
        NumberField precio = new NumberField("Precio");
        precio.setMin(0);

        if (existente != null) {
            nombre.setValue(existente.getNombre());
            stock.setValue(existente.getStock());
            precio.setValue(existente.getPrecio());
        }

        FormLayout formulario = new FormLayout(nombre, stock, precio);
        formulario.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2));

        Button guardar = new Button("Guardar", e -> {
            String error = validar(nombre.getValue(), stock.getValue(), precio.getValue());
            if (error != null) {
                Notification.show(error);
                return;
            }

            if (existente == null) {
                Repuesto nuevo = new Repuesto(GestorDatos.siguienteIdRepuesto(),
                        nombre.getValue().trim(), stock.getValue(), precio.getValue());
                GestorDatos.getRepuestos().add(nuevo);
            } else {
                existente.setNombre(nombre.getValue().trim());
                existente.setStock(stock.getValue());
                existente.setPrecio(precio.getValue());
            }

            try {
                GestorDatos.guardarRepuestos();
                Notification.show("Repuesto guardado");
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

    private String validar(String nombre, Integer stock, Double precio) {
        String error = Validador.texto(nombre, "El nombre");
        if (error != null) return error;

        if (stock == null || stock < 0) return "El stock debe ser un número entero de 0 o más";
        if (precio == null || precio <= 0) return "El precio debe ser mayor que 0";
        return null;
    }
}