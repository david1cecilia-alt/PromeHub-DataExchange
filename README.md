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

### Cómo ejecutar

La aplicación debe ejecutarse **desde la carpeta raíz del proyecto**, porque busca los ficheros en `datos/`.

**Desde VS Code o IntelliJ:** abrir la carpeta del proyecto (el IDE detecta el `pom.xml` y descarga las dependencias), abrir `src/main/java/app/App.java` y pulsar **Run**.

**Desde la terminal:**

```bash
mvn compile exec:java
```

### Ficheros que usa la aplicación

| Fichero | Uso |
|---|---|
| `datos/videojuegos.csv` | CSV de entrada de PHManager |
| `datos/catalogo.xml` | XML generado con la opción 3 y leído con la opción 4 |
| `datos/catalogo_exportado.csv` | CSV generado con la opción 5 |

---

## 3. Estructura del proyecto

```
PromeHub-DataExchange/
├── pom.xml                      Configuración de Maven y dependencias de JAXB
├── README.md
├── datos/                       Datos reales de la aplicación
│   └── videojuegos.csv
├── pruebas/                     Ficheros y resultados de las pruebas
│   ├── csv/
│   ├── xml/
│   └── RESULTADOS_PRUEBAS.md
└── src/main/java/
    ├── app/
    │   └── App.java             Menú principal
    ├── modelo/
    │   ├── Videojuego.java      Datos de un videojuego
    │   └── Catalogo.java        Raíz del XML (envuelve la lista)
    └── ficheros/
        ├── GestorCSV.java       Lectura y escritura de CSV
        ├── GestorXML.java       Exportación e importación de XML con JAXB
        └── InfoFicheros.java    Información de los ficheros
```

Los datos reales (`datos/`) están separados de los ficheros de prueba (`pruebas/`), para que las pruebas no mezclen sus ficheros con los de trabajo.

---

## 4. Clases y responsabilidades

| Paquete | Clase | Responsabilidad |
|---|---|---|
| `app` | `App` | Muestra el menú y lee la opción. Guarda el catálogo en memoria (`List<Videojuego> catalogo`) y llama a la clase que corresponde en cada opción. Contiene la búsqueda (RF6) y la comprobación de los datos leídos del XML. |
| `modelo` | `Videojuego` | Los 7 datos de un videojuego, con sus getters y setters y las anotaciones JAXB. |
| `modelo` | `Catalogo` | Elemento raíz `<catalogo>` del XML. Envuelve la lista de videojuegos, porque JAXB necesita un objeto raíz. |
| `ficheros` | `GestorCSV` | `leer()`: CSV → lista de objetos (RF1). `escribir()`: lista → CSV (RF5). |
| `ficheros` | `GestorXML` | `exportarXML()`: lista → XML (RF3). `cargarDesdeXML()`: XML → lista (RF4). |
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
| 4 | RF4 | Lee `catalogo.xml` con JAXB, comprueba los datos y reconstruye la lista. |
| 5 | RF5 | Genera `catalogo_exportado.csv` a partir del catálogo en memoria. |
| 6 | RF6 | Busca por **id** (número exacto) o por **título** (texto contenido, sin distinguir mayúsculas). |
| 7 | RF7 | Muestra la existencia, el tamaño y la ruta de los tres ficheros. |
| 0 | — | Sale de la aplicación. |

---

## 6. Flujo de los datos

```
videojuegos.csv
      │  GestorCSV.leer()               (RF1 · BufferedReader, acceso secuencial)
      ▼
List<Videojuego>  ← catálogo en memoria (App)
      │  GestorXML.exportarXML()        (RF3 · JAXB marshal)
      ▼
catalogo.xml                            (sin codigoProveedor)
      │  GestorXML.cargarDesdeXML()     (RF4 · JAXB unmarshal)
      ▼
List<Videojuego>  ← codigoProveedor = null
      │  GestorCSV.escribir()           (RF5 · BufferedWriter)
      ▼
catalogo_exportado.csv                  (última columna vacía)
```

