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
    escribirResultado = createWriter("similitud.txt");
    String path = sketchPath("data"); // Variable para acceder a la carpeta
    File[] files = listFiles(path); // Lista de objetos con los archivos


    // Crear el diccionario con todas las palabras únicas de todos los documentos
    diccionario = new IntDict();
    //for (int i = 1; i < files.length; i++) {
    for (int i = 1; i < 10; i++) {
        contador = new IntDict();
        // Convertir el nombre del archivo para ser leído como variable de lectura loadStrings
        String fileTexto = files[i].getName();

        // Tomar cada palabra y descartar signos
        String[] lines = loadStrings(fileTexto);
        String textoSinCorte = join(lines, " ");
        String[] palabras = splitTokens(textoSinCorte, "\", .:¿?!¡()-");

        // Saber qué texto es, e iterar por sus palabras convertidas en minúsculas
        // Iterar por cada palabra y sumar la cantidad que aparece en el documento
        // println("TEXTO : " + fileTexto);

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
        Documento d = new Documento(fileTexto, contador.keyArray(), contador.valueArray(), palabras.length);
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
    exit();
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
class Documento {
    String[] key;
    float tf, idf;
    double tfidf;
    String[] bagOfWords;
    int[] freq;
    String name;
    int value, totalPalabras;

    Documento(String name, String[] key, int[] freq, int totalPalabras) {
        this.name = name;
        this.key = key;
        this.freq = freq;
        this.totalPalabras = totalPalabras;
    }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "usspde" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
