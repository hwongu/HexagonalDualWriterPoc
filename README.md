# 🏦 HexagonalDualWriterPoc – Validación de Arquitectura (Dual Writer & Composite)

Este repositorio contiene el código fuente de la **Prueba de Concepto (PoC)** avanzada, diseñada para validar la estrategia de **Escritura Dual (Dual Writer)** en procesos de migración bancaria.

El objetivo principal fue demostrar cómo una aplicación puede escribir simultáneamente en el sistema **Legacy (On-Premise)** y en la nueva infraestructura **Cloud**, manteniendo el Core del negocio totalmente desacoplado de esta complejidad mediante el **Patrón Composite**.

### 🔗 Contexto y Demo
* **Caso de Estudio:** Para entender el desafío de negocio (tablas en Inglés vs Español), revisa el post aquí:
    👉 **[LinkedIn Post: El desafío del Cliente - Migración Legacy a Nube](https://www.linkedin.com/posts/hwongu_el-cliente-lleg%C3%B3-con-una-petici%C3%B3n-que-suena-activity-7427374631667568640-BUlm?utm_source=share&utm_medium=member_desktop&rcm=ACoAAAZyivkBpqjS-7ZvQubxyD0MzBUVYtR8T4k)**
* **Demo en Vivo:** Mira la ejecución del código y la sincronización de bases de datos en tiempo real:
    📺 **[YouTube: Demo Técnica Dual Writer en Java](https://youtu.be/1p2_dR4iSvM)**

---

## 📁 Estructura de la Solución

El proyecto sigue estrictamente la **Arquitectura Hexagonal (Ports & Adapters)**. La gran diferencia en esta versión es la implementación del patrón **Composite** en la capa de infraestructura para lograr la escritura doble transparente.

### 1️⃣ 1_Domain (El Negocio)
Ubicación: `net.hwongu.prueba.domain`
Aquí reside la lógica pura de la organización.
* **Agnosticismo Total:** El dominio NO SABE que existe una estrategia de "Dual Write". Para el dominio, solo existe un repositorio donde guardar datos. Esto cumple con el Principio de Responsabilidad Única (SRP).

### 2️⃣ 2_Application (La Orquestación)
Ubicación: `net.hwongu.prueba.application.service`
Contiene los casos de uso:
* **CrearClienteService:** Llama al método `guardar()` del puerto. No contiene lógica de replicación ni if/else para elegir base de datos.

### 3️⃣ 3_Infrastructure (La Magia del Composite)
Ubicación: `net.hwongu.prueba.infrastructure.adapter`
Aquí se encuentran los adaptadores que hacen posible la convivencia:

* 🔄 **Dual Writer (El Orquestador):** `CompositeClienteRepository`
    * Implementa el patrón **Composite**.
    * Actúa como un proxy que recibe el dato y lo delega secuencialmente a todas las implementaciones configuradas (Legacy + Cloud).
* 🏢 **Legacy Adapter:** `ClienteRepositoryDbOnPremise`
    * Conecta con Postgres On-Premise (Tablas en Inglés `business_name`).
* ☁️ **Cloud Adapter:** `ClienteRepositoryDbCloud`
    * Conecta con Postgres Cloud (Tablas en Español `razon_social`).

### 4️⃣ 4_DataBase (Scripts de Validación)
Ubicación: `/scripts`
Incluye los scripts SQL para simular los entornos heterogéneos:
* `Db_OnPrememise.sql`: Crea el entorno Legacy.
* `Db_Cloud.sql`: Crea el entorno Cloud.

---

## 📜 Licencia y Uso

Este código es propiedad intelectual de **Henry Wong** y se entrega como parte de los entregables de la consultoría para validación técnica.
Está permitido su uso para referencia interna del equipo de desarrollo y arquitectura.
Queda prohibido su uso en entornos productivos externos o su redistribución sin autorización.

---

## ⚠️ Nota Técnica (Trade-offs)

Esta implementación utiliza una estrategia de **Dual Write Síncrono**. Aunque es funcional para la PoC y mantiene el código limpio:
1.  **Latencia:** El tiempo de respuesta es la suma de las escrituras en Legacy + Cloud.
2.  **Atomicidad:** En un entorno productivo, si falla la segunda escritura (Cloud) después de la primera, podría haber inconsistencia. Para producción crítica, se recomienda evolucionar hacia el patrón **Transactional Outbox** (Consistencia Eventual).