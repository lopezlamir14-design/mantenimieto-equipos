package com.proyecto.mantenimiento.vistas;
import com.proyecto.mantenimiento.datos.GestorDatos;
import com.proyecto.mantenimiento.modelo.Equipo;
import com.proyecto.mantenimiento.modelo.Orden;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

@Route(value = "historial", layout = MainLayout.class)
@PageTitle("Historial por equipo")
public class HistorialView extends VerticalLayout {

    private Select<Equipo> selectorEquipo = new Select<>();
    private VerticalLayout contenedor = new VerticalLayout();

    public HistorialView() {
        setSizeFull();

        selectorEquipo.setLabel("Elige un equipo");
        selectorEquipo.setItems(GestorDatos.getEquipos());
        selectorEquipo.setItemLabelGenerator(eq -> eq.getCodigo() + " - " + eq.getNombre());
        selectorEquipo.addValueChangeListener(e -> mostrarHistorial(selectorEquipo.getValue()));

        contenedor.setPadding(false);
        add(selectorEquipo, contenedor);

        // Deja el primer equipo seleccionado para que no se vea vacío
        if (!GestorDatos.getEquipos().isEmpty()) {
            selectorEquipo.setValue(GestorDatos.getEquipos().get(0));
        }
    }

    // Separa las órdenes del equipo en preventivas y correctivas
    private void mostrarHistorial(Equipo equipo) {
        contenedor.removeAll();
        if (equipo == null) {
            return;
        }

        List<Orden> preventivos = new ArrayList<>();
        List<Orden> correctivos = new ArrayList<>();
        double costoTotal = 0;

        for (Orden orden : GestorDatos.ordenesDeEquipo(equipo.getCodigo())) {
            if (orden.getTipo().equals("Preventivo")) {
                preventivos.add(orden);
            }
            else {
                correctivos.add(orden);
            }
            costoTotal = costoTotal + orden.getCostoTotal();
        }

        Span resumen = new Span("Estado actual: " + equipo.getEstado()
                + "  |  Costo total de mantenimiento: $ " + String.format("%.0f", costoTotal));

        Accordion acordeon = new Accordion();
        acordeon.setWidthFull();
        acordeon.add("Preventivos (" + preventivos.size() + ")", crearGrid(preventivos));
        acordeon.add("Correctivos (" + correctivos.size() + ")", crearGrid(correctivos));

        contenedor.add(resumen, acordeon);
    }

    private Grid<Orden> crearGrid(List<Orden> lista) {
        Grid<Orden> grid = new Grid<>(Orden.class, false);
        grid.addColumn(Orden::getId).setHeader("N°");
        grid.addColumn(Orden::getFecha).setHeader("Fecha");
        grid.addColumn(o -> GestorDatos.nombreTecnico(o.getTecnicoId())).setHeader("Técnico");
        grid.addColumn(Orden::getEstado).setHeader("Estado");
        grid.addColumn(Orden::getFalla).setHeader("Falla");
        grid.addColumn(o -> "$ " + String.format("%.0f", o.getCostoTotal())).setHeader("Costo");
        grid.setAllRowsVisible(true);   // el Grid crece según sus filas, sin barra interna
        grid.setItems(lista);
        return grid;
    }
}