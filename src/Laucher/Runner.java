package src.Laucher;

import java.io.IOException;
import java.util.Scanner;

import src.Keeper.ReadingStory;
import src.ui.PrincipalMenu;

public class Runner {
    static ReadingStory hiii = new ReadingStory("capitulo1.txt");

    public static void main(String[] args)  {
         
        System.out.println("EL CABALLERO DE LA ARMADURA OXIDADA!");
        PrincipalMenu newUser = new PrincipalMenu();
        newUser.principalMenu();    
        }

    }


