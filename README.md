Mantenimiento de equipos

Aplicación web hecha con Java 21 utilizando Vaadin 25 para llevar el mantenimiento
preventivo y correctivo de los equipos de una planta pequeña.

Autor:David Lopez Lamir
Fecha: 9 de noviembre de 2026
Curso: Lógica de Programación

¿Qué hace?
- CRUD de equipos, técnicos, repuestos y órdenes de trabajo.
- Órdenes preventivas y correctivas con técnico, fecha, estado y costos.
- Usar un repuesto en una orden descuenta su stock (si no alcanza, no se guarda).
- Historial de mantenimiento por equipo.
- Alerta de mantenimientos de los próximos 7 días al abrir la aplicación.
- Reporte de costos por equipo y por mes.

¿Cómo ejecutarlo?
1. Instalar Java 21.
2. Clonar el repositorio y abrir la carpeta del proyecto.
3. Ejecutar en Java
4. Abrir http://localhost:8080 en el navegador web

La primera vez se crea la carpeta con datos de ejemplo.

Manual de uso
### Equipos
![Equipos](capturas/equipos.png)
Buscar, filtrar por estado, crear, editar y eliminar (con confirmación).
### Órdenes de trabajo
![Órdenes](capturas/ordenes.png)
### Historial por equipo
![Historial](capturas/historial.png)
### Reportes de costos
![Reportes](capturas/reportes.png)
### Técnicos y repuestos
![Técnicos](capturas/tecnicos.png)

Componentes investigados
- Accordion:historial del equipo con secciones de preventivos y correctivos.
- Select: elegir equipo, técnico, tipo, estado y repuesto.
- Avatar: identifica al técnico en la lista de órdenes.

Decisiones de diseño
- El reporte de costos suma todas las órdenes por el mes de su fecha programada.
- No se puede eliminar un equipo, técnico o repuesto que tenga órdenes.
- Al eliminar una orden aún no cerrada, el repuesto vuelve al stock.
- Al editar una orden no se cambia el equipo ni el repuesto.

Tecnologías
Java 21, Vaadin 25, Spring Boot, archivos de texto, Git y GitHub.
