import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.Date; 
import java.util.List; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class usspde extends PApplet {

// test from Atompush
// TF-IDF
/*
TF: "Term Frecuency" how frecuent was the term in ONE document

IDF: ""
*/

// Código extraído de una tarea del ramo Recuperación de Información
// del diplomado de cienca de datos de la universidad de chile




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

public void setup() {
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
    int mesa = PApplet.parseInt(fileName.substring(4, 6));
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
      // inicio += nuevo.length();
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

public void draw() {}

public void keyPressed() {
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

public void makeFile() {
  String d = str(year()) + str(month()) + str(day()) + str(hour()) + str(minute());
  saveFrame("imgs/" + d + "-####.jpg");
}
class Asistente {
  String sede, nombre, rol, facilitador;
  int mesa;
  ArrayList < Documento > d;
  char inicial;

  Asistente(char inicial, String sede, int mesa, String nombre, String rol, String facilitador) {
    this.sede = sede;
    this.mesa = mesa;
    this.nombre = nombre;
    this.rol = rol;
    this.facilitador = facilitador;
    this.d = new ArrayList < Documento > ();
    this.inicial = inicial;
  }


  public void nombre(float x, float y) {
    textSize(10);
    if (mesa % 2 == 0) {
      fill(51);
    } else {
      fill(0, 0, 255);
    }
    text(this.nombre, x, y);
  }

  public void mesa(float x, float y) {
    textSize(12);
    if (mesa % 2 == 0) {
      fill(51);
    } else {
      fill(0, 0, 255);
    }
    text(this.mesa, x, y);
  }

  public void textoA(String s, float x, float min, float max) {
    float y = map(min, 0, max, 200, 1800);
    textSize(12);
    fill(0);
    text(s, x, y);
  }

  public String text_todas_preguntas (ArrayList <Documento> docus) {
      String s = "";
      for (Documento doc : docus){
          // if (doc.)
          for (int i = 0; i < doc.freq.length; i ++) {
              //
              long v = doc.freq[i]/doc.freq.length;
              long v1 = doc.freq.length;
              s += v + ":" + doc.key[i] + " ";
          }
      }
      return s;
  }
}
class Documento {
  String[] key;
  float tf, idf;
  double tfidf;
  int[] freq;
  String fileName, allWords, pregunta;
  int value, totalPalabras, indexFile, mesa;
  char sedeInicial;
  boolean paired;
  // nueva clase
  Palabra[] palabra;

  int bg, verde, rosa;
  PFont font;

  Documento() {
      fileName = "Asistente no tiene preguntas respondidas.";
      freq = new int[1];
      key = new String[1];
  }

  Documento(String fileName, String[] key, int[] freq, int totalPalabras, int indexFile, String pregunta, int mesa, char sedeInicial) {
    // arrays
    this.key = key;
    this.freq = freq;
    this.palabra = new Palabra[totalPalabras];
    this.indexFile = indexFile;

    allWords = "";
    for (int i = 0; i < key.length; i++) {
      palabra[i] = new Palabra(key[i], freq[i]);
      // allWords +=  + ":" + key[i] + " ";
    }

    // primitive variables
    this.totalPalabras = totalPalabras;
    this.fileName = fileName;
    this.pregunta = pregunta;
    this.mesa = mesa;
    this.sedeInicial = sedeInicial;
    this.paired = false;


    // GRAPHICS---------------------------------------------------------
    verde = color(0xffb8df94);
    rosa = color(0xffe7c3c7);
    textSize(10);
    // font = createFont("LetterGothicStd.ttf", 32);
    // textFont(font);
  }

  public Documento deepCopy() {
    Documento foo = new Documento();
    this.key = key;
    this.tf = tf;
    this.idf = idf;
    this.freq = freq;
    this.fileName = fileName;
    this.allWords = allWords;
    this.pregunta = pregunta;
    this.value = value;
    this.totalPalabras = totalPalabras;
    this.indexFile = indexFile;
    this.mesa = mesa;
    this.sedeInicial = sedeInicial;
    this.palabra = palabra;
    return foo;
  }

  public void wordCount(float x, float y, float width) {
    fill(0);
    textSize(8);
    text(allWords, x, y, width, 3500);
  }

  // preguntaTitle
  public void pTitle_WC(float x, float min, float max) {
    float y = map(min, 0, max, 200, 1800);
    textSize(12);
    fill(51);
    text(this.fileName, x, y);
    // text(totalPalabras, x-30, y);

  }
}
class Palabra {
    String key;
    int value;

    Palabra(String k, int v) {
        this.key = k;
        this.value = v;
    }
}
class Sede {
    Asistente a;
    Documento d;
    Sede(Asistente a, Documento d) {
        this.a = a;
        this.d = d;
    }

    public void texto() {
        
    }
}
class Subrayo {
  float x, y, w, h;
  Subrayo(float x, float y, float w, float h) {
    this.x = x;
    this.x = x;
    this.w = w;
    this.h = h;

  }

  //  description
  public void subrayado() {
    noStroke();
    fill(255, 0, 0, 200);
    rect(x, y, 2, 9);
  }
}
// this file is for less cluttered main
// cointain all the global functions

public double tf(Documento doc, String keys) {
  double resultado = 0;
  for (String palabra: doc.key) {
    if (keys.equalsIgnoreCase(palabra)) {
      resultado++;
    }
  }
  return resultado / doc.key.length;
}

// funcion IDF:
// log de el número total de documentos dividido por el número de documentos
// con la palabra
public double idf(ArrayList < Documento > documentos, String palabra) {
  double n = 0;
  for (Documento doc: documentos) {
    for (String word: doc.key) {
      if (palabra.equalsIgnoreCase(word)) {
        n++;
        break;
      }
    }
  }
  return (double) Math.log(documentos.size() / n);
}

// cálculo TF*IDF
public double tfidf(Documento doc, ArrayList < Documento > docs, String palabra) {
  double a = tf(doc, palabra);
  double b = idf(docs, palabra);
  return a * b;
}


// This function returns all the files in a directory as an array of File objects
// This is useful if you want more info about the file
public File[] listFiles(String dir) {
  File file = new File(dir);
  if (file.isDirectory()) {
    File[] files = file.listFiles();
    return files;
  } else {
    // If it's not a directory
    return null;
  }
}

public void frecuenciaSedeTotal(IntDict dicc) {
  dicc.sortValuesReverse();
  String[] s = dicc.keyArray();
  int[] v = dicc.valueArray();
  String st = "";
  int totalPalabras = 0;
  for (int i = 0; i < v.length; i++) {
    totalPalabras += v[i];
  }
  // for (int i = 0; i < s.length; i++) { // todos
  for (int i = 0; i < 10; i++) { // top10
    int d = diccionario.get(s[i]);
    // st += v[i] + " " + d + ": " + s[i] + " ";
    text(s[i], 0, 20*i, 500, 1500);
    for (int j = 0; j < v[i]; j ++) {
      stroke(0);
      line(-j*3-5,i*20,-j*3-5, i*20+15);
    }

    for (int j = 0; j < d; j ++) {
      stroke(0);
      line(100+j*3,i*20,100+j*3, i*20+15);
    }
  }
}
public String[] sWords() {
  String[] s = {
    "0",
    "1",
    "2",
    "3",
    "4",
    "5",
    "6",
    "7",
    "8",
    "9",
    "_",
    "a",
    "actualmente",
    "acuerdo",
    "adelante",
    "ademas",
    "además",
    "adrede",
    "afirmó",
    "agregó",
    "ahi",
    "ahora",
    "ahí",
    "al",
    "algo",
    "alguna",
    "algunas",
    "alguno",
    "algunos",
    "algún",
    "alli",
    "allí",
    "alrededor",
    "ambos",
    "ampleamos",
    "antano",
    "antaño",
    "ante",
    "anterior",
    "antes",
    "apenas",
    "aproximadamente",
    "aquel",
    "aquella",
    "aquellas",
    "aquello",
    "aquellos",
    "aqui",
    "aquél",
    "aquélla",
    "aquéllas",
    "aquéllos",
    "aquí",
    "arriba",
    "arribaabajo",
    "aseguró",
    "asi",
    "así",
    "atras",
    "aun",
    "aunque",
    "ayer",
    "añadió",
    "aún",
    "b",
    "bajo",
    "bastante",
    "bien",
    "breve",
    "buen",
    "buena",
    "buenas",
    "bueno",
    "buenos",
    "c",
    "cada",
    "casi",
    "cerca",
    "cierta",
    "ciertas",
    "cierto",
    "ciertos",
    "cinco",
    "claro",
    "comentó",
    "como",
    "con",
    "conmigo",
    "conocer",
    "conseguimos",
    "conseguir",
    "considera",
    "consideró",
    "consigo",
    "consigue",
    "consiguen",
    "consigues",
    "contigo",
    "contra",
    "cosas",
    "creo",
    "cual",
    "cuales",
    "cualquier",
    "cuando",
    "cuanta",
    "cuantas",
    "cuanto",
    "cuantos",
    "cuatro",
    "cuenta",
    "cuál",
    "cuáles",
    "cuándo",
    "cuánta",
    "cuántas",
    "cuánto",
    "cuántos",
    "cómo",
    "d",
    "da",
    "dado",
    "dan",
    "dar",
    "de",
    "debajo",
    "debe",
    "deben",
    "debido",
    "decir",
    "dejó",
    "del",
    "delante",
    "demasiado",
    "demás",
    "dentro",
    "deprisa",
    "desde",
    "despacio",
    "despues",
    "después",
    "detras",
    "detrás",
    "dia",
    "dias",
    "dice",
    "dicen",
    "dicho",
    "dieron",
    "diferente",
    "diferentes",
    "dijeron",
    "dijo",
    "dio",
    "donde",
    "dos",
    "durante",
    "día",
    "días",
    "dónde",
    "e",
    "ejemplo",
    "el",
    "ella",
    "ellas",
    "ello",
    "ellos",
    "embargo",
    "empleais",
    "emplean",
    "emplear",
    "empleas",
    "empleo",
    "en",
    "encima",
    "encuentra",
    "enfrente",
    "enseguida",
    "entonces",
    "entre",
    "era",
    "erais",
    "eramos",
    "eran",
    "eras",
    "eres",
    "es",
    "esa",
    "esas",
    "ese",
    "eso",
    "esos",
    "esta",
    "estaba",
    "estabais",
    "estaban",
    "estabas",
    "estad",
    "estada",
    "estadas",
    "estado",
    "estados",
    "estais",
    "estamos",
    "estan",
    "estando",
    "estar",
    "estaremos",
    "estará",
    "estarán",
    "estarás",
    "estaré",
    "estaréis",
    "estaría",
    "estaríais",
    "estaríamos",
    "estarían",
    "estarías",
    "estas",
    "este",
    "estemos",
    "esto",
    "estos",
    "estoy",
    "estuve",
    "estuviera",
    "estuvierais",
    "estuvieran",
    "estuvieras",
    "estuvieron",
    "estuviese",
    "estuvieseis",
    "estuviesen",
    "estuvieses",
    "estuvimos",
    "estuviste",
    "estuvisteis",
    "estuviéramos",
    "estuviésemos",
    "estuvo",
    "está",
    "estábamos",
    "estáis",
    "están",
    "estás",
    "esté",
    "estéis",
    "estén",
    "estés",
    "ex",
    "excepto",
    "existe",
    "existen",
    "explicó",
    "expresó",
    "f",
    "fin",
    "final",
    "fue",
    "fuera",
    "fuerais",
    "fueran",
    "fueras",
    "fueron",
    "fuese",
    "fueseis",
    "fuesen",
    "fueses",
    "fui",
    "fuimos",
    "fuiste",
    "fuisteis",
    "fuéramos",
    "fuésemos",
    "g",
    "general",
    "gran",
    "grandes",
    "gueno",
    "h",
    "ha",
    "haber",
    "habia",
    "habida",
    "habidas",
    "habido",
    "habidos",
    "habiendo",
    "habla",
    "hablan",
    "habremos",
    "habrá",
    "habrán",
    "habrás",
    "habré",
    "habréis",
    "habría",
    "habríais",
    "habríamos",
    "habrían",
    "habrías",
    "habéis",
    "había",
    "habíais",
    "habíamos",
    "habían",
    "habías",
    "hace",
    "haceis",
    "hacemos",
    "hacen",
    "hacer",
    "hacerlo",
    "haces",
    "hacia",
    "haciendo",
    "hago",
    "han",
    "has",
    "hasta",
    "hay",
    "haya",
    "hayamos",
    "hayan",
    "hayas",
    "hayáis",
    "he",
    "hecho",
    "hemos",
    "hicieron",
    "hizo",
    "horas",
    "hoy",
    "hube",
    "hubiera",
    "hubierais",
    "hubieran",
    "hubieras",
    "hubieron",
    "hubiese",
    "hubieseis",
    "hubiesen",
    "hubieses",
    "hubimos",
    "hubiste",
    "hubisteis",
    "hubiéramos",
    "hubiésemos",
    "hubo",
    "i",
    "igual",
    "incluso",
    "indicó",
    "informo",
    "informó",
    "intenta",
    "intentais",
    "intentamos",
    "intentan",
    "intentar",
    "intentas",
    "intento",
    "ir",
    "j",
    "junto",
    "k",
    "l",
    "la",
    "lado",
    "largo",
    "las",
    "le",
    "lejos",
    "les",
    "llegó",
    "lleva",
    "llevar",
    "lo",
    "los",
    "luego",
    "lugar",
    "m",
    "mal",
    "manera",
    "manifestó",
    "mas",
    "mayor",
    "me",
    "mediante",
    "medio",
    "mejor",
    "mencionó",
    "menos",
    "menudo",
    "mi",
    "mia",
    "mias",
    "mientras",
    "mio",
    "mios",
    "mis",
    "misma",
    "mismas",
    "mismo",
    "mismos",
    "modo",
    "momento",
    "mucha",
    "muchas",
    "mucho",
    "muchos",
    "muy",
    "más",
    "mí",
    "mía",
    "mías",
    "mío",
    "míos",
    "n",
    "nada",
    "nadie",
    "ni",
    "ninguna",
    "ningunas",
    "ninguno",
    "ningunos",
    "ningún",
    "no",
    "nos",
    "nosotras",
    "nosotros",
    "nuestra",
    "nuestras",
    "nuestro",
    "nuestros",
    "nueva",
    "nuevas",
    "nuevo",
    "nuevos",
    "nunca",
    "o",
    "ocho",
    "os",
    "otra",
    "otras",
    "otro",
    "otros",
    "p",
    "pais",
    "para",
    "parece",
    "parte",
    "partir",
    "pasada",
    "pasado",
    "paìs",
    "peor",
    "pero",
    "pesar",
    "poca",
    "pocas",
    "poco",
    "pocos",
    "podeis",
    "podemos",
    "poder",
    "podria",
    "podriais",
    "podriamos",
    "podrian",
    "podrias",
    "podrá",
    "podrán",
    "podría",
    "podrían",
    "poner",
    "por",
    "por qué",
    "porque",
    "posible",
    "primer",
    "primera",
    "primero",
    "primeros",
    "principalmente",
    "pronto",
    "propia",
    "propias",
    "propio",
    "propios",
    "proximo",
    "próximo",
    "próximos",
    "pudo",
    "pueda",
    "puede",
    "pueden",
    "puedo",
    "pues",
    "q",
    "qeu",
    "que",
    "quedó",
    "queremos",
    "quien",
    "quienes",
    "quiere",
    "quiza",
    "quizas",
    "quizá",
    "quizás",
    "quién",
    "quiénes",
    "qué",
    "r",
    "raras",
    "realizado",
    "realizar",
    "realizó",
    "repente",
    "respecto",
    "s",
    "sabe",
    "sabeis",
    "sabemos",
    "saben",
    "saber",
    "sabes",
    "sal",
    "salvo",
    "se",
    "sea",
    "seamos",
    "sean",
    "seas",
    "segun",
    "segunda",
    "segundo",
    "según",
    "seis",
    "ser",
    "sera",
    "seremos",
    "será",
    "serán",
    "serás",
    "seré",
    "seréis",
    "sería",
    "seríais",
    "seríamos",
    "serían",
    "serías",
    "seáis",
    "señaló",
    "si",
    "sido",
    "siempre",
    "siendo",
    "siete",
    "sigue",
    "siguiente",
    "sin",
    "sino",
    "sobre",
    "sois",
    "sola",
    "solamente",
    "solas",
    "solo",
    "solos",
    "somos",
    "son",
    "soy",
    "soyos",
    "su",
    "supuesto",
    "sus",
    "suya",
    "suyas",
    "suyo",
    "suyos",
    "sé",
    "sí",
    "sólo",
    "t",
    "tal",
    "tambien",
    "también",
    "tampoco",
    "tan",
    "tanto",
    "tarde",
    "te",
    "temprano",
    "tendremos",
    "tendrá",
    "tendrán",
    "tendrás",
    "tendré",
    "tendréis",
    "tendría",
    "tendríais",
    "tendríamos",
    "tendrían",
    "tendrías",
    "tened",
    "teneis",
    "tenemos",
    "tener",
    "tenga",
    "tengamos",
    "tengan",
    "tengas",
    "tengo",
    "tengáis",
    "tenida",
    "tenidas",
    "tenido",
    "tenidos",
    "teniendo",
    "tenéis",
    "tenía",
    "teníais",
    "teníamos",
    "tenían",
    "tenías",
    "tercera",
    "ti",
    "tiempo",
    "tiene",
    "tienen",
    "tienes",
    "toda",
    "todas",
    "todavia",
    "todavía",
    "todo",
    "todos",
    "total",
    "trabaja",
    "trabajais",
    "trabajamos",
    "trabajan",
    "trabajar",
    "trabajas",
    "trabajo",
    "tras",
    "trata",
    "través",
    "tres",
    "tu",
    "tus",
    "tuve",
    "tuviera",
    "tuvierais",
    "tuvieran",
    "tuvieras",
    "tuvieron",
    "tuviese",
    "tuvieseis",
    "tuviesen",
    "tuvieses",
    "tuvimos",
    "tuviste",
    "tuvisteis",
    "tuviéramos",
    "tuviésemos",
    "tuvo",
    "tuya",
    "tuyas",
    "tuyo",
    "tuyos",
    "tú",
    "u",
    "ultimo",
    "un",
    "una",
    "unas",
    "uno",
    "unos",
    "usa",
    "usais",
    "usamos",
    "usan",
    "usar",
    "usas",
    "uso",
    "usted",
    "ustedes",
    "v",
    "va",
    "vais",
    "valor",
    "vamos",
    "van",
    "varias",
    "varios",
    "vaya",
    "veces",
    "ver",
    "verdad",
    "verdadera",
    "verdadero",
    "vez",
    "vosotras",
    "vosotros",
    "voy",
    "vuestra",
    "vuestras",
    "vuestro",
    "vuestros",
    "w",
    "x",
    "y",
    "ya",
    "yo",
    "z",
    "él",
    "éramos",
    "ésa",
    "ésas",
    "ése",
    "ésos",
    "ésta",
    "éstas",
    "éste",
    "éstos",
    "última",
    "últimas",
    "último",
    "últimos"
};
  return s;
}
  public void settings() {  size(3500, 1920); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "usspde" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
