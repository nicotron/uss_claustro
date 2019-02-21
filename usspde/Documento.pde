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
