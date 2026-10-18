# Resultados de las pruebas

Resultados de ejecutar las 38 pruebas del documento *Pruebas y listado de errores* (CP-01 a CP-38) sobre la versión corregida de la aplicación.

- **Fecha de ejecución:** 18/10/2026
- **Ficheros usados:** los de `pruebas/csv/` y `pruebas/xml/`, copiados en `datos/` con el nombre que espera la aplicación.
- **Resultado global:** 38 de 38 pruebas superadas.

## 1. Menú principal

| ID | Prueba | Resultado obtenido | Estado |
|---|---|---|---|
| CP-01 | El menú se ve al arrancar | Aparecen las opciones 1 a 7 y 0 | OK |
| CP-02 | Opción escrita con letras (`abc`) | `Error: "abc" no es una opción válida. Escribe un número del 0 al 7.` | OK |
| CP-03 | Opción que no existe (`99`) | `Error: la opción 99 no existe. Elige un número del 0 al 7.` | OK |
| CP-04 | Números negativos (`-1`, `-5`) | `Error: la opción -1 no existe…` y `Error: la opción -5 no existe…` | OK (ver error 02) |
| CP-05 | Pulsar Enter sin escribir | `Error: "" no es una opción válida…` | OK |
| CP-06 | Salir (`0`) | `Saliendo de PromeHub Data Exchange. ¡Hasta pronto!` | OK |

## 2. Cargar CSV (RF1)

| ID | Prueba | Resultado obtenido | Estado |
|---|---|---|---|
| CP-07 | CSV correcto | `Registros procesados: 5 \| válidos: 5 \| descartados: 0` | OK |
| CP-08 | CSV con registros erróneos | Descarta 10 líneas indicando la línea y el motivo; `procesados: 13 \| válidos: 3 \| descartados: 10`; el programa sigue | OK |
| CP-09 | CSV vacío | `Aviso: el fichero no contiene ningún videojuego válido.` | OK |
| CP-10 | CSV con solo la cabecera | `Aviso: el fichero no contiene ningún videojuego válido.` | OK |
| CP-11 | CSV que no existe | `Error: no se puede cargar el catálogo. El fichero datos/videojuegos.csv no existe.` | OK |

Detalle de CP-08: se descartan el título vacío, el id `abc`, el precio negativo, el precio `gratis`, la línea con 4 campos, la de 9 campos, el id repetido, el stock `muchos`, el stock negativo y la línea `12,50` (se parte en 8 campos). Se aceptan Cyberpunk, "Sin proveedor" (el código de proveedor vacío se permite) y "Valido final".

## 3. Mostrar catálogo (RF2)

| ID | Prueba | Resultado obtenido | Estado |
|---|---|---|---|
| CP-12 | Mostrar tras cargar | Salen los 5 videojuegos con todos sus datos, incluido el código de proveedor | OK |
| CP-13 | Mostrar sin cargar nada | `El catálogo está vacío. Carga primero un CSV (opción 1) o un XML (opción 4).` | OK |
| CP-14 | Mostrar tras cargar un CSV vacío | Aviso de fichero sin videojuegos y después el mensaje de catálogo vacío | OK |

## 4. Exportar a XML (RF3)

| ID | Prueba | Resultado obtenido | Estado |
|---|---|---|---|
| CP-15 | Exportar un catálogo cargado | `Catálogo exportado a datos/catalogo.xml (5 videojuegos).` El XML tiene 5 `<videojuego id="…">` y ninguna etiqueta `codigoProveedor` | OK |
| CP-16 | Exportar sin cargar nada | Mensaje de catálogo vacío; no se crea el fichero | OK |

## 5. Cargar XML (RF4)

| ID | Prueba | Resultado obtenido | Estado |
|---|---|---|---|
| CP-17 | XML correcto escrito a mano | `Videojuegos cargados: 2` | OK |
| CP-18 | XML generado por la aplicación | `Videojuegos cargados: 5`, mismos datos; código de proveedor `null` | OK |
| CP-19 | XML mal formado | `Error al leer el fichero XML: está vacío, mal formado, no es un catálogo o contiene datos con un formato incorrecto…` | OK (ver error 03) |
| CP-20 | XML vacío | Mismo mensaje de error; el programa sigue | OK (ver error 03) |
| CP-21 | XML que no es un catálogo | Mismo mensaje de error; el programa sigue | OK |
| CP-22 | XML que no existe | `Error: no se puede cargar el catálogo. El fichero datos/catalogo.xml no existe.` | OK |
| CP-23 | XML con el precio en letras | Mensaje de error; no se carga nada y el catálogo queda vacío | OK (ver error 04) |

