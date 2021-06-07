package src.Events;

import java.util.Scanner;

public class GenericEvent {

    private int strong;
    private int life;
    private int min = 1;
    private int max =6;
    private int a;
    private Scanner sc =  new Scanner(System.in);


    public GenericEvent() {
    }

    public void randomNumber(int a){
        
            a = (int) (Math.random()* ((max-min)+1 ))+min;
            System.out.println(a);           
        
    }

    public void fight(){

        

    }

    public void event (){

        a = (int) (Math.random()* ((max-min)+1 ))+min;    

              
        
        switch (a) {
            case 1:
            System.out.println("uno");           
                
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





    
