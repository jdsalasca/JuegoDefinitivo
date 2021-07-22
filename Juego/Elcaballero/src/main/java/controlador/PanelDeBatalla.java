/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import java.util.Scanner;
import modelo_lecturayescrituradedatos.GuardarPartida;
import modelo_lecturayescrituradedatos.PersonajePrincipal;
import modelo_lecturayescrituradedatos.LecturaDeCapitulos;
import vista.MenuPrincipal;

/**
 *
 * @author Juan David Salas Camargo
 */
public class PanelDeBatalla {
    int dmg = 0;
    int vida_restante_enemigo = 0;
    public Scanner leer = new Scanner (System.in);
    private int strong;
    private int life;
    MenuPrincipal invocacion =  new MenuPrincipal();
    LecturaDeCapitulos lector_de_Capiturlos = new LecturaDeCapitulos();    
    PersonajePrincipal principal = new PersonajePrincipal();
    GuardarPartida cargar_y_Guardar = new GuardarPartida();
    Scanner sc = new Scanner(System.in);
    
    


    public PanelDeBatalla() {
        this.strong = 10;
        this.life = 100;
    }

    public int randomAttack(int strong) {
        int a = (int) (Math.random()* ((strong-0)+1 ))+0;
        a = a*3;


        
        return a;
     }
     public void inicializeGame(){
        cargar_y_Guardar.setFileName("Lore.txt");
        cargar_y_Guardar.setNewGame(principal);
        cargar_y_Guardar.cargarPartida();
    }

    public void ganador_o_perdedor (){
        if (life<=0) {
            System.out.println("Ganaste");

       }else {
           System.out.println("perdiste");
       }
    }

    public void ganador(){
        inicializeGame();
        int lifePrincipal = principal.getLife();
        if (dmg>=30){System.out.println("has derrotado a tu enemigo"); invocacion.loadGameAfterBattle();}
        if (lifePrincipal<=0) {
            System.out.println("perdiste :c ");
            
        }
    }
  


    public void ataque(){
        
        int ataque = randomAttack(principal.getStrong());    
        int ataqueNPC = randomAttack(strong);

        if (ataque<ataqueNPC) {
            inicializeGame();
            int lifePrincipal = principal.getLife();
            System.out.println("te han  causado " + (ataqueNPC-ataque) + " de daño");
            System.out.println("tu vida restante es " + lifePrincipal);
            principal.setLife(lifePrincipal - (ataqueNPC-ataque));
            cargar_y_Guardar.guardarPartida();


        } else if(ataque>ataqueNPC){
            dmg = dmg + (ataque-ataqueNPC);
            vida_restante_enemigo=this.life - (ataque-ataqueNPC);
            
            life =  life - (ataque- ataqueNPC);
            System.out.println("has causado " + (ataque- ataqueNPC) + " de daño");
            System.out.println("Vida restante del enemigo " + vida_restante_enemigo);
        

        }else {
            System.out.println("el enemigo esquivo el ataque");

        }
    }

    public void huir (){
        int a = (int) (Math.random()* ((2-1)+1 ))+0;

        switch (a) {
            case 1:
                System.out.println("escapaste sin exito");
                ataque();
                
                break;
            case 2:
                System.out.println("lograste escapar");
                invocacion.loadGameAfterBattle();

            
            break;
            case 0:
            System.out.println("lograste escapar");
            invocacion.loadGameAfterBattle();

                break;
        
            default:
                System.out.println("no reconocido, numero ingresado: " + a);
                break;
        }
    }

    public void invetory(){
        System.out.println("aun no se ha implementado");
    }

    public void invocation(){
        System.out.println("aun no se ha implementado");
    }
    
    public int Scanner () {
        int c;
        c = leer.nextInt();
        return c;
    }
 
    public void battlePanel(){
        
        
     
        
   
        
        
        while(this.life>0 && principal.getLife()>0){
            inicializeGame();
            System.out.println("1.atacar");
            System.out.println("2.inventario");
            System.out.println("3.invocar");
            System.out.println("4.huir");
            ganador();



            try {
                System.out.println("escribe una de las opciones");
                var opcion = sc.nextInt();
                switch (opcion) {
                    case 1:
                    ataque();

                        break;
                    case 2:
                    invetory();

                    break;
                    case 3:
                    invocation();

                    break;
                    case 4:
                    huir();


                    break;

                    default:
                        break;
                }
            } catch (Exception e) {
                //TODO: handle exception
            }
    }


        }
    
}
