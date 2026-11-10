package com.proyecto.mantenimiento.vistas;
import com.proyecto.mantenimiento.datos.GestorDatos;
import com.proyecto.mantenimiento.modelo.Equipo;
import com.proyecto.mantenimiento.modelo.FilaCosto;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.ArrayList;
import java.util.List;

@Route(value = "reportes", layout = MainLayout.class)
@PageTitle("Reportes de costos")
public class ReportesView extends VerticalLayout {

    private Grid<FilaCosto> grid = new Grid<>(FilaCosto.class, false);
    private Select<String> filtroEquipo = new Select<>();
    private Span totalGeneral = new Span();

    public ReportesView() {
        setSizeFull();

        H3 titulo = new H3("Costos de mantenimiento por equipo y por mes");

        // Opciones del filtro: "Todos" y el código de cada equipo
        List<String> opciones = new ArrayList<>();
        opciones.add("Todos");
        for (Equipo equipo : GestorDatos.getEquipos()) {
            opciones.add(equipo.getCodigo());
        }
        filtroEquipo.setLabel("Equipo");
        filtroEquipo.setItems(opciones);
        filtroEquipo.setValue("Todos");
        filtroEquipo.addValueChangeListener(e -> actualizar());

        grid.addColumn(FilaCosto::getEquipo).setHeader("Equipo").setSortable(true);
        grid.addColumn(FilaCosto::getMes).setHeader("Mes").setSortable(true);
        grid.addColumn(FilaCosto::getCantidad).setHeader("Órdenes");
        grid.addColumn(f -> "$ " + String.format("%.0f", f.getTotal())).setHeader("Costo total");

        add(titulo, filtroEquipo, grid, totalGeneral);
        actualizar();
    }

    // Recalcula el reporte y deja solo las filas del equipo elegido
    private void actualizar() {
        String elegido = filtroEquipo.getValue();
        List<FilaCosto> filas = new ArrayList<>();
        double suma = 0;

        for (FilaCosto fila : GestorDatos.costosPorEquipoYMes()) {
            if (elegido.equals("Todos") || fila.getEquipo().equals(elegido)) {
                filas.add(fila);
                suma = suma + fila.getTotal();
            }
        }
        grid.setItems(filas);
        totalGeneral.setText("Total: $ " + String.format("%.0f", suma));
    }
}