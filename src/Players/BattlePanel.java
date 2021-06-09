package src.Players;

import java.util.Scanner;

// import java.security.InvalidParameterException;

import src.Keeper.ReadingStory;
import src.ui.PrincipalMenu;

public class BattlePanel {
    int dmg = 0;
    int vida_restante_enemigo = 0;
   

    private int strong;
    private int life;
    PrincipalMenu invocacion =  new PrincipalMenu();
    static PrincipalCharacter principal = new PrincipalCharacter();
    ReadingStory probando = new ReadingStory();
    Scanner sc = new Scanner(System.in);
    


    public BattlePanel() {
        this.strong = 10;
        this.life = 100;
    }

    public int randomAttack(int strong) {
        int a = (int) (Math.random()* ((strong-0)+1 ))+0;
        a = a*3;


        
        return a;
     }
     public void inicializeGame(){
        probando.setFileName("Lore.txt");
        probando.setNewGame(principal);
        probando.loadGame();
    }

    public void counterOfLife (){
        if (life<=0) {
            System.out.println("Ganaste");

       }else {
           System.out.println("perdiste");
       }
    }

    public void winner(){
        if (dmg>=100){System.out.println("has derrotado a tu enemigo");}
        // if (vida_restante_principal<0) {
        //     System.out.println("perdiste :c ");
            
        // }
    }
  


    public void attack(){
        
        int ataque = randomAttack(principal.getStrong());    
        int ataqueNPC = randomAttack(strong);

        if (ataque<ataqueNPC) {
            inicializeGame();
            int lifePrincipal = principal.getLife();
            System.out.println("te han  causado " + (ataqueNPC-ataque) + " de daño");
            System.out.println("tu vida restante es " + lifePrincipal);
            principal.setLife(lifePrincipal);
            probando.writingGames();


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
                attack();
                
                break;
            case 2:
                System.out.println("lograste escapar");
                invocacion.loadGame();

            
            break;
            case 0:
            System.out.println("lograste escapar");
            invocacion.loadGame();

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

    public void battlePanel(){

        
        
   
        
        
            while(this.life>0 && principal.getLife()>0){
                inicializeGame();
                System.out.println("1.atacar");
                System.out.println("2.inventario");
                System.out.println("3.invocar");
                System.out.println("4.huir");

                try {
                    System.out.println("escribe una de las opciones");
                    var opcion = sc.nextInt();
                    switch (opcion) {
                        case 1:
                        attack();
                            
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


        }}

    
    

    





    

