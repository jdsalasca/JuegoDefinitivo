package src.Keeper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;

import java.util.Scanner;


import src.Player.PrincipalCharacter;


    // private ReadingStory capitulo1;
    // private ReadingStory capitulo2;
    // private ReadingStory capitulo3;
    // private ReadingStory capitulo4;
    // private ReadingStory capitulo5;
    // private ReadingStory capitulo6;
    // private ReadingStory capitulo7;

    // public ReadingStory(){
    //    this.capitulo1= new ReadingStory();
    // // }

    // public ReadingStory getCapitulo1() {
    //     return this.capitulo1;
    // }
    

public class ReadingStory {



    
    private String fileName;
    public ArrayList <Story> Story;
    public int a = 0;
    private PrincipalCharacter newGame;

    public ReadingStory (String fileName) {
		this.fileName = fileName;
        this.Story = new ArrayList<Story>();
        this.newGame = new PrincipalCharacter();
    }
    public ReadingStory () {
		this.fileName = fileName;
        this.Story = new ArrayList<Story>();
        this.newGame = newGame;
    }




    public ReadingStory(String fileName, ArrayList<Story> Story) {
        this.fileName = fileName;
        this.Story = Story;
        this.newGame = new PrincipalCharacter();
        
        
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public void setStory(ArrayList<Story> Story) {
        this.Story = Story;
    }

    public int getA() {
        return this.a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public PrincipalCharacter getNewGame() {
        return this.newGame;
    }

    public void setNewGame(PrincipalCharacter newGame) {
        this.newGame = newGame;
    }



       
    public  void readingRecords() {
		File inFile = new File(this.fileName);
		try {
			Scanner sc = new Scanner(inFile);
			readItem(sc);
			sc.close();
		} catch (FileNotFoundException e) {
			System.out.println("Archivo inexistente -- " + this.fileName);
			System.exit(0);
		}
	}
	private  void readItem(Scanner sc) {
		while(sc.hasNextLine()){
        ArrayList<String> text = new ArrayList<>();

            for (int i = 0; i < 5; i++) {                
                text.add(sc.nextLine());
                if (!sc.hasNextLine()){
                    break;
                }

            }      

        Story v = new Story(a, text);
        a++;        
        this.Story.add(v);
        // text.clear();   
        
    }
    sc.close();
        
	}


    public ArrayList<Story> getStory(){
        return this.Story;
    }

    public  void writingRecords() {
        try {
            FileWriter myWriter = new FileWriter("Lore.txt");
            myWriter.write("\n");
            for (Story v: this.Story) {
                myWriter.write(v.toString());
				myWriter.write("\n");
            }    
            myWriter.close();                
        } 
        
        catch (Exception e) {
            System.out.println("Error salvando el lore");
        }
    }


    public void loadStory(){
        this.readingRecords();
    }
    public void saveStory(){
        this.writingRecords();
    }
//este sera un nuevo metodo para leer los archivos guardados de cada usuario
    private  void readingGames() {
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
    private void processLine(String line){
        Scanner sc = new Scanner(line);
        sc.useDelimiter("");
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
            default:System.out.println("error al cargar");
            break;
        }

        // this.newGame.setName(sc.next().trim());                         
        // this.newGame.setLife(sc.nextInt());
        // this.newGame.setStrong(sc.nextInt());
        // this.newGame.setDefense(sc.nextInt());
        // this.newGame.setAgility(sc.nextInt());
        // this.newGame.setIntelect(sc.nextInt());
        // this.newGame.setWisdom(sc.nextInt());

        // this.newGame.setName(sc.next().trim());                         
        // this.newGame.setLife(Integer.parseInt(sc.next().trim()));
        // this.newGame.setStrong(Integer.parseInt(sc.next().trim()));
        // this.newGame.setDefense(Integer.parseInt(sc.next().trim()));
        // this.newGame.setAgility(Integer.parseInt(sc.next().trim()));
        // this.newGame.setIntelect(Integer.parseInt(sc.next().trim()));
        // this.newGame.setWisdom(Integer.parseInt(sc.next().trim()));

            // ArrayList objets = sc.next().trim();
            // ArrayList Body = add(sc.next());



            // ArrayList Inventary
            // ArrayList equipedObjects
            // ArrayList inventory

                            

        
        sc.close();
    }

            public void test (){
                System.out.println(this.newGame.toString());
            }

            public  void writingGames() {
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

            public void loadGame(){
                this.readingGames();
            }
            public void saveGames(){
                this.writingGames();
            }

    


}
