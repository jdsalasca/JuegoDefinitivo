package src.Players;

// import java.security.InvalidParameterException;

import src.Keeper.ReadingStory;
import src.ui.PrincipalMenu;

public class BattlePanel {

    private int strong;
    private int life;
    PrincipalMenu invocacion =  new PrincipalMenu();
    PrincipalCharacter principal = new PrincipalCharacter();
    ReadingStory probando = new ReadingStory();


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
  


    public void attack(){
        
        int ataque = randomAttack(principal.getStrong());    
        int ataqueNPC = randomAttack(strong);

        if (ataque<ataqueNPC) {
            inicializeGame();            
            int lifePrincipal = principal.getLife();
            lifePrincipal =  lifePrincipal - (ataqueNPC-ataque);
            System.out.println("te han  causado " + (ataqueNPC-ataque) + " de daño");
            principal.setLife(lifePrincipal);
            probando.writingGames();

        } else if(ataque>ataqueNPC){
            
            life =  life - (ataque- ataqueNPC);
            System.out.println("has causado " + (ataque- ataqueNPC) + " de daño");
        

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

            
            break;
        
            default:
                break;
        }

        

    }


    





    
}
