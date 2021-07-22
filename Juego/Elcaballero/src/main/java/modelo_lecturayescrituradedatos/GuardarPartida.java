
package modelo_lecturayescrituradedatos;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

    /** 
     * Esta clase es MUY importante porque es la que se debe invocar cada vez que
     * se necesite interactuar con el personaje principal, en esta clase
     * se guardar los atributos del personaje y se modifican dichos atributos
     * @author Juan David Salas Camargo
     * @version 1.0.0 

     */

     

public class GuardarPartida {
    PersonajePrincipal newGame = new PersonajePrincipal();
    private String fileName;
    private ArrayList <PersonajePrincipal> game;

    public PersonajePrincipal getNewGame() {
        return newGame;
    }

    public void setNewGame(PersonajePrincipal newGame) {
        this.newGame = newGame;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ArrayList<PersonajePrincipal> getGame() {
        return game;
    }

    public void setGame(ArrayList<PersonajePrincipal> game) {
        this.game = game;
    }
    
    
        public void guardarPartida() {
        try {
                    FileWriter myWriter = new FileWriter("Lore.txt");
                    myWriter.write(this.newGame.toString());
                    myWriter.write("\n");                    
                    myWriter.close();                
                } 
                
        catch (Exception e) {
                    System.out.println("Error al guardar");
                }
            }
    
    
    


    public void cargarPartida() {
		File inFile = new File(this.fileName);
		try {
			Scanner sc = new Scanner(inFile);
			readGameLine(sc);
			sc.close();
		} catch (FileNotFoundException e) {
			System.out.println("Archivo inexistente -- " + this.fileName);
			System.exit(0);
		}
	}
    
    
       
       
    private void readGameLine(Scanner sc){
        while(sc.hasNextLine()){
            String line = sc.nextLine();
            processLine(line);  
        }
    }
    
     /** 
     * ProcessLine is one the most beautiful methods of my proyect, what it do is 
     * change the vale of each line accord to the changes made for the user
     * in his game, adiccionally, the method who embboded processLine often is 
     * called when the user read the Story like a form to save the progress.
     * 
     * @version 1.0.0 

     */
     
    private void processLine(String line){
        Scanner sc = new Scanner(line);
        sc.useDelimiter(" ");
        String category = sc.next().trim().toLowerCase();
        switch (category) {
            case "name":this.newGame.setName(sc.next().trim());               
            break;
            case "life":this.newGame.setLife(Integer.parseInt(sc.next().trim()));
            break;
            case "strong":this.newGame.setStrong(Integer.parseInt(sc.next().trim())); 
            break;
            case "defense":this.newGame.setDefense(Integer.parseInt(sc.next().trim()));
            break;
            case "agility":this.newGame.setAgility(Integer.parseInt(sc.next().trim()));                    
            break;
            case "intelect":this.newGame.setIntelect(Integer.parseInt(sc.next().trim()));                    
            break;
            case "wisdom":this.newGame.setWisdom(Integer.parseInt(sc.next().trim()));                    
            break;
            case "progress":this.newGame.setProgress(Integer.parseInt(sc.next().trim()));                    
            break;
            default:System.out.println("error al cargar");
            break;
        }        
        sc.close();
    }


    
    /**
        public void cargarPartida_CrearPartida(){
        try{
            File myObj = new File("partida1");        
            FileWriter newGame = new FileWriter("partida1.txt");
                newGame.write(game.toString());
                newGame.write("\n");
                newGame.close();
        }
        catch (IOException e){
            System.out.println("no se puedo guardar la partida");
        }
            
    }
    
*/
    


       
}



    
    
    


    