## 6. Exportar a CSV (RF5)

| ID | Prueba | Resultado obtenido | Estado |
|---|---|---|---|
| CP-24 | Exportar tras cargar el CSV | El fichero generado es idéntico a `videojuegos.csv` | OK |
| CP-25 | Exportar sin cargar nada | Mensaje de catálogo vacío; no se crea el fichero | OK |
| CP-26 | Exportar un catálogo que viene de un XML | CSV con cabecera y 2 videojuegos, 7 columnas en cada línea, la última vacía | OK |

## 7. Buscar (RF6)

| ID | Prueba | Resultado obtenido | Estado |
|---|---|---|---|
| CP-27 | Id que existe (`2`) | `Encontrado: ID: 2 \| Título: EA Sports FC 26…` | OK |
| CP-28 | Id que no existe (`999`) | `No hay ningún videojuego con el id 999.` | OK |
| CP-29 | Id con letras (`abc`) | `Error: "abc" no es un id válido. El id debe ser un número.` | OK |
| CP-30 | Parte del título (`mine`) | Sale Minecraft · `Resultados encontrados: 1` | OK |
| CP-31 | Título en mayúsculas (`GTA`) | Sale GTA V · `Resultados encontrados: 1` | OK |
| CP-32 | Título que no existe (`zelda`) | `No hay ningún videojuego cuyo título contenga "zelda".` | OK |
| CP-33 | Búsqueda vacía | `Error: el texto de búsqueda no puede estar vacío.` | OK |
| CP-34 | Tipo de búsqueda `3` | `Error: escribe 1 para buscar por id o 2 para buscar por título.` | OK |
| CP-35 | Buscar sin cargar nada | Mensaje de catálogo vacío | OK |

## 8. Información de ficheros (RF7)

| ID | Prueba | Resultado obtenido | Estado |
|---|---|---|---|
| CP-36 | Con los tres ficheros creados | Para cada uno: ruta completa, `Existe: sí` y tamaño en bytes | OK |
| CP-37 | Sin `catalogo.xml` | `catalogo.xml` aparece con `Existe: no`; el programa sigue | OK |

## 9. Flujo completo

| ID | Prueba | Resultado obtenido | Estado |
|---|---|---|---|
| CP-38 | CSV → XML → reiniciar → XML → CSV | El CSV final tiene los mismos 5 videojuegos y los mismos datos, **excepto el código de proveedor, que queda vacío** | OK (ver nota) |

**Nota sobre CP-38:** el resultado esperado del documento decía "los mismos datos que el original". No es posible, ni debe serlo: el enunciado exige que `codigoProveedor` quede fuera del XML (`@XmlTransient`), así que al reconstruir el CSV desde el XML ese dato ya no existe. La aplicación se comporta como pide el enunciado; el error estaba en el resultado esperado.

## 10. Errores encontrados y corregidos

| Nº | Prueba | Qué pasaba | Corrección | Comprobación |
|---|---|---|---|---|
| 01 | CP-12 | Ver el documento de pruebas | Corregido por Rubén | CP-12 |
| 02 | CP-04 | Al escribir `-1` no salía ningún error: `-1` se usaba como marca interna de "no es un número" | `App` usa la constante `OPCION_NO_NUMERICA` (`Integer.MIN_VALUE`) en lugar de `-1` | CP-04 |
| 03 | CP-19, CP-20 | El mensaje era `Error al leer el fichero XML: null`, porque en esos casos JAXB no da mensaje | Mensaje propio que explica las posibles causas | CP-19, CP-20, CP-21 |
| 04 | CP-23 | El XML con `<precio>gratis</precio>` se cargaba sin avisar, con precio 0 | `GestorXML` activa `setEventHandler(evento -> false)`: cualquier error de conversión lanza una excepción | CP-23 |
| 05 | — | `GestorXML.exportarXML()` siempre exportaba una lista vacía porque no había forma de pasarle el catálogo; por eso el XML se había programado dentro de `App` | Se añade `setVideojuegos()` a `GestorXML` y `App` usa `GestorXML` en las opciones 3 y 4 | CP-15 a CP-23, CP-38 |
| 06 | CP-12 | El símbolo `€` salía como `?` en la consola de Windows | `toString()` de `Videojuego` usa `EUR` | CP-12 |
