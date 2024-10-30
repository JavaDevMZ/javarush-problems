package chat.client;

import chat.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BotClient extends Client {

    public static void main(String[] args){
        BotClient botClient = new BotClient();
        botClient.run();
          }
    protected SocketThread getSocketThread(){
        return new BotSocketThread();
    }

    protected boolean shouldSendTextFromConsole(){
        return false;
    }

    public String getUserName(){
        return "date_bot_" + (int)(Math.random()*100);
    }

    public class BotSocketThread extends SocketThread{

        protected void clientMainLoop() throws IOException, ClassNotFoundException{
          sendTextMessage("Привіт чатику. Я робот. Розумію команди: дата, день, місяць, рік, час, година, хвилини, секунди.");
          super.clientMainLoop();
        }

        protected void processIncomingMessage(String message){
                 if(message==null||!message.contains(": ")){
                return;
            }
               String userName = message.substring(0, message.indexOf(": "));
              String command = message.substring(message.indexOf(": ")+2);
           ConsoleHelper.writeMessage(message);
              String pattern = "";
              switch(command){
                case "час":
                pattern = "H:mm:ss";
                break;
                case "година":
                pattern = "H";
                break;
                case "хвилини":
                pattern = "m";
                break;
                case "секунди":
                pattern = "s";
                break;
                case "дата":
                pattern = "d.MM.YYYY";
                break;
                case "день":
                pattern = "d";
                break;
                case "місяць":
                pattern = "MMMM";
                break;
                case "рік":
                pattern = "YYYY";
                break;
                default:
                return;
            }
           SimpleDateFormat format = new SimpleDateFormat(pattern);
           sendTextMessage("Інформація для "+ userName + ": " + format.format(Calendar.getInstance().getTime()));

                  }
    }
}