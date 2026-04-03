import java.util.Scanner;

public class InputUtil {
    static Scanner sc=new Scanner(System.in);

    public static int getIntegerInput(String message){
        while(true){
            try{
                System.out.println(message);
                return Integer.parseInt(sc.nextLine());
            }
            catch (Exception e){
                System.out.println("Invalid Number");
            }
        }
    }
    public static String getStringInput(String message){
        while(true){
            System.out.println(message);
            String input=sc.nextLine().trim();
            if(!input.isEmpty()){
                return input;
            }
            else{
                System.out.println("Empty Input");
            }
        }
    }
}
