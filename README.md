# PromeHub-DataExchange
Tenemos 7 días para entregar una aplicación Java de consola que convierte el catálogo de videojuegos CSV → Java → XML y XML → Java → CSV usando JAXB. La entrega es el jueves 8/10/2026 a las 10:20.

# PromeHub-DataExchange
Tenemos 7 días para entregar una aplicación Java de consola que convierte el catálogo de videojuegos CSV → Java → XML y XML → Java → CSV usando JAXB. La entrega es el jueves 8/10/2026 a las 10:20.

# PromeHub Data Exchange

Aplicación Java de consola que hace de intermediaria entre **PHManager**, que trabaja con CSV, y **PHStore**, que trabaja con XML. Convierte el catálogo de videojuegos en los dos sentidos:

```
CSV → Java → XML
XML → Java → CSV
```

Práctica U1_Actividad5 · Acceso a Datos · Unidad 1: Persistencia en ficheros.

---

## Índice

1. [Equipo y roles](#1-equipo-y-roles)
2. [Requisitos y ejecución](#2-requisitos-y-ejecución)
3. [Estructura del proyecto](#3-estructura-del-proyecto)
4. [Clases y responsabilidades](#4-clases-y-responsabilidades)
5. [Funcionalidades](#5-funcionalidades)
6. [Flujo de los datos](#6-flujo-de-los-datos)
7. [Anotaciones JAXB](#7-anotaciones-jaxb)
8. [Gestión de excepciones](#8-gestión-de-excepciones)
9. [Decisiones técnicas](#9-decisiones-técnicas)
10. [Pruebas](#10-pruebas)

---

## 1. Equipo y roles

| Integrante | Rol |
|---|---|
| David | Team Leader |
| Rubén Cuenca | Programador experto |
| Oskar Bielecki | Responsable de pruebas (QA) |
| Cristian Adasme | Responsable de documentación |

---

## 2. Requisitos y ejecución

### Requisitos

- **Java 17 o superior**.
- **Maven**: descarga automáticamente JAXB, que no viene incluido en Java desde la versión 11.
- Opcional: VS Code con la extensión *Extension Pack for Java*, o IntelliJ IDEA.

### Cómo ejecutar

La aplicación debe ejecutarse **desde la carpeta raíz del proyecto**, porque busca los ficheros en `datos/`.

**Opción A: desde VS Code o IntelliJ**

1. Abrir la carpeta del proyecto. El IDE detecta el `pom.xml` y descarga las dependencias.
2. Abrir `src/main/java/app/App.java` y pulsar **Run**.

**Opción B: desde la terminal con Maven**

```bash
mvn compile exec:java
```

### Ficheros que usa la aplicación

| Fichero | Uso |
|---|---|
| `datos/videojuegos.csv` | CSV de entrada proporcionado por PHManager |
| `datos/catalogo.xml` | XML generado con la opción 3 y leído con la opción 4 |
| `datos/catalogo_exportado.csv` | CSV generado con la opción 5 |

---

## 3. Estructura del proyecto

```
PromeHub-DataExchange/
├── pom.xml                      Configuración de Maven y dependencias JAXB
├── README.md
├── datos/                       Datos reales de la aplicación
│   └── videojuegos.csv
│   └── catalogo_exportado.csv
├── pruebas/                     Ficheros para el catálogo de pruebas
│   ├── csv/
│   └── xml/
└── src/main/java/
    ├── app/
    │   └── App.java             Menú principal
    ├── modelo/
    │   ├── Videojuego.java      Datos de un videojuego
    │   └── Catalogo.java        Raíz del XML (envuelve la lista)
    └── ficheros/
        ├── GestorCSV.java       Lectura y escritura de CSV
        ├── GestorXML.java       Exportación e importación XML con JAXB
        └── InfoFicheros.java    Información de los ficheros
```

Los datos reales (`datos/`) están separados de los ficheros de prueba (`pruebas/`), para que las pruebas no modifiquen los datos de trabajo.

---

## 4. Clases y responsabilidades

Cada clase tiene una sola responsabilidad.

| Paquete | Clase | Responsabilidad |
|---|---|---|
| `app` | `App` | Muestra el menú, lee la opción, guarda el catálogo en memoria (`List<Videojuego>`) y llama al resto de clases. Contiene la búsqueda (RF6). |
| `modelo` | `Videojuego` | Los 7 datos de un videojuego, con sus getters y setters y las anotaciones JAXB. |
| `modelo` | `Catalogo` | Elemento raíz `<catalogo>` del XML. Envuelve la lista de videojuegos, porque JAXB necesita un objeto raíz. |
| `ficheros` | `GestorCSV` | `leer()`: CSV → lista de objetos (RF1). `escribir()`: lista → CSV (RF5). |
| `ficheros` | `GestorXML` | `exportar()`: lista → XML (RF3). `importar()`: XML → lista (RF4). |
| `ficheros` | `InfoFicheros` | Muestra si existe cada fichero, su tamaño y su ruta (RF7). |

---

## 5. Funcionalidades

```
========================================
 PROMEHUB DATA EXCHANGE
========================================
1. Cargar catálogo desde CSV
2. Mostrar catálogo
3. Exportar catálogo a XML
4. Cargar catálogo desde XML
5. Exportar catálogo a CSV
6. Buscar videojuego
7. Información de ficheros
0. Salir
```

| Opción | RF | Qué hace |
|---|---|---|
| 1 | RF1 | Comprueba que el CSV existe, lo lee línea a línea, ignora la cabecera, crea un `Videojuego` por cada registro válido y muestra un resumen: procesados, válidos y descartados. |
| 2 | RF2 | Muestra todos los videojuegos cargados. Si el catálogo está vacío, avisa. |
| 3 | RF3 | Convierte el catálogo en `catalogo.xml` con JAXB. |
| 4 | RF4 | Lee `catalogo.xml` con JAXB y reconstruye la lista de videojuegos. |
| 5 | RF5 | Genera `catalogo_exportado.csv` a partir del catálogo en memoria. |
| 6 | RF6 | Busca por **id** (número exacto) o por **título** (texto contenido, sin distinguir mayúsculas). |
| 7 | RF7 | Muestra la existencia, el tamaño y la ruta de los tres ficheros. |
| 0 | — | Sale de la aplicación. |

---

## 6. Flujo de los datos

```
videojuegos.csv
      │  GestorCSV.leer()          (RF1 · BufferedReader, acceso secuencial)
      ▼
List<Videojuego>  ← catálogo en memoria
      │  GestorXML.exportar()      (RF3 · JAXB marshal)
      ▼
catalogo.xml                       (sin codigoProveedor)
      │  GestorXML.importar()      (RF4 · JAXB unmarshal)
      ▼
List<Videojuego>  ← codigoProveedor = null
      │  GestorCSV.escribir()      (RF5 · BufferedWriter)
      ▼
catalogo_exportado.csv             (última columna vacía)
```

**El CSV nunca se convierte directamente en XML.** Primero se crean objetos `Videojuego`, y JAXB trabaja con esos objetos.

### CSV → Java

Cada línea se separa por comas con `split(",", -1)`. El `-1` conserva los campos vacíos del final. Cada campo se convierte a su tipo: `Integer.parseInt` para `id` y `stock`, y `Double.parseDouble` para `precio`. Con eso se crea el objeto con el constructor completo.

```
1,Cyberpunk 2077,PC,RPG,39.99,12,PROV-001
        ↓
new Videojuego(1, "Cyberpunk 2077", "PC", "RPG", 39.99, 12, "PROV-001")
```

### Java → XML (marshal)

`GestorXML` mete la lista en un objeto `Catalogo` y usa `JAXBContext` y `Marshaller` para escribirlo. JAXB lee las anotaciones de las clases para saber qué es atributo, qué es elemento y qué se excluye.

```xml
<catalogo>
    <videojuego id="1">
        <titulo>Cyberpunk 2077</titulo>
        <plataforma>PC</plataforma>
        <genero>RPG</genero>
        <precio>39.99</precio>
        <stock>12</stock>
    </videojuego>
</catalogo>
```

### XML → Java (unmarshal)

`Unmarshaller` lee el XML y crea un objeto `Catalogo` con su lista de `Videojuego`. Para crear cada objeto, JAXB usa el **constructor vacío** de `Videojuego` y después rellena sus atributos. Por eso esa clase necesita un constructor sin parámetros.

### Pérdida de `codigoProveedor`

`codigoProveedor` es información interna de PHManager y no debe llegar a PHStore, así que no se escribe en el XML. Al volver a CSV, ese dato **no se puede recuperar**, porque el XML no lo contiene. Por eso queda vacío en `catalogo_exportado.csv`. No es un error: es la consecuencia esperada del requisito.

---

## 7. Anotaciones JAXB

| Anotación | Dónde | Para qué |
|---|---|---|
| `@XmlRootElement(name = "catalogo")` | `Catalogo` | Indica que esta clase es la raíz del documento XML: `<catalogo>`. |
| `@XmlAccessorType(XmlAccessType.FIELD)` | `Videojuego`, `Catalogo` | JAXB lee y escribe directamente los **atributos** de la clase, no los getters y setters. |
| `@XmlAttribute` | `id` | El id aparece como **atributo** XML: `<videojuego id="1">`. |
| `@XmlElement` | `titulo`, `plataforma`, `genero`, `precio`, `stock`, y la lista en `Catalogo` | Cada dato aparece como **elemento** XML: `<titulo>…</titulo>`. En `Catalogo`, `name = "videojuego"` da nombre a cada elemento de la lista. |
| `@XmlTransient` | `codigoProveedor` | **Excluye** el atributo del XML. |

---

## 8. Gestión de excepciones

Todos los mensajes están en español, indican qué ha fallado y la aplicación **no se cierra** por un error: vuelve al menú.

| Error | Dónde | Cómo se gestiona | Mensaje de ejemplo |
|---|---|---|---|
| Fichero inexistente | `GestorCSV.leer()` | Se comprueba con `Files.exists()` y se lanza `FileNotFoundException`, que captura `App`. | `Error: no se puede cargar el catálogo. El fichero datos/videojuegos.csv no existe.` |
| Error de lectura o escritura | `GestorCSV` | `IOException`, capturada en `App`. | `Error al leer el fichero CSV: …` |
| Registro CSV incorrecto | `GestorCSV.leer()` | `IllegalArgumentException` en esa línea: se descarta y **se sigue leyendo**. | `Línea 7 descartada: tiene 3 campos y se esperaban 7.` |
| Error de conversión numérica | `GestorCSV.leer()` | `NumberFormatException` en `id`, `precio` o `stock`: se descarta la línea y se sigue. | `Línea 4 descartada: el id, el precio o el stock no es un número válido.` |
| Error en el XML | `GestorXML` | `JAXBException`, capturada con un mensaje propio. | Mensaje de error del XML |
| Opción de menú incorrecta | `App.leerOpcion()` | Texto → `NumberFormatException`. Número fuera de rango → `default` del `switch`. | `Error: "hola" no es una opción válida. Escribe un número del 0 al 7.` |

### Registros CSV que se consideran incorrectos

- Número de campos distinto de 7 (faltan o sobran datos).
- Título vacío.
- `id`, `precio` o `stock` que no son números.
- Precio o stock negativos.
- Id repetido: se detecta con un `HashSet` de los ids ya leídos.

Las líneas vacías se ignoran. Al terminar se muestra un resumen:

```
Registros procesados: 10 | válidos: 2 | descartados: 8
```

Todos los ficheros se abren con **try-with-resources**, que los cierra automáticamente aunque se produzca un error.

---

## 9. Decisiones técnicas

| Decisión | Motivo |
|---|---|
| **Maven** con `jakarta.xml.bind-api` y `jaxb-runtime` | JAXB no viene incluido en Java desde la versión 11. Maven descarga la misma versión en todos los equipos sin copiar ficheros `.jar` a mano. El enunciado no lo exige: es una decisión del grupo. |
| **`BufferedReader` línea a línea** | El enunciado exige **acceso secuencial**. El búfer reduce los accesos a disco. |
| **UTF-8** al leer y escribir | Para que los acentos se lean igual en cualquier sistema. |
| **Descartar la línea incorrecta y seguir** | Una línea mala no debe hacer perder todo el fichero. El usuario ve qué línea falló y por qué. |
| **Separar `Catalogo` de la lista** | JAXB necesita un objeto raíz que corresponda a `<catalogo>`. |
| **Gestores separados (`GestorCSV`, `GestorXML`)** | Cada formato está en su clase. El menú no sabe cómo se lee un fichero, solo pide la lista. |
| **Rutas con `Path.of("datos", …)`** | Funcionan igual en Windows y en Linux. |
| **Si una carga falla, se conserva el catálogo anterior** | Un error al cargar no borra los datos que ya había en memoria. |

**Limitación conocida:** el CSV usa la coma como separador y no admite comillas, así que un título que contenga comas no se leería correctamente. Los datos de PHManager no tienen ese caso.

---

## 10. Pruebas

Los ficheros de prueba están en `pruebas/csv/` y `pruebas/xml/`. Para usar un fichero de prueba, se copia a `datos/` con el nombre que espera la aplicación (`videojuegos.csv` o `catalogo.xml`).

### Catálogo de pruebas

| # | Prueba | Pasos | Resultado esperado | Resultado obtenido | ¿Supera? |
|---|---|---|---|---|---|
| 1 | Cargar correctamente el CSV | Opción 1 con `datos/videojuegos.csv` | 5 válidos, 0 descartados | `Registros procesados: 5 \| válidos: 5 \| descartados: 0` | ✅ |
| 2 | Mostrar el catálogo | Opción 1 y después opción 2 | Lista de los 5 videojuegos | Se muestran los 5 con todos sus datos | ✅ |
| 3 | Mostrar el catálogo vacío | Opción 2 sin cargar nada | Aviso de catálogo vacío | `El catálogo está vacío. Carga primero un CSV…` | ✅ |
| 4 | Generar el XML | Opción 1 y después opción 3 | Se crea `datos/catalogo.xml` | | |
| 5 | `codigoProveedor` no aparece en el XML | Abrir `catalogo.xml` | No hay ninguna etiqueta `codigoProveedor` | | |
| 6 | `id` como atributo | Abrir `catalogo.xml` | `<videojuego id="1">` | | |
| 7 | Cargar nuevamente el XML | Opción 4 y después opción 2 | Los 5 videojuegos, con `codigoProveedor` a `null` | | |
| 8 | Generar un CSV a partir del XML | Opciones 4 y 5 | `catalogo_exportado.csv` con la última columna vacía | | |
| 9 | Exportar CSV sin XML intermedio | Opciones 1 y 5 | CSV idéntico al original | Fichero generado idéntico a `videojuegos.csv` | ✅ |
| 10 | Cargar un fichero que no existe | Opción 1 sin `datos/videojuegos.csv` | Mensaje de fichero inexistente; la aplicación sigue | `Error: no se puede cargar el catálogo. El fichero … no existe.` | ✅ |
| 11 | Registros CSV incorrectos | Opción 1 con `pruebas/csv/02_registros_erroneos.csv` | Se descartan las líneas erróneas indicando el motivo | 2 válidos, 8 descartados, cada uno con su línea y su motivo | ✅ |
| 12 | CSV vacío | Opción 1 con `03_vacio.csv` | Aviso de que no hay videojuegos válidos | `Aviso: el fichero no contiene ningún videojuego válido.` | ✅ |
| 13 | CSV con solo la cabecera | Opción 1 con `04_solo_cabecera.csv` | Aviso de que no hay videojuegos válidos | `Aviso: el fichero no contiene ningún videojuego válido.` | ✅ |
| 14 | XML mal formado | Opción 4 con `05_xml_mal_formado.xml` | Mensaje de error del XML; la aplicación sigue | | |
| 15 | XML vacío | Opción 4 con `06_xml_vacio.xml` | Mensaje de error o catálogo vacío | | |
| 16 | XML con estructura incorrecta | Opción 4 con `07_xml_estructura_incorrecta.xml` | Mensaje de error o catálogo vacío | | |
| 17 | XML válido | Opción 4 con `08_xml_valido.xml` | Se cargan sus videojuegos | | |
| 18 | XML con precio en texto | Opción 4 con `09_xml_precio_texto.xml` | Mensaje de error del XML | | |
| 19 | Buscar por id existente | Opción 6 → 1 → `2` | Se muestra EA Sports FC 26 | Videojuego encontrado | ✅ |
| 20 | Buscar por id no numérico | Opción 6 → 1 → `abc` | Mensaje de id no válido | `Error: "abc" no es un id válido…` | ✅ |
| 21 | Buscar por título parcial | Opción 6 → 2 → `cyber` | Se muestra Cyberpunk 2077 | 1 resultado encontrado | ✅ |
| 22 | Opción de menú con texto | Escribir `hola` | Mensaje de error y vuelve el menú | `Error: "hola" no es una opción válida…` | ✅ |
| 23 | Opción de menú fuera de rango | Escribir `9` | Mensaje de error y vuelve el menú | `Error: la opción 9 no existe…` | ✅ |
| 24 | Información de ficheros | Opción 7 | Existencia, tamaño y ruta de los 3 ficheros | Se muestran los 3 correctamente | ✅ |

### Errores encontrados y corregidos

| Error | Causa | Solución | Comprobación |
|---|---|---|---|
| El fichero de registros erróneos no probaba los errores previstos | Usaba otro formato (`;`, columna año, coma decimal) | Se rehízo con el formato real, manteniendo los mismos casos | Prueba 11: cada línea se descarta por su motivo concreto |
| El símbolo `€` salía como `?` en la consola de Windows | La consola no usa UTF-8 | Se sustituyó por el texto `EUR` en `toString()` | Prueba 2 |
| Las clases no compilaban | Ficheros fuera de la carpeta de su `package` | Cada clase se movió a la carpeta de su paquete | La aplicación compila y se ejecuta |
