BillManager – Sistema Web de Gestión Financiera

BillManager es una aplicación web desarrollada en Java con OpenXava para administrar ingresos, egresos, metas financieras y generar reportes en PDF y Excel. Está orientado a usuarios individuales y pequeños negocios que necesitan controlar sus finanzas de manera sencilla, segura y centralizada.

Funciones Principales

Registro de ingresos y egresos con categoría, fecha y descripción.

Gestión de metas financieras y seguimiento de su avance.

KPIs y dashboard con balance, totales y evolución financiera.

Reportes exportables en PDF y XLSX.

CRUD automático gracias a OpenXava.

Inicio de sesión individual.

Tecnologías

Java, OpenXava, Naviox

JPA/Hibernate

PostgreSQL (H2 en desarrollo)

JasperReports

Maven, arquitectura MVC

Objetivo del Proyecto

Crear un sistema web que facilite el registro y análisis financiero mediante indicadores claros, reportes automáticos y una interfaz ágil construida con OpenXava.

Requerimientos (Resumen)
Funcionales

Registrar ingresos, egresos y metas.

Mostrar indicadores financieros.

Exportar reportes PDF/XLSX.

CRUD completo con OpenXava.

Inicio de sesión individual.

No funcionales

Uso de OpenXava y PostgreSQL.

Interfaz responsiva.

Seguridad e integridad de datos.

Arquitectura MVC.

Código bajo control en GitHub.

🚀 Instalación
git clone https://github.com/efrinn/BillManager
cd BillManager
mvn clean install
mvn xava:run


Acceso:
http://localhost:8080/BillManager

🧬 Alcance (MVP)

Modulos: Transacciones, Metas, Mis Finanzas, Reportes.

Dashboard con KPIs.

Exportación de reportes.

CRUD completo.

📚 Repositorio

🔗 https://github.com/efrinn/BillManager

Proyecto realizado por Cristofer Cuarezma, Geordany Valdez, Jorge Cubillo, Kenneth Acuña, Stalin Cordoba
