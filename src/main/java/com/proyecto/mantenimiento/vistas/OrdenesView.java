package com.proyecto.mantenimiento.vistas;
import com.proyecto.mantenimiento.datos.GestorDatos;
import com.proyecto.mantenimiento.datos.Validador;
import com.proyecto.mantenimiento.modelo.Equipo;
import com.proyecto.mantenimiento.modelo.Orden;
import com.proyecto.mantenimiento.modelo.Repuesto;
import com.proyecto.mantenimiento.modelo.Tecnico;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Route(value = "ordenes", layout = MainLayout.class)
@PageTitle("Órdenes de trabajo")
public class OrdenesView extends VerticalLayout {

    private static final List<String> TIPOS = List.of("Preventivo", "Correctivo");
    private static final List<String> ESTADOS = List.of("Programada", "En proceso", "Cerrada");
    private static final List<String> ESTADOS_FILTRO = List.of("Todos", "Programada", "En proceso", "Cerrada");

    private Grid<Orden> grid = new Grid<>(Orden.class, false);
    private TextField buscador = new TextField();
    private Select<String> filtroEstado = new Select<>();

    public OrdenesView() {
        setSizeFull();

        buscador.setPlaceholder("Buscar por equipo, técnico o falla");
        buscador.setClearButtonVisible(true);
        buscador.setValueChangeMode(ValueChangeMode.EAGER);
        buscador.addValueChangeListener(e -> aplicarFiltros());

        filtroEstado.setItems(ESTADOS_FILTRO);
        filtroEstado.setValue("Todos");
        filtroEstado.addValueChangeListener(e -> aplicarFiltros());

        Button nueva = new Button("Nueva orden", e -> abrirFormulario(null));
        nueva.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout barra = new HorizontalLayout(buscador, filtroEstado, nueva);
        barra.getStyle().set("flex-wrap", "wrap");

        grid.addColumn(Orden::getId).setHeader("N°").setSortable(true);
        grid.addColumn(Orden::getEquipoCodigo).setHeader("Equipo").setSortable(true);

        grid.addComponentColumn(orden -> {
            String nombre = GestorDatos.nombreTecnico(orden.getTecnicoId());
            Avatar avatar = new Avatar(nombre);
            HorizontalLayout fila = new HorizontalLayout(avatar, new Span(nombre));
            fila.setAlignItems(FlexComponent.Alignment.CENTER);
            return fila;
        }).setHeader("Técnico");

        grid.addColumn(Orden::getTipo).setHeader("Tipo");
        grid.addColumn(Orden::getFecha).setHeader("Fecha").setSortable(true);
        grid.addColumn(Orden::getEstado).setHeader("Estado");
        grid.addColumn(o -> "$ " + String.format("%.0f", o.getCostoTotal())).setHeader("Costo total");
        grid.addComponentColumn(orden -> {
            Button editar = new Button(VaadinIcon.EDIT.create(), e -> abrirFormulario(orden));
            Button borrar = new Button(VaadinIcon.TRASH.create(), e -> confirmarBorrado(orden));
            borrar.addThemeVariants(ButtonVariant.LUMO_ERROR);
            return new HorizontalLayout(editar, borrar);
        }).setHeader("Acciones");

        add(barra, grid);
        aplicarFiltros();
    }

    private void aplicarFiltros() {
        String texto = buscador.getValue().trim().toLowerCase();
        String estado = filtroEstado.getValue();
        List<Orden> resultado = new ArrayList<>();

        for (Orden o : GestorDatos.getOrdenes()) {
            String tecnico = GestorDatos.nombreTecnico(o.getTecnicoId()).toLowerCase();
            boolean coincideTexto = texto.isEmpty()
                    || o.getEquipoCodigo().toLowerCase().contains(texto)
                    || tecnico.contains(texto)
                    || o.getFalla().toLowerCase().contains(texto);
            boolean coincideEstado = estado.equals("Todos") || o.getEstado().equals(estado);

            if (coincideTexto && coincideEstado) {
                resultado.add(o);
            }
        }
        grid.setItems(resultado);
    }

    private void confirmarBorrado(Orden orden) {
        String texto = "¿Eliminar la orden N° " + orden.getId() + "?";
        if (orden.getRepuestoId() != 0 && !orden.getEstado().equals("Cerrada")) {
            texto = texto + " El repuesto se devolverá al stock.";
        }

        ConfirmDialog dialogo = new ConfirmDialog();
        dialogo.setHeader("Eliminar orden");
        dialogo.setText(texto);
        dialogo.setCancelable(true);
        dialogo.setConfirmText("Eliminar");
        dialogo.addConfirmListener(e -> {
            try {
                GestorDatos.eliminarOrden(orden);
                Notification.show("Orden eliminada");
            } catch (IOException ex) {
                Notification.show("Error al guardar: " + ex.getMessage());
            }
            aplicarFiltros();
        });
        dialogo.open();
    }

