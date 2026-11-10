package com.proyecto.mantenimiento.vistas;
import com.proyecto.mantenimiento.datos.GestorDatos;
import com.proyecto.mantenimiento.modelo.Orden;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import java.util.List;

// Estructura general: barra superior, menú lateral y aviso de mantenimientos próximos
public class MainLayout extends AppLayout {

    public MainLayout() {
        H1 titulo = new H1("Mantenimiento de equipos");
        titulo.getStyle().set("font-size", "var(--lumo-font-size-l)");
        titulo.getStyle().set("margin", "0");
        addToNavbar(new DrawerToggle(), titulo);

        SideNav menu = new SideNav();
        agregarOpcion(menu, "Equipos", "", VaadinIcon.COG);
        agregarOpcion(menu, "Órdenes de trabajo", "ordenes", VaadinIcon.CLIPBOARD_TEXT);
        agregarOpcion(menu, "Historial por equipo", "historial", VaadinIcon.TIME_BACKWARD);
        agregarOpcion(menu, "Reportes de costos", "reportes", VaadinIcon.CHART);
        agregarOpcion(menu, "Técnicos", "tecnicos", VaadinIcon.USER);
        agregarOpcion(menu, "Repuestos", "repuestos", VaadinIcon.TOOLS);
        addToDrawer(menu);
        avisarMantenimientosProximos();
    }
    private void agregarOpcion(SideNav menu, String texto, String ruta, VaadinIcon icono) {
        SideNavItem opcion = new SideNavItem(texto, ruta);
        opcion.setPrefixComponent(icono.create());
        menu.addItem(opcion);
    }
    private void avisarMantenimientosProximos() {
        List<Orden> proximas = GestorDatos.ordenesProximas(7);
        if (proximas.isEmpty()) {
            return;
        }
        String texto = "Mantenimientos en los próximos 7 días (" + proximas.size() + "):";
        for (Orden orden : proximas) {
            texto = texto + " " + orden.getEquipoCodigo() + " el " + orden.getFecha() + ";";
        }
        Notification.show(texto, 8000, Notification.Position.TOP_CENTER);
    }
}