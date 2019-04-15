// test from Atompush
// TF-IDF
/*
TF: "Term Frecuency" how frecuent was the term in ONE document

IDF: ""
*/

// Código extraído de una tarea del ramo Recuperación de Información
// del diplomado de cienca de datos de la universidad de chile

import java.util.Date;
import java.util.List;

IntDict contador, dicc_Crudo, diccionario, stopWords, sDicc, cDicc, vDicc, pmDicc; // Clase que trabaja con diccionarios y números
ArrayList < Documento > listaDocumentos;
int i;
// PrintWriter escribirResultado; // Construir un arcivo de texto.

// Una lista de listas, cada lista separada por sede tiene: mesa > asistente > pregunta
ArrayList < ArrayList > sedes = new ArrayList < ArrayList > ();

// ASISTENTES
ArrayList < Asistente > asistente_list;
ArrayList < Asistente > stgo = new ArrayList < Asistente > ();
ArrayList < Asistente > pm = new ArrayList < Asistente > ();
ArrayList < Asistente > valdivia = new ArrayList < Asistente > ();
ArrayList < Asistente > conce = new ArrayList < Asistente > ();

// VARIABLES DE PROTOTIPADO
String enterely = "";
ArrayList < Subrayo > subraya = new ArrayList < Subrayo > ();

