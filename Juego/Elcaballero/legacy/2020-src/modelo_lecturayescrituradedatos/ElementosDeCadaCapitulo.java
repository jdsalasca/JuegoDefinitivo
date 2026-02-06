
package modelo_lecturayescrituradedatos;

import java.util.ArrayList;


public class ElementosDeCadaCapitulo {
    private int number;
    private ArrayList text;
    
    public ElementosDeCadaCapitulo (int number, ArrayList text) {
        this.number = number;
        this.text = text;
    }


    public int getNumber() {
        return this.number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public ArrayList getText() {
        return this.text;
    }

    public void setText(ArrayList text) {
        this.text = text;
    }


@Override
public String toString() {
    String h = "";
    for (int i = 0; i < this.text.size(); i++) {
        
        h = h + "\n" + this.text.get(i);
               
    }
    return h;    
        
    }
    
}
