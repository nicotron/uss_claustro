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

  color bg, verde, rosa;
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
    verde = color(#b8df94);
    rosa = color(#e7c3c7);
    textSize(10);
    // font = createFont("LetterGothicStd.ttf", 32);
    // textFont(font);
  }

  Documento deepCopy() {
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

  void wordCount(float x, float y, float width) {
    fill(0);
    textSize(8);
    text(allWords, x, y, width, 3500);
  }

  // preguntaTitle
  void pTitle_WC(float x, float min, float max) {
    float y = map(min, 0, max, 200, 1800);
    textSize(12);
    fill(51);
    text(this.fileName, x, y);
    // text(totalPalabras, x-30, y);

  }
}