    private void abrirFormulario(Orden existente) {
        Dialog dialog = new Dialog();
        if (existente == null) {
            dialog.setHeaderTitle("Nueva orden de trabajo");
        } else {
            dialog.setHeaderTitle("Editar orden N° " + existente.getId());
        }

        Select<Equipo> equipo = new Select<>();
        equipo.setLabel("Equipo");
        equipo.setItems(GestorDatos.getEquipos());
        equipo.setItemLabelGenerator(eq -> eq.getCodigo() + " - " + eq.getNombre());

        Select<Tecnico> tecnico = new Select<>();
        tecnico.setLabel("Técnico");
        tecnico.setItems(GestorDatos.getTecnicos());
        tecnico.setItemLabelGenerator(t -> t.getNombre());

        Select<String> tipo = new Select<>();
        tipo.setLabel("Tipo de mantenimiento");
        tipo.setItems(TIPOS);

        TextField falla = new TextField("Falla (obligatoria en correctivos)");
        DatePicker fecha = new DatePicker("Fecha programada");

        Select<String> estado = new Select<>();
        estado.setLabel("Estado");
        estado.setItems(ESTADOS);

        Select<Repuesto> repuesto = new Select<>();
        repuesto.setLabel("Repuesto (opcional)");
        repuesto.setItems(GestorDatos.getRepuestos());
        repuesto.setItemLabelGenerator(r -> r.getNombre() + " (stock: " + r.getStock() + ")");
        repuesto.setEmptySelectionAllowed(true);
        repuesto.setEmptySelectionCaption("Sin repuesto");

        IntegerField cantidad = new IntegerField("Cantidad de repuesto");
        cantidad.setMin(1);
        cantidad.setValue(1);

        NumberField costoManoObra = new NumberField("Costo mano de obra");
        costoManoObra.setMin(0);
        costoManoObra.setValue(0.0);

        if (existente != null) {
            equipo.setValue(GestorDatos.buscarEquipo(existente.getEquipoCodigo()));
            tecnico.setValue(GestorDatos.buscarTecnico(existente.getTecnicoId()));
            tipo.setValue(existente.getTipo());
            falla.setValue(existente.getFalla());
            fecha.setValue(existente.getFecha());
            estado.setValue(existente.getEstado());
            repuesto.setValue(GestorDatos.buscarRepuesto(existente.getRepuestoId()));
            if (existente.getCantRepuesto() > 0) {
                cantidad.setValue(existente.getCantRepuesto());
            }
            costoManoObra.setValue(existente.getCostoManoObra());

            equipo.setEnabled(false);
            repuesto.setEnabled(false);
            cantidad.setEnabled(false);
        }

        FormLayout formulario = new FormLayout(equipo, tecnico, tipo, fecha, falla, estado,
                repuesto, cantidad, costoManoObra);
        formulario.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2));

        Button guardar = new Button("Guardar", e -> {
            String error = validar(equipo.getValue(), tecnico.getValue(), tipo.getValue(),
                    falla.getValue(), fecha.getValue(), estado.getValue(),
                    repuesto.getValue(), cantidad.getValue(), costoManoObra.getValue(),
                    existente == null);
            if (error != null) {
                Notification.show(error);
                return;
            }

            if (existente == null) {

                int idRepuesto = 0;
                int cantidadUsada = 0;
                if (repuesto.getValue() != null) {
                    idRepuesto = repuesto.getValue().getId();
                    cantidadUsada = cantidad.getValue();
                }

                Orden nueva = new Orden(0, equipo.getValue().getCodigo(), tecnico.getValue().getId(),
                        tipo.getValue(), falla.getValue().trim(), fecha.getValue(), estado.getValue(),
                        idRepuesto, cantidadUsada, 0.0, costoManoObra.getValue());
                try {
                    String errorStock = GestorDatos.agregarOrden(nueva);
                    if (errorStock != null) {
                        Notification.show(errorStock);
                        return;
                    }
                } catch (IOException ex) {
                    Notification.show("Error al guardar: " + ex.getMessage());
                    return;
                }
            }
            else {
                existente.setTecnicoId(tecnico.getValue().getId());
                existente.setTipo(tipo.getValue());
                existente.setFalla(falla.getValue().trim());
                existente.setFecha(fecha.getValue());
                existente.setEstado(estado.getValue());
                existente.setCostoManoObra(costoManoObra.getValue());
                try {
                    GestorDatos.guardarOrdenes();
                } catch (IOException ex) {
                    Notification.show("Error al guardar: " + ex.getMessage());
                    return;
                }
            }

            Notification.show("Orden guardada");
            dialog.close();
            aplicarFiltros();
        });
        guardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelar = new Button("Cancelar", e -> dialog.close());

        dialog.add(formulario);
        dialog.getFooter().add(cancelar, guardar);
        dialog.open();
    }

    private String validar(Equipo equipo, Tecnico tecnico, String tipo, String falla,
                        LocalDate fecha, String estado, Repuesto repuesto,
                        Integer cantidad, Double costoManoObra, boolean esNueva) {
        if (equipo == null) return "Elige un equipo";
        if (tecnico == null) return "Elige un técnico";
        if (tipo == null) return "Elige el tipo de mantenimiento";

        if (tipo.equals("Correctivo")) {
            String error = Validador.texto(falla, "La falla");
            if (error != null) return error;
        }
        if (falla.contains("|")) return "La falla no puede contener el símbolo |";

        if (fecha == null) return "La fecha es obligatoria";
        if (esNueva && fecha.isBefore(LocalDate.now())) return "La fecha no puede estar en el pasado";
        if (estado == null) return "Elige un estado";

        if (costoManoObra == null || costoManoObra < 0) {
            return "El costo de mano de obra no puede ser negativo";
        }
        if (esNueva && repuesto != null && (cantidad == null || cantidad < 1)) {
            return "La cantidad de repuesto debe ser al menos 1";
        }
        return null;
    }
}