class Subrayo {
  float x, y, w, h;
  Subrayo(float x, float y, float w, float h) {
    this.x = x;
    this.x = x;
    this.w = w;
    this.h = h;

  }

  //  description
  void subrayado() {
    noStroke();
    fill(255, 0, 0, 200);
    rect(x, y, 2, 9);
  }
}
