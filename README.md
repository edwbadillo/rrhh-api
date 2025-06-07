# RH API

Sistema backend para la gestión de Recursos Humanos, desarrollado con Spring Boot y PostgreSQL. Este proyecto forma parte de un sistema más amplio que incluye módulos de empleados, contratos, pagos mensuales, notificaciones por correo y más. Este proyecto sirve como portafolio y practica de desarrollo de software y no representa un producto funcional completo.

## 🚀 Características principales

- Gestión de usuarios (roles: ADMIN, RH, EMPLOYEE)
- Integración con Firebase Authentication (login externo)
- Gestión de empresas y sucursales
- Registro de empleados
- Contratos laborales
- Registro de pagos mensuales por contrato
- Arquitectura en capas limpia (controller, service, repository, dto)
- Pruebas unitarias para capas de repositorio, servicio y controlador

## 🛠️ Tecnologías utilizadas

- **Java 21**
- **Spring Boot 3.5**
- **PostgreSQL 17.5**
- **Lombok**
- **JUnit + Mockito**
- **Firebase Admin SDK**

## 📝 Variables de entorno
| Variable    | Descripción                                                     | Valor por defecto |
| ----------- | --------------------------------------------------------------- |-------------------|
| `DB_HOST`   | Dirección del host de la base de datos PostgreSQL.              | `localhost`       |
| `DB_PORT`   | Puerto en el que PostgreSQL está escuchando.                    | `5432`            |
| `DB_NAME`   | Nombre de la base de datos a la que se conectará la aplicación. | *(requerido)*     |
| `DB_USER`   | Usuario con permisos para acceder a la base de datos.           | *(requerido)*     |
| `DB_PASSWD` | Contraseña correspondiente al usuario de la base de datos.      | *(requerido)*     |


## 🔗 Integración con Firebase
Crea un archivo `serviceAccountKey.json` con las credenciales del proyecto de Firebase [más información](https://firebase.google.com/docs/admin/setup?hl=es-419#initialize-sdk). Este archivo debe estar ubicado en la carpeta `src/main/resources`.

Debes tener un proyecto de Firebase configurado y configurar las variables de entorno correspondientes. El proyecto utiliza el SDK de Firebase Admin para interactuar con Firebase Authentication.
Debes asociar el ID del usuario Firebase con el ID del usuario registrado en la base de datos.

## 📦 Base de datos
El proyecto no crea el esquema automáticamente (spring.jpa.hibernate.ddl-auto=validate). Asegúrate de tener las tablas creadas con el script SQL incluido antes de ejecutar la aplicación, recuerda asociar el ID del usuario Firebase con el ID del usuario registrado en la base de datos.