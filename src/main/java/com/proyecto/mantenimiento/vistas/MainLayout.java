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
        SideNavItem equipos = new SideNavItem("Equipos", "");
        equipos.setPrefixComponent(VaadinIcon.COG.create());
        menu.addItem(equipos);
        addToDrawer(menu);
    }
}