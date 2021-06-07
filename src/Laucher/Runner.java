package src.Laucher;

import java.io.IOException;
import java.util.Scanner;

import src.Keeper.ReadingStory;
import src.Players.BattlePanel;
import src.ui.PrincipalMenu;
import src.Events.GenericEvent;

//zona de pruebas jeje



public class Runner {
    static ReadingStory hiii = new ReadingStory("capitulo1.txt");
    GenericEvent invocacion =  new GenericEvent();
    static BattlePanel probaaaaando = new BattlePanel(); 

    public static void main(String[] args)  {
         
        System.out.println("EL CABALLERO DE LA ARMADURA OXIDADA!");
        // PrincipalMenu newUser = new PrincipalMenu();
        probaaaaando.attack();

        // newUser.principalMenu();
        

        
   
        }

    }



    