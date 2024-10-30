package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleHelper {
     private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static void writeMessage(String message){
         System.out.println(message);
    }

    public static String readString(){
       String string = "";
          try{
      string = reader.readLine();
        }catch(IOException e){
            writeMessage("Помилка при спробі введення тексту. Спробуйте ще раз.");
      string = readString();
                  }
            return string;
            }

   public static int readInt(){
            int result = -1;
                try{
              result = Integer.parseInt(readString());
            }catch(NumberFormatException e){
              writeMessage("Помилка при спробі введення числа. Спробуйте ще раз");
            result = readInt();
                      }
                return result;
        }
}