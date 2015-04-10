package stimko;

/**
 * Classe que abstrai a utilização da classe Scanner, escondendo todos os
 * problemas relacionados com excepções, e que oferece metodos simples e
 * robustos para a leitura de valores de tipos simples.
 * 
 * Utilização tipica:  int x = Input.lerInt();
 *                     String nome = Input.lerString();
 * 
 * @author F. Mario Martins
 * @version 1.0 (6/2006)
 */
import static java.lang.System.out;
import static java.lang.System.in;
import java.util.Scanner;
import java.util.InputMismatchException;

public class Input {

 /**
  * Metodos de Classe
  */
 
   public static String lerYesOrNo() {
     Scanner input = new Scanner(in);
     boolean ok = false; 
     String txt = "";
     while(!ok) {
         try {
             txt = input.nextLine();
             if(txt.equals("y") || txt.equals("Y") || txt.equals("n") || txt.equals("N"))
             {
                 ok = true ;
             }
             else
             {
                 out.println("\t\t\t\tCaractér Inválido"); 
                 out.print("\n\t\t\t\tNovo valor: ");
                 ok = false;
             }
         }
         catch(InputMismatchException e) 
             { out.println("\t\t\t\tTexto Invalido"); 
               out.print("\n\t\t\t\tNovo valor: ");
               input.nextLine(); 
         }
     }
     //input.close();
     return txt;
  }
   
 public static String lerString() {
     Scanner input = new Scanner(in);
     boolean ok = false; 
     String txt = "";
     while(!ok) {
         try {
             txt = input.nextLine();
             ok = true;
         }
         catch(InputMismatchException e) 
             { out.println("\t\t\t\tTexto Invalido"); 
               out.print("\n\t\t\t\tNovo valor: ");
               input.nextLine(); 
             }
     }
     //input.close();
     return txt;
  } 

 public static int lerInt() {
     Scanner input = new Scanner(in);
     boolean ok = false; 
     int i = 0; 
     while(!ok) {
         try {
             i = input.nextInt();
             if(i>0)
             {
                 ok = true;
             }
             else
             {
                out.println("\t\t\t\tValor Inválido"); 
                out.print("\n\t\t\t\tNovo valor: ");
                input.nextLine();             
             } 
            }
         catch(InputMismatchException e) 
             { out.println("\t\t\t\tValor Inválido"); 
               out.print("\n\t\t\t\tNovo valor: ");
               input.nextLine(); 
             }

     }
     return i;
  } 
 
  public static int lerIntIntervalo(int inf, int sup) {
     Scanner input = new Scanner(in);
     boolean ok = false; 
     int i = 0; 
     while(!ok) {
         try {
             i = input.nextInt();
             if(i < inf || i > sup)
             {
                 out.println("\t\t\t\tValor Inválido"); 
                 out.print("\n\t\t\t\tNovo valor: ");
                 ok = false;
             }
             else
             {
                 ok = true;
             }
             
         }
         catch(InputMismatchException e) 
             { out.println("\t\t\t\tValor Inválido"); 
               out.print("\n\t\t\t\tNovo valor: ");
               input.nextLine(); 
             }
     }    
    // input.close();
     return i;
  } 
  
    public static double lerDouble() {
     Scanner input = new Scanner(in);
     boolean ok = false; 
     double d = 0.0; 
     while(!ok) {
         try {
             d = input.nextDouble();
             if(d>0)
             {
                 ok = true;
             }
             else
             {
                out.println("\t\t\t\tValor Inválido"); 
                out.print("\n\t\t\t\tNovo valor: ");
                input.nextLine();             
             }          }
         catch(InputMismatchException e) 
             { out.println("\t\t\t\tValor Inválido"); 
               out.print("\n\t\t\t\tNovo valor: ");
               input.nextLine(); 
             }
     }
     //input.close();
     return d;
  }  

  
   public static float lerFloat() {
     Scanner input = new Scanner(in);
     boolean ok = false; 
     float f = 0.0f; 
     while(!ok) {
         try {
             f = input.nextFloat();
             ok = true;
         }
         catch(InputMismatchException e) 
             { out.println("\t\t\t\tValor Inválido"); 
               out.print("\n\t\t\t\tNovo valor: ");
               input.nextLine(); 
             }
     }
     //input.close();
     return f;
  }  
  
   public static boolean lerBoolean() {
     Scanner input = new Scanner(in);
     boolean ok = false; 
     boolean b = false; 
     while(!ok) {
         try {
             b = input.nextBoolean();
             ok = true;
         }
         catch(InputMismatchException e) 
             { out.println("\t\t\t\tBooleano Invalido"); 
               out.print("\n\t\t\t\tNovo valor: ");
               input.nextLine(); 
             }
     }
     //input.close();
     return b;
  } 
  
  public static short lerShort() {
     Scanner input = new Scanner(in);
     boolean ok = false; 
     short s = 0; 
     while(!ok) {
         try {
             s = input.nextShort();
             ok = true;
         }
         catch(InputMismatchException e) 
             { out.println("\t\t\t\tShort Invalido"); 
               out.print("\n\t\t\t\tNovo valor: ");
               input.nextLine(); 
             }
     }
     //input.close();
     return s;
  }   
  
 
}

