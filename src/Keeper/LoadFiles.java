package src.Keeper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import java.util.Scanner;

import src.Players.PrincipalCharacter;


public class LoadFiles {
    



    private String fileName;
    public ArrayList <PrincipalCharacter> game;

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ArrayList<PrincipalCharacter> getGame() {
        return this.game;
    }

    public void setGame(ArrayList<PrincipalCharacter> game) {
        this.game = game;
    }

    public void readingFiles(){
        File inFile= new File(this.fileName);
        try{
            Scanner sc = new Scanner(inFile);
            readItem(sc);
            sc.close();
        } catch (FileNotFoundException e){
            System.out.println("archivo inexistente--" + this.fileName);
            System.exit(0);
            }   
    }

    private void readItem(Scanner sc){
        while(sc.hasNextLine()){
            String line = sc.nextLine();
            processLine(line);  
        }
    }
    private void processLine(String line){
        Scanner sc = new Scanner(line);
        sc.useDelimiter(",");
        String category = sc.next().trim().toLowerCase();
        switch (category) {
            case "User":
            String name =sc.next().trim();
            int life= Integer.parseInt(sc.next().trim());
            int strong = Integer.parseInt(sc.next().trim());
           // ArrayList objets = sc.next().trim();
            // ArrayList Body = add(sc.next());
            int defense =Integer.parseInt(sc.next().trim());
            int Agility =Integer.parseInt(sc.next().trim());
            int intelect=Integer.parseInt(sc.next().trim());
            int wisdom =Integer.parseInt(sc.next().trim());
            // ArrayList Inventary
            // ArrayList equipedObjects
            // ArrayList inventory
            PrincipalCharacter c = new PrincipalCharacter();
            this.game.add(c);                
                break;
        
            default:
                System.out.println("error al guardar el archivo");
                break;
        }
        sc.close();
    }
    public void writingRecords(){
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

    
    }



    // private void add ( Scanner sc){
    //     ArrayList body = new ArrayList<>();
    //     sc.useDelimiter(',');
    //     body.add(sc);
            
    //     }
        

    
    
    