void setup() {
  // crear dicccionario de stopWords
  String[] stops = sWords();
  println("stops.length: " + stops.length);
  stopWords = new IntDict();
  for (int i = 0; i < stops.length; i++) {

    stopWords.set(stops[i], 1);
  }

  // escribirResultado = createWriter("similitud_todas_preguntas.txt");
  listaDocumentos = new ArrayList < Documento > ();

  // Esta explicación está en la libreta.
  String path = sketchPath("data"); // Variable para acceder a la carpeta
  File[] files = listFiles(path); // Lista de objetos con los archivos

  // Crear el diccionario con todas las palabras únicas de todos los documentos
  dicc_Crudo = new IntDict();
  diccionario = new IntDict();
  // Se inicializan todos los diccionarios locales
  sDicc = new IntDict();
  cDicc = new IntDict();
  vDicc = new IntDict();
  pmDicc = new IntDict();
  for (int i = 0; i < files.length; i++) {
    contador = new IntDict();
    // Convertir el nombre del archivo para ser leído como variable de lectura loadStrings
    // Obtener número de pregunta, mesa, sedeInicial
    String fileName = files[i].getName();
    String pregunta = fileName.substring(0, 3);
    // String mesa = fileName.substring(4, 6);
    int mesa = int(fileName.substring(4, 6));
    char sedeInicial = fileName.charAt(fileName.length() - 5);

    /* Cargar el archivo en un array de líneas de texto, juntarlas en
    una gran línea, y separar por palabra usando REGEX command
    */
    String[] lines = loadStrings(fileName);
    String textoSinCorte = join(lines, "\n");
    String[] palabras = splitTokens(textoSinCorte, "\n, •.:¿?!¡()-");

    // Saber qué texto es, e iterar por sus palabras convertidas en minúsculas
    //Iterar por cada palabra y sumar la cantidad que aparece en el documento
    for (int j = 0; j < palabras.length; j++) {
      String palabra = palabras[j].toLowerCase();
      enterely += palabra + " ";

      // Crear un diccionario crudo, con todas las palabras en orden
      dicc_Crudo.set(palabra, 0);

      // Limpieza con stopWords
      if (stopWords.hasKey(palabra) == false) {
        // con cada palabra de toda la colección de archivos,
        // llena dos diccionarios con key y value
        if (diccionario.hasKey(palabra)) {
          contador.increment(palabra);
          diccionario.increment(palabra);
        } else {
          contador.set(palabra, 1);
          diccionario.set(palabra, 1);
        }

        // crear diccinario para cada sede
        switch (sedeInicial) {
          case 's':
            if (sDicc.hasKey(palabra)) {
              sDicc.increment(palabra);
            } else {
              sDicc.set(palabra, 1);
            }
            break;
          case 'v':
            if (vDicc.hasKey(palabra)) {
              vDicc.increment(palabra);
            } else {
              vDicc.set(palabra, 1);
            }
            break;
          case 'c':
            if (cDicc.hasKey(palabra)) {
              cDicc.increment(palabra);
            } else {
              cDicc.set(palabra, 1);
            }
            break;
          case 'p':
            if (pmDicc.hasKey(palabra)) {
              pmDicc.increment(palabra);
            } else {
              pmDicc.set(palabra, 1);
            }
            break;
        }
        if (sedeInicial == 'c') {
          if (sDicc.hasKey(palabra)) {
            sDicc.increment(palabra);
          } else {
            sDicc.set(palabra, 1);
          }
        }
      }
    }
    contador.sortValuesReverse();
    // con cada documento, crear un objeto con nombre, key, value, totalWords
    // añadir cada objeto Documento a una lista
    Documento dd = new Documento(fileName, contador.keyArray(), contador.valueArray(), palabras.length, i, pregunta, mesa, sedeInicial);
    listaDocumentos.add(dd);
  }
  // println("diccinario size " + diccionario.size());
  // println("dicc_Crudo " + dicc_Crudo.size());
  // println(enterely.length());
  // ------------------------------------------------------------------------
  // tfidf almacenado en la variable tfidf del objeto doc
  for (Documento doc: listaDocumentos) {
    double tfidf_Resultado = 0;
    for (int i = 0; i < doc.key.length; i++) {
      tfidf_Resultado += tfidf(doc, listaDocumentos, doc.key[i]);
    }
    doc.tfidf = tfidf_Resultado;
  }
  // encontar el más parecido dentro de todos los documentos
  for (Documento doc: listaDocumentos) {
    double record = 1000;
    double candidato = 1000;
    String documento = "";
    String similar = "";
    for (Documento par: listaDocumentos) {
      if (doc.tfidf != par.tfidf) {
        candidato = Math.abs(doc.tfidf - par.tfidf);

        if (record > candidato) {
          record = candidato;
          documento = doc.fileName;
          similar = par.fileName;
        }
      }
    }
    // escribirResultado.println(documento + ' ' + similar);
  }
  // escribirResultado.flush(); escribirResultado.close();

  // -------------------------------------------------------------------------
  // ASISTENTES CLASS --------------------------------------------------------
  asistente_list = new ArrayList < Asistente > ();
  Table table = loadTable("asistentesData/asistentes.csv", "header");

  for (TableRow row: table.rows()) {
    String sede = row.getString("sede");
    sede = sede.toLowerCase();
    char inicial = sede.charAt(0);
    int mesa = row.getInt("mesa");
    String nombre = row.getString("nombre");
    String rol = row.getString("rol");
    String facilitador = row.getString("facilitador");
    // one asistente object to the list
    asistente_list.add(new Asistente(inicial, sede, mesa, nombre, rol, facilitador));
  }
  // -------------------------------------------------------------------------
  // HACER OBJETOS SEDE: ASISTENTE + DOCUMENTOS ------------------------------
  String[] sedesN = {
    "Santiago",
    "Concepcion",
    "Valdivia",
    "Puerto Montt"
  };

  char[] iniciales = {
    's',
    'c',
    'v',
    'p'
  };

  for (int i = 0; i < listaDocumentos.size(); i++) {
    Documento d = listaDocumentos.get(i);
  }
  // println("listaDocumentos.size(): " + listaDocumentos.size());

  // println("Asistentes en lista Asitentes.cvs " + asistente_list.size());
  for (Documento docu: listaDocumentos) {
    for (Asistente as: asistente_list) {
      if (as.inicial == docu.sedeInicial && as.mesa == docu.mesa) {
        as.d.add(docu);
      }
    }
  }

  for (Asistente a: asistente_list) {
    if (a.d.size() == 0) {
      Documento doc = new Documento();
      a.d.add(doc);
    }
  }

  // println("-----------------------------------------------");
  for (Asistente a: asistente_list) {
    switch (a.inicial) {
      case 's':
        stgo.add(a);
        break;
      case 'p':
        pm.add(a);
        break;
      case 'c':
        conce.add(a);
        break;
      case 'v':
        valdivia.add(a);
        break;
    }
  }
  sedes.add(stgo);
  sedes.add(conce);
  sedes.add(valdivia);
  sedes.add(pm);

  ArrayList < Asistente > santiago = sedes.get(0);
  ArrayList < Asistente > concepcion = sedes.get(1);
  ArrayList < Asistente > valdivia = sedes.get(2);
  ArrayList < Asistente > puertomontt = sedes.get(3);

  // exit();
  size(3500, 1920);
  // size(100, 100);
  background(255);

  float xText = 10;
  float yText = 10;
  float wText = width - 10;
  float hText = height - 10;

  fill(0);
  textSize(9);
  IntDict d = new IntDict();
  d.set("uno", 11); // 0123456789012345
  String todotexto = "dos uno tres uno";
  text(todotexto, xText, yText, wText, hText);

  // String str1 = "como investigación estas investigación";
  String str1 = todotexto;
  String nuevo = "";
  int inicio = 0;
  // int fin = 0;
  String[] list = split(str1, " ");
  for (int i = 0; i < list.length; i++) {
    println("palabra [" + i + "]" + list[i]);
    println("nuevo :" + nuevo);
    println("str1  :" + str1);

    if (d.hasKey(list[i]) && d.get(list[i]) > 10) {
      int inicioPalabra = str1.indexOf(list[i]);
      int largoPalabra = list[i].length();
      println("palabra:" + list[i]);
      inicio += nuevo.length();
      int fin = inicioPalabra + largoPalabra;
      int fs = inicioPalabra;
      int fs2 = largoPalabra;

      println("inicio " + inicio);
      println("fin " + fin + " desde:" + fs + " hasta: " + fs2);

      nuevo += str1.substring(inicio, fin);
      str1 = str1.substring(nuevo.length());

      println("nuevo size:" + nuevo.length());

      float x = textWidth(nuevo);
      float y = 9 * (x / wText);
      float w = textWidth(list[i]);
      float h = 9;
      Subrayo s = new Subrayo(x + xText, y + yText, -w, h);
      subraya.add(s);
    }
    println("nuevo str:" + nuevo);
    println("str1  str:" + str1);
    println("--------------");
  }

  for (Subrayo s: subraya) {
    s.subrayado();
  }
  // String top10Diccionario = "temprana";
  // String miEntero = "temprana como la mañana temprana";
  //
  // int head = miEntero.indexOf(top10Diccionario);
  // int tail = top10Diccionario.length();
  //
  // String delEntero = miEntero.substring(head, tail);
  //
  // if (delEntero.equals(top10Diccionario)) {
  //   println("here");
  //   float x = random(width);
  //   float y = random(height);
  //   float w = 10;
  //   float h = 10;
  //
  //   fill(255, 0, 0);
  //   noStroke();
  //   rect(x, y, w, h);
  // }

  // String t = "temprana";
  //
  // int x = enterely.indexOf(t);
  // String subSAntes = enterely.substring(0,x);
  // // println(textWidth(subSAntes) + " " + subSAntes);
  // String subS = enterely.substring(x, x+t.length());
  // float wordWidth = textWidth(subS);
  // float y = 10;
  // if(textWidth(subSAntes)> width-10) {
  //     y+=9;
  // }
  // fill(255,0,0,10);
  // float x2 = textWidth(subSAntes)-width-10;
  // rect(x2, y, 10,10);
  //
  // println(x + " " + enterely.length() + " " + subS);
  // // text(enterely, 10, 10);

  // ---------------------------------------------------------------------------
  // frecuencia por sede top 10 ------------------------------------------------
  // textSize(14);
  // fill(0);
  // pushMatrix();
  // translate(width * .5, height * .1);
  // text("santiago", 0, 0);
  // frecuenciaSedeTotal(sDicc);
  // popMatrix();
  //
  // pushMatrix();
  // translate(width * .5, height * .25);
  // text("concepción", 0, 0);
  // frecuenciaSedeTotal(cDicc);
  // popMatrix();
  //
  // pushMatrix();
  // translate(width * .5, height * .4);
  // text("valdivia", 0, 0);
  // frecuenciaSedeTotal(vDicc);
  // popMatrix();
  //
  // pushMatrix();
  // translate(width * .5, height * .6);
  // text("puerto montt", 0, 0);
  // frecuenciaSedeTotal(pmDicc);
  // popMatrix();
  // ---------------------------------------------------------------------------
  // ---------------------------------------------------------------------------
  // exit();
}

void draw() {}

void keyPressed() {
  if (key == ' ') {
    makeFile();
  }
  if (key == '+') {
    i++;
  }
  if (key == '-') {
    i--;
  }
}

void makeFile() {
  String d = str(year()) + str(month()) + str(day()) + str(hour()) + str(minute());
  saveFrame("imgs/" + d + "-####.jpg");
}
