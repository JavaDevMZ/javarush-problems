package chat.client;

public class ClientGuiController extends Client{

    private ClientGuiModel model = new ClientGuiModel();
    private ClientGuiView view = new ClientGuiView(this);
                        
    public static void main(String[] args){
        ClientGuiController instance = new ClientGuiController();
        instance.run();
    }
    protected SocketThread getSocketThread(){
        return new GuiSocketThread();
    }

    public void run(){
        SocketThread socketThread = getSocketThread();
        socketThread.run();
    }

    public String getServerAddress(){
        return view.getServerAddress();
    }

    public int getServerPort(){
        return view.getServerPort();
    }

    public String getUserName(){
        return view.getUserName();
    }

    public ClientGuiModel getModel(){
        return model;
    }

    public class GuiSocketThread extends SocketThread{

       /* public GuiSocketThread(Client client){
            client.super();
        } */

        protected void processIncomingMessage(String message){
           model.setNewMessage(message);
           view.refreshMessages();
        }

        protected void informAboutAddingNewUser(String userName){
           model.addUser(userName);
           view.refreshUsers();
        }

        protected void informAboutDeletingNewUser(String userName){
           model.deleteUser(userName);
           view.refreshUsers();
        }

        public void notifyConnectionStatusChanged(boolean clientConnected){
            view.notifyConnectionStatusChanged(clientConnected);
        }
    }
}
    
