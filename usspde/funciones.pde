// this file is for less cluttered main
// cointain all the global functions

double tf(Documento doc, String keys) {
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
double idf(ArrayList < Documento > documentos, String palabra) {
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
double tfidf(Documento doc, ArrayList < Documento > docs, String palabra) {
  double a = tf(doc, palabra);
  double b = idf(docs, palabra);
  return a * b;
}


// This function returns all the files in a directory as an array of File objects
// This is useful if you want more info about the file
File[] listFiles(String dir) {
  File file = new File(dir);
  if (file.isDirectory()) {
    File[] files = file.listFiles();
    return files;
  } else {
    // If it's not a directory
    return null;
  }
}

void frecuenciaSedeTotal(IntDict dicc) {
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