**El CSV nunca se convierte directamente en XML.** Primero se crean objetos `Videojuego` en memoria, y JAXB trabaja con esos objetos.

### CSV → Java

Cada línea se separa por comas con `split(",", -1)`. El `-1` conserva los campos vacíos del final. Después cada campo se convierte a su tipo (`Integer.parseInt` para `id` y `stock`, `Double.parseDouble` para `precio`) y se crea el objeto con el constructor completo.

```
1,Cyberpunk 2077,PC,RPG,39.99,12,PROV-001
        ↓
new Videojuego(1, "Cyberpunk 2077", "PC", "RPG", 39.99, 12, "PROV-001")
```

### Java → XML (marshal)

`App` pasa el catálogo a `GestorXML` con `setVideojuegos()`. `GestorXML` lo mete en un objeto `Catalogo` y lo escribe con `JAXBContext` y `Marshaller`. JAXB lee las anotaciones de las clases para saber qué es atributo, qué es elemento y qué se excluye.

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

`GestorXML` usa `Unmarshaller` para leer el XML y obtener un `Catalogo` con su lista de `Videojuego`. Para crear cada objeto, JAXB usa el **constructor vacío** de `Videojuego` y después rellena sus atributos; por eso la clase necesita un constructor sin parámetros. Después, `App` comprueba los datos (título no vacío, precio y stock no negativos, ids no repetidos) antes de sustituir el catálogo.

### Pérdida de `codigoProveedor`

`codigoProveedor` es información interna de PHManager y el enunciado exige que no aparezca en el XML. Al volver a CSV ese dato **no se puede recuperar**, porque el XML no lo contiene, y queda vacío en `catalogo_exportado.csv`. No es un error: es la consecuencia del requisito.

---

## 7. Anotaciones JAXB

| Anotación | Dónde | Para qué |
|---|---|---|
| `@XmlRootElement(name = "catalogo")` | `Catalogo` | Indica que esta clase es la raíz del documento XML: `<catalogo>`. |
| `@XmlAccessorType(XmlAccessType.FIELD)` | `Videojuego`, `Catalogo` | JAXB lee y escribe directamente los **atributos** de la clase, no los getters y setters. |
| `@XmlAttribute` | `id` | El id aparece como **atributo** XML: `<videojuego id="1">`. |
| `@XmlElement` | `titulo`, `plataforma`, `genero`, `precio`, `stock`, y la lista en `Catalogo` | Cada dato aparece como **elemento** XML. En `Catalogo`, `name = "videojuego"` da nombre a cada elemento de la lista. |
| `@XmlTransient` | `codigoProveedor` | **Excluye** el atributo del XML. |

---

## 8. Gestión de excepciones

Todos los mensajes están en español, indican qué ha fallado y la aplicación **no se cierra** por un error: vuelve al menú.

| Error | Dónde | Cómo se gestiona | Mensaje |
|---|---|---|---|
| Fichero CSV inexistente | `GestorCSV.leer()` | `Files.exists()` y `FileNotFoundException`, capturada en `App`. | `Error: no se puede cargar el catálogo. El fichero datos/videojuegos.csv no existe.` |
| Error de lectura o escritura de CSV | `GestorCSV` | `IOException`, capturada en `App`. | `Error al leer el fichero CSV: …` |
| Registro CSV incorrecto | `GestorCSV.leer()` | `IllegalArgumentException` en esa línea: se descarta y **se sigue leyendo**. | `Línea 7 descartada: tiene 4 campos y se esperaban 7.` |
| Error de conversión numérica en el CSV | `GestorCSV.leer()` | `NumberFormatException` en `id`, `precio` o `stock`: se descarta la línea y se sigue. | `Línea 4 descartada: el id, el precio o el stock no es un número válido.` |
| Fichero XML inexistente | `App.cargarDesdeXML()` | `Files.exists()` antes de leer. | `Error: no se puede cargar el catálogo. El fichero datos/catalogo.xml no existe.` |
| XML vacío, mal formado, que no es un catálogo o con un número mal escrito | `GestorXML` | `JAXBException`, capturada en `App`. `setEventHandler(evento -> false)` hace que un dato que no se puede convertir (por ejemplo `<precio>gratis</precio>`) también lance la excepción. | `Error al leer el fichero XML: está vacío, mal formado, no es un catálogo o contiene datos con un formato incorrecto…` |
| Datos del XML no válidos | `App.catalogoXMLValido()` | Título vacío, precio o stock negativos, o ids repetidos: no se carga el XML. | `Error: el XML contiene datos de videojuegos no válidos.` |
| Error al escribir el XML | `App.exportarAXML()` | `JAXBException`. | `Error al exportar el catálogo a XML: …` |
| Opción de menú incorrecta | `App` | Texto → `NumberFormatException` en `leerOpcion()`. Número fuera de rango → `default` del `switch`. | `Error: "hola" no es una opción válida…` / `Error: la opción 9 no existe…` |

