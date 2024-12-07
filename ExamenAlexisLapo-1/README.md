
# Sistema de Gestión de Aviones y Partes

Una aplicación Android completa para la gestión de aviones y su inventario de partes. Esta aplicación CRUD (Crear, Leer, Actualizar, Eliminar) permite a los usuarios mantener una base de datos de aviones y sus componentes asociados.

## Características

### Gestión de Aviones
- Agregar nuevos aviones con detalles:
  - Nombre
  - Fecha de fabricación
  - Estado operativo
  - Capacidad de pasajeros
- Editar información de aviones existentes
- Eliminar registros de aviones
- Ver lista de todos los aviones

### Gestión de Partes
- Agregar partes a aviones específicos con detalles:
  - Nombre de la parte
  - Costo de reemplazo
  - Horas de uso
- Editar información de partes
- Eliminar partes
- Ver partes asociadas a cada avión

## Detalles Técnicos

### Construido Con
- Kotlin
- Android SDK
- Base de datos SQLite
- View Binding
- Componentes de Material Design

### Arquitectura
- Navegación basada en Activities
- Base de datos SQLite con clases helper
- Patrón Adapter para vistas de lista
- Proveedores de contenido para gestión de datos

### Esquema de Base de Datos

#### Tabla de Aviones
```sql
CREATE TABLE avion (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    fecha_fabricacion TEXT NOT NULL,
    estado_operativo INTEGER NOT NULL,
    capacidad_pasajeros INTEGER NOT NULL
)
```

#### Tabla de Partes
```sql
CREATE TABLE parte (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    avion_id INTEGER NOT NULL,
    nombre TEXT NOT NULL,
    precio_repuesto REAL NOT NULL,
    horas_uso INTEGER NOT NULL,
    FOREIGN KEY(avion_id) REFERENCES avion(id)
)
```

## Instalación

1. Clona el repositorio
```bash
https://github.com/MOV-2024-B-SW-GR1/mov-sw-gr1-alexis-jahir-lapo-cabrera/new/main/ExamenAlexisLapo-1
```

2. Abre el proyecto en Android Studio

3. Compila y ejecuta la aplicación en tu dispositivo Android o emulador

## Uso

1. Inicia la aplicación
2. Selecciona "Avión" o "Parte de Avión" del spinner
3. Utiliza los botones para:
   - Insertar nuevos registros
   - Editar registros existentes
   - Eliminar registros

## Licencia

Este proyecto está licenciado bajo la Licencia MIT - ver el archivo [LICENSE.md](LICENSE.md) para más detalles




