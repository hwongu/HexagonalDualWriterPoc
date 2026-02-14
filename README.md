# 🏦 HexagonalDualWriterPoc – Validación de Arquitectura (Dual Writer & Composite)

Este repositorio contiene el código fuente de la **Prueba de Concepto (PoC)** avanzada, diseñada para validar la estrategia de **Escritura Dual (Dual Writer)** en procesos de migración bancaria.

El objetivo principal fue demostrar cómo una aplicación puede escribir simultáneamente en el sistema **Legacy (On-Premise)** y en la nueva infraestructura **Cloud**, manteniendo el Core del negocio totalmente desacoplado de esta complejidad mediante el **Patrón Composite**.

### 🔗 Contexto y Demo
* **Caso de Estudio:** Para entender el desafío de negocio (tablas en Inglés vs Español) y sincronización, revisa el post aquí:
    👉 **[LinkedIn Post: Del "Legacy" a la Nube: Implementando Dual Writer sin ensuciar el Dominio](https://www.linkedin.com/posts/hwongu_softwarearchitecture-designpatterns-compositepattern-activity-7427883033283825664-zbxu?utm_source=share&utm_medium=member_desktop&rcm=ACoAAAZyivkBpqjS-7ZvQubxyD0MzBUVYtR8T4k)**
* **Demo en Vivo:** Mira la ejecución del código y la sincronización de bases de datos en tiempo real:
    📺 **[YouTube: Demo Técnica Dual Writer en Java](https://youtu.be/1p2_dR4iSvM)**

---

## 📁 Estructura del Repositorio

El proyecto se divide en tres componentes físicos principales:

### 1️⃣ 1_BackEnd (El Código Java)
Ubicación: `/1_BackEnd/PoCDualWriter`
Contiene el proyecto Maven con la implementación de la **Arquitectura Hexagonal**. Dentro de sus paquetes (`src/main/java/net/hwongu/poc`) encontrarás:

* **Domain (El Negocio):** Lógica pura. No sabe que existe un "Dual Write".
* **Application (La Orquestación):** Casos de uso (`CrearClienteService`) que llaman a puertos.
* **Infrastructure (La Magia del Composite):** Aquí reside el adaptador `CompositeClienteRepository` que actúa como proxy para delegar la escritura secuencial al adaptador Legacy (`DbOnPremise`) y al adaptador Cloud (`DbCloud`).

### 2️⃣ 2_DataBase (Scripts de Referencia)
Ubicación: `/2_DataBase`
Contiene los scripts SQL crudos (`.sql`) para referencia manual o ejecución individual:
* `Db_OnPremise.sql`: Crea la tabla `customers` (Legacy en Inglés).
* `Db_Cloud.sql`: Crea la tabla `cliente` (Cloud en Español).

### 3️⃣ 3_Infrastructure (Automatización Docker)
Ubicación: `/3_Infrastructure/PocAutomated`
Contiene la **Infraestructura como Código (IaC)** para levantar los entornos de base de datos automáticamente sin instalar PostgreSQL localmente.
* `docker-compose.yml`: Orquesta dos contenedores de base de datos.
* `sql/`: Carpeta con los scripts de inicialización que Docker ejecuta al arrancar.

---

## 🚀 Guía de Ejecución (Entorno Dockerizado)

Para validar esta PoC, la infraestructura está automatizada mediante contenedores.

1.  **Infraestructura:** Navega a la carpeta `3_Infrastructure/PocAutomated` y levanta los servicios utilizando tu orquestador de contenedores favorito.
    * **Cloud DB:** Quedará expuesta en el puerto `5440`.
    * **Legacy DB:** Quedará expuesta en el puerto `5441`.

2.  **Aplicación:** Una vez que las bases de datos estén activas, abre el proyecto ubicado en `1_BackEnd` con tu IDE y ejecuta la clase principal `App.java`.

---

## 🐳 Comandos Docker

Para levantar toda la infraestructura de las bases de datos: 

docker-compose up -d

Para detener los servicios y eliminar los volúmenes de datos (reset completo):

docker-compose down -v

---

## ⚠️ Nota Técnica (Trade-offs)

Esta implementación utiliza una estrategia de **Dual Write Síncrono**. Aunque es funcional para la PoC y mantiene el código limpio:
1.  **Latencia:** El tiempo de respuesta es la suma de las escrituras en Legacy + Cloud.
2.  **Atomicidad:** En un entorno productivo, si falla la segunda escritura (Cloud) después de la primera, podría haber inconsistencia. Para producción crítica, se recomienda evolucionar hacia el patrón **Transactional Outbox** (Consistencia Eventual).

---

## 🛠️ Stack Tecnológico

* **Lenguaje:** Java 21 (Records, Text Blocks, Switch Expressions).
* **Arquitectura:** Hexagonal (Ports & Adapters).
* **Patrones Clave:**
    * **Composite Pattern** (Para la orquestación de repositorios).
    * **Dual Writer Pattern** (Para la estrategia de migración).
* **Base de Datos:** PostgreSQL 15 (Instancias Cloud y Legacy).
* **Infraestructura:** Docker & Docker Compose.

---

**Author:** [Henry Wong](https://github.com/hwongu)

---

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)
![Docker](https://img.shields.io/badge/Docker-Compose-blue?style=for-the-badge&logo=docker)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-336791?style=for-the-badge&logo=postgresql)