Todos los ficheros CSV se abren con **try-with-resources**, que los cierra automáticamente aunque se produzca un error.

---

## 9. Decisiones técnicas

| Decisión | Motivo |
|---|---|
| **Maven** con `jakarta.xml.bind-api` y `jaxb-runtime` | JAXB no viene incluido en Java desde la versión 11. Maven descarga la misma versión en todos los equipos. El enunciado no lo exige: es una decisión del grupo. |
| **`BufferedReader` línea a línea** | El enunciado exige **acceso secuencial**. |
| **UTF-8** al leer y escribir | Para que los acentos se lean igual en cualquier sistema. |
| **Descartar la línea incorrecta y seguir** | Una línea mala no debe hacer perder todo el fichero. El usuario ve qué línea falló y por qué. |
| **Ids repetidos detectados con un `HashSet`** | `add()` devuelve `false` si el id ya estaba, sin recorrer toda la lista. |
| **`Catalogo` separado de la lista** | JAXB necesita un objeto raíz que corresponda a `<catalogo>`. |
| **Gestores separados (`GestorCSV`, `GestorXML`)** | Cada formato está en su clase. El menú no necesita saber cómo se lee o escribe cada fichero. |
| **Rutas con `Path.of("datos", …)`** | Funcionan igual en Windows y en Linux. |
| **Si una carga falla, se conserva el catálogo anterior** | Un error al cargar no borra los datos que ya había en memoria. |
| **`OPCION_NO_NUMERICA` en el menú** | `leerOpcion()` necesita devolver algo cuando no se escribe un número. Se usa `Integer.MIN_VALUE` porque nadie lo escribiría como opción (antes se usaba `-1` y escribir -1 no daba error). |

**Limitación conocida:** el CSV usa la coma como separador y no admite comillas, así que un título que contenga comas no se leería correctamente. Los datos de PHManager no tienen ese caso.

---

## 10. Pruebas

- **Diseño de las pruebas:** documento *Pruebas y listado de errores* (38 casos, CP-01 a CP-38).
- **Ficheros de prueba:** `pruebas/csv/` y `pruebas/xml/`. Para usarlos, se copian en `datos/` con el nombre que espera la aplicación (`videojuegos.csv` o `catalogo.xml`).
- **Resultados obtenidos y errores corregidos:** [`pruebas/RESULTADOS_PRUEBAS.md`](pruebas/RESULTADOS_PRUEBAS.md). Resultado: 38 de 38 pruebas superadas.

### Correcciones posteriores a la entrega

Al ejecutar todas las pruebas se encontraron y corrigieron estos fallos (detalle en `RESULTADOS_PRUEBAS.md`):

1. Al escribir `-1` en el menú no salía ningún error.
2. Un XML vacío o mal formado mostraba `Error al leer el fichero XML: null`.
3. Un XML con texto en el precio se cargaba sin avisar, con precio 0.
4. `GestorXML` no permitía recibir el catálogo para exportarlo, por lo que el XML estaba programado dentro de `App`. Ahora `App` usa `GestorXML`.
5. El símbolo `€` salía como `?` en la consola de Windows; se muestra `EUR`.
