package chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    public static void main(String[] args){
        ConsoleHelper.writeMessage("Choose a port for the server: ");
            int port = ConsoleHelper.readInt();
       try(ServerSocket serverSocket = new ServerSocket(port)){
             ConsoleHelper.writeMessage("Server started");

         while(true){
            Socket socket = null;
              if((socket = serverSocket.accept())!=null){
               new Handler(socket).start();
                continue;
            }
        }
            }catch(Exception e){
                ConsoleHelper.writeMessage(e.getMessage());
            }
    }

    public static void sendBroadcastMessage(Message message){
           try{
               for(Map.Entry<String, Connection> entry : connectionMap.entrySet()){
          entry.getValue().send(message);
            }
                }catch(Exception e){
                    ConsoleHelper.writeMessage("Не вдалося надіслати повідомлення");
                }
        }

    private static class Handler extends Thread{

        private Socket socket;

        public Handler(Socket socket){
            this.socket = socket;
        }

        public void run(){
             String username = "";
                       try(Connection connection = new Connection(socket)){
           System.out.printf("Connection with remote address %s has been established\n", socket.getRemoteSocketAddress().toString());
            username = serverHandshake(connection);
             sendBroadcastMessage(new Message(MessageType.USER_ADDED, username));
             notifyUsers(connection, username);
             serverMainLoop(connection, username);
                                              }catch(Exception e){
                             ConsoleHelper.writeMessage("An exception occured while exchanging data with remote address");
                     }finally{
                              connectionMap.remove(username);
             sendBroadcastMessage(new Message(MessageType.USER_REMOVED, username));
             ConsoleHelper.writeMessage("Connection with remote address " + socket.getRemoteSocketAddress() + " has been closed." );
                     }
                                  }

        private String serverHandshake(Connection connection) throws ClassNotFoundException, IOException{
               Message nameRequest = new Message(MessageType.NAME_REQUEST, "What's your name?");
               connection.send(nameRequest);
               Message response = connection.receive();
               String name = response.getData();
                     if(MessageType.USER_NAME==response.getType()&&!name.isEmpty()&&name!=null&&!connectionMap.keySet().contains(name)){
                    connectionMap.put(response.getData(), connection);
                    connection.send(new Message(MessageType.NAME_ACCEPTED, "Name has been accepted"));
                          }else{
                           return serverHandshake(connection);
                        }
                        return response.getData();
                         }

        private void notifyUsers(Connection connection, String username) throws IOException{

                         for(String name : connectionMap.keySet()){
                             Message message = new Message(MessageType.USER_ADDED, name);
                             if(!username.equals(name)){
                                connection.send(message);
                                }
                        }
               }

        private void serverMainLoop(Connection connection, String username) throws IOException, ClassNotFoundException {
                        while(true){
               Message message = connection.receive();
               if(message.getType()==MessageType.TEXT){
                  String messageText = username + ": " + message.getData();
                  sendBroadcastMessage(new Message(MessageType.TEXT, messageText));
                            }else{
                  ConsoleHelper.writeMessage("An error occurred");
                        }
                    }
           }
    }
}