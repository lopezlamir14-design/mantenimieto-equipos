package com.proyecto.mantenimiento.vistas;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
public class MainLayout extends AppLayout {

    public MainLayout() {
        H1 titulo = new H1("Mantenimiento de equipos");
        titulo.getStyle().set("font-size", "var(--lumo-font-size-l)");
        titulo.getStyle().set("margin", "0");
        addToNavbar(new DrawerToggle(), titulo);

        SideNav menu = new SideNav();
        agregarOpcion(menu, "Equipos", "", VaadinIcon.COG);
        agregarOpcion(menu, "Órdenes de trabajo", "ordenes", VaadinIcon.CLIPBOARD_TEXT);
        agregarOpcion(menu, "Técnicos", "tecnicos", VaadinIcon.USER);
        agregarOpcion(menu, "Repuestos", "repuestos", VaadinIcon.TOOLS);
        addToDrawer(menu);
    }

    private void agregarOpcion(SideNav menu, String texto, String ruta, VaadinIcon icono) {
        SideNavItem opcion = new SideNavItem(texto, ruta);
        opcion.setPrefixComponent(icono.create());
        menu.addItem(opcion);
    }
}