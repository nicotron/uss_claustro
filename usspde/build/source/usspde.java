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

// TF-IDF
/*
TF: "Term Frecuency" how frecuent was the term in ONE document

IDF: ""
*/

// Código extraído de una tarea del ramo Recuperación de Información
// del diplomado de cienca de datos de la universidad de chile




IntDict contador, diccionario; // Clase que trabaja con diccionarios y números
ArrayList < Documento > listaDocumentos = new ArrayList < Documento > ();
PrintWriter escribirResultado;

public void setup() {
    escribirResultado = createWriter("similitud_todas_preguntas.txt");
    String path = sketchPath("data"); // Variable para acceder a la carpeta
    File[] files = listFiles(path); // Lista de objetos con los archivos

    // Crear el diccionario con todas las palabras únicas de todos los documentos
    diccionario = new IntDict();
    for (int i = 1; i < files.length; i++) {
        contador = new IntDict();
        // Convertir el nombre del archivo para ser leído como variable de lectura loadStrings
        String fileName = files[i].getName();

        /* Cargar el archivo en un array de líneas de texto, juntarlas en
        una gran línea, y separar por palabra usando REGEX command
        */
        String[] lines = loadStrings(fileName);
        String textoSinCorte = join(lines, "\n");
        String[] palabras = splitTokens(textoSinCorte, "\", .:¿?!¡()-");

        // Saber qué texto es, e iterar por sus palabras convertidas en minúsculas
        // Iterar por cada palabra y sumar la cantidad que aparece en el documento
        // println("TEXTO : " + fileName);
        for (int j = 0; j < palabras.length; j++) {
            String palabra = palabras[j].toLowerCase();

            if (diccionario.hasKey(palabra)) {
                contador.increment(palabra);
                diccionario.increment(palabra);
            } else {
                contador.set(palabra, 1);
                diccionario.set(palabra, 1);
            }
        }
        // con cada documento, crear un objeto con nombre, key, value, totalWords
        // añadir cada objeto Documento a una lista
        Documento d = new Documento(fileName, contador.keyArray(), contador.valueArray(), palabras.length);
        listaDocumentos.add(d);
    }

    // tfidf almacenado en la variable tfidf del objeto doc
    for (Documento doc: listaDocumentos) {
        double tfidf_Resultado = 0;
        for (int i = 0; i < doc.key.length; i++) {
            tfidf_Resultado += tfidf(doc, listaDocumentos, doc.key[i]);
        }
        doc.tfidf = tfidf_Resultado;
    }
    // println("TF IDF listos");

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
                    documento = doc.name;
                    similar = par.name;
                }
            }
        }
        // println("similitud de: " + record + " encontrada entre: " + documento + " y " + similar);
        escribirResultado.println(documento + ' ' + similar);
    }
    escribirResultado.flush();
    escribirResultado.close();
    // exit();

    // GRAPHICS SETTINGS -------------------------------------------------------
    
}



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

public void draw() {
    background(255);
    for (int i = 0; i < listaDocumentos.size(); i++) {
        Documento d = listaDocumentos.get(i);
        String titulo = d.name;
        float y = map(i, 0, listaDocumentos.size(), height * .025f, height * .99f);
        fill(0);
        textSize(15);
        text(d.totalPalabras, 10, y);
        text(titulo, 60, y);
        d.wordCount(200, y);
    }
}
class Documento {
    String[] key;
    float tf, idf;
    double tfidf;
    int[] freq;
    String name;
    int value, totalPalabras;

    // nueva clase
    Palabra palabra;

    Documento(String name, String[] key, int[] freq, int totalPalabras) {
        // arrays
        this.key = key;
        this.freq = freq;

        // primitiva variables
        this.totalPalabras = totalPalabras;
        this.name = name;
    }

    public void wordCount(float x, float y) {
        int largoPalabra = 0;
        String todas = "";

        for (int i = 0; i < key.length; i++) {
            if (freq[i] > 5) {
                todas += key[i] + ": " + freq[i] + " ";
                // largoPalabra += key[i].length();
                // largoPalabra += freq[i];
                fill(0);
                // text(key[i] + ": " + freq[i], x+largoPalabra, y);
                text(todas, x, y);
            }
        }
    }

    // boolean ranking(int limit){
    //     if()
    // }

}
class Palabra {
    String key;
    int value;

    Palabra(String k, int v) {
        this.key = k;
        this.value = v;
    }
}
  public void settings() {  size(3500, 1900); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "usspde" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
