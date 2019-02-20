import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class asistentes extends PApplet {

Table table, santiago, valdivia, concepcion, patagonia;
ArrayList<Table> tables = new ArrayList<Table>();

public void setup() {
  
  table = loadTable("asistentes.csv", "header");

  tables.add(table);
  tables.add(santiago);
  tables.add(valdivia);
  tables.add(concepcion);
  tables.add(patagonia);
  
  println(tables.get(0));
  
  for(int i=0; i < tables.size(); i++){
    //tables[i].addColumn("mesa");
    //table[i].addColumn("nombre");
    //table[i].addColumn("rol");
    //table[i].addColumn("facilitador");
  
  }
  
  println(table.getRowCount() + " filas total"); 
  
  for (TableRow row : table.rows()) {
    
    String sede = row.getString("sede");
    String mesa = row.getString(1);
    String nombre = row.getString("nombre");
    String rol = row.getString("rol");
    String facilitador = row.getString("facilitador");
    
    if (sede.equals("Puerto Montt")){
      println(sede + "\t" + mesa + "\t" + nombre);
    } else {
    println(sede + "\t\t" + mesa + "\t" + nombre);
    }
  }
  
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "asistentes" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
