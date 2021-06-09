package src.ui;

import java.util.Scanner;

import src.Events.GenericEvent;
import src.Keeper.ReadingStory;

import src.Players.PrincipalCharacter;

public class PrincipalMenu {

    Scanner sc =new Scanner(System.in);
    Scanner nn= new Scanner(System.in);
    boolean salir = false;
    int opcion;
    int opcion2;
   
    
    
    ReadingStory cap1 = new ReadingStory("Capitulo1.txt");
    PrincipalCharacter newGame = new PrincipalCharacter();
    ReadingStory probando = new ReadingStory();
    static GenericEvent menu_nivel_2 = new GenericEvent();

    public PrincipalMenu() {
    }
    

    public void beginStory(){

        inicializeGame();
        cap1.loadStory();
        cap1.Story.size();
        newGame.getProgress();;
        int a = newGame.getProgress();
        for (int i = 0; i < cap1.Story.size(); i++){
        // System.out.println("hola");
    

        System.out.println((a*100/cap1.Story.size()) + "% de la Historia");
                 
        System.out.println(cap1.Story.get(i));
        System.out.println("deseas continuar? 1. si 2. salir");
        opcion2 = sc.nextInt();
        if (opcion2 == 2) {System.exit(0);}
  
    }
}
    public void inicializeGame(){
        probando.setFileName("Lore.txt");
        probando.setNewGame(newGame);
        probando.loadGame();
    }
    public void loadGame (){
        
        //se parece mucho a beginStory pero tiene un anidado nuevo que lo hace depender del archivo guardad para continuar la partida
        //Este metodo mas adelante tendra que inicializar el cap2, cap3....cap8. 
        inicializeGame();
        cap1.loadStory();
        cap1.Story.size();
        newGame.getProgress();
        int a = newGame.getProgress();
        for (a =newGame.getProgress() ; a< cap1.Story.size(); a++){
            menu_nivel_2.event();
            System.out.println((a*100/cap1.Story.size()) + "% de la Historia");
            // menu_nivel_2.event();
            System.out.println(cap1.Story.get(a));
            System.out.println("deseas continuar? 1. si 2. salir");
            opcion2 = sc.nextInt();
            if (opcion2 == 2) {newGame.setProgress(a);probando.writingGames();System.exit(0);}
        }
    }
    public void loadGameAfterBattle(){
        //se parece mucho a LoadGame pero es para que el usuario no pieda el hilo de la historia
        // lo que hace es retroceder en el proceso un poco
        inicializeGame();
        cap1.loadStory();
        cap1.Story.size();
        
        newGame.setProgress(newGame.getProgress()-2);
        probando.writingGames();
        newGame.getProgress();
        
        int a = newGame.getProgress();
        for (a =newGame.getProgress() ; a< cap1.Story.size(); a++){
            menu_nivel_2.event();
            System.out.println((a*100/cap1.Story.size()) + "% de la Historia");
            // menu_nivel_2.event();
            System.out.println(cap1.Story.get(a));
            System.out.println("deseas continuar? 1. si 2. salir");
            opcion2 = sc.nextInt();
            if (opcion2 == 2) {newGame.setProgress(a);probando.writingGames();System.exit(0);}
        }
        
    }


    

    public void createNewGame (){


        inicializeGame();
        System.out.println(newGame.getName());

        newGame.getName();
        String name = newGame.getName();
        if (name.equals("generic") ){
        // System.out.println(probando.getName());
        // System.out.println("Bienvenido al Caballero de la Armadura Oxidada");
        // System.out.println("a continuacion viajaras por un mundo de fantasia");
        // System.out.println("lleno de cosas interesantes por descubrir :3");        
        System.out.println("como quieres que se llame tu personaje?");
        String newName = nn.nextLine();
        newGame.setName(newName);
        probando.writingGames();
        }
        else  {
        System.out.println();

        }       

        System.out.println("Bienvenido " + newGame.getName());
        System.exit(0);
    }
    public void principalMenu() {
        inicializeGame();
        while(!salir){
            System.out.println("probando si cambia el atributo, Hola  " + newGame.getName());
            System.out.println("1. Nueva partida");
            System.out.println("2. Cargar partida");
            System.out.println("3. Configuraciones");
            System.out.println("4.crear personaje");
            System.out.println("5.salir");            

            try {
                System.out.println("Escribe una de las opciones");
                opcion = sc.nextInt();
                switch (opcion) {
                    case 1:
                    // try {
                    //     System.out.println("hola");
                     
                    // } catch (Exception e) {
                    //     System.out.println("no esta funcionando el catch");
                        beginStory();
                        menu_nivel_2.event();

                    // }
                            
                    case 2:
                    
                    loadGame();

                        break;
                    case 3:
                    menu_nivel_2.event();

                        break;
                    case 4:createNewGame();   
                        break;
                    case 5:System.exit(0);
                        break;
                    default:
                        System.out.println("solo numeros entre 1 y 4 c:");
                }
            } catch (Exception e) {
                System.out.println("");
                sc.next();
                //TODO: handle exception
            }
        }
    }


}