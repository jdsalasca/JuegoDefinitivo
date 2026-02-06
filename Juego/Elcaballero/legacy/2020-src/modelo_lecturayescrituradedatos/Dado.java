
package modelo_lecturayescrituradedatos;

import controlador.PanelDeBatalla;
import java.util.Scanner;

public class Dado {
    private int strong;
    private int life;
    private int min = 1;
    private int max =2;
    private int a;
    private Scanner sc =  new Scanner(System.in);
    PanelDeBatalla pb = new PanelDeBatalla();



    
    

    public void numeroAleatrorio(int a){
        
            a = (int) (Math.random()* ((max-min)+1 ))+min;
            System.out.println(a);           
        
    }

    public void opcionesDato (){

        a = (int) (Math.random()* ((max-min)+1 ))+min;    

              
        
        switch (a) {
            case 1:
            System.out.println("detras de un arbol te has encontrado con un enemigo");
            pb.battlePanel();
            // System.out.println("uno");   
                
                break;
            case 2:
            System.out.println("dos");

                break;
            case 3:
            System.out.println("tres");

                break;
            case 4:
            System.out.println("cuatro");

                break;
            case 5:
            System.out.println("cinco");

                break;
            
            case 6:
            System.out.println("seis");

                break;
        
            default:
            System.out.println("error");
                break;
        }


    }
}
