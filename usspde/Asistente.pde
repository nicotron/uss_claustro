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


  void nombre(float x, float y) {
    textSize(10);
    if (mesa % 2 == 0) {
      fill(51);
    } else {
      fill(0, 0, 255);
    }
    text(this.nombre, x, y);
  }

  void mesa(float x, float y) {
    textSize(12);
    if (mesa % 2 == 0) {
      fill(51);
    } else {
      fill(0, 0, 255);
    }
    text(this.mesa, x, y);
  }

  void textoA(String s, float x, float min, float max) {
    float y = map(min, 0, max, 200, 1800);
    textSize(12);
    fill(0);
    text(s, x, y);
  }

  String text_todas_preguntas (ArrayList <Documento> docus) {
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
