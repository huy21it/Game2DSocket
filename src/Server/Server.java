package Server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import entity.Player;
import com.google.gson.*;

public class Server {
    static ArrayList<ServerHandler> clients = new ArrayList<ServerHandler>();
    static ArrayList<Player> players = new ArrayList<Player>();
    static int id = 0;
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        ServerSocket serverSocket = new ServerSocket(8080);
        //random number
        Random random = new Random();
        
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("New client connected");
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            dos.writeUTF(id+"");
            dos.flush();

            Player p = new Player(random.nextInt(600), random.nextInt(600), id);
            players.add(p);
            ServerHandler serverHandler = new ServerHandler(socket, dis, dos, id);
            clients.add(serverHandler);
            id++;
            GsonBuilder gsonBuilder = new GsonBuilder();
		
		    Gson gson = gsonBuilder.create();
 
		    String JSONObject = gson.toJson(players);
            dos.writeUTF(JSONObject);
            dos.flush();


            Thread thread = new Thread(serverHandler);
            thread.start();
        }
        
    }
}
class ServerHandler implements Runnable {
    private Socket socket;
    public DataOutputStream dos;
    private DataInputStream dis;
    private String name;
    private int id;
    public boolean logout = false;

    public ServerHandler(Socket socket, DataInputStream dis, DataOutputStream dos, int id) {
        this.socket = socket;
        this.dis = dis;
        this.dos = dos;
        this.id = id;
    }

    @Override
    public void run() {
        
        try {
            while (true) {
                String message = dis.readUTF();
                
                switch (message) {
                    case "LEFT":
                        Server.players.get(this.id).setDirection("left");
                        break;
                    case "RIGHT":
                        Server.players.get(this.id).setDirection("right");
                        break;
                    case "UP":
                        Server.players.get(this.id).setDirection("up");
                        break;
                    case "DOWN":
                        Server.players.get(this.id).setDirection("down");
                        break;
                    case "STAND":
                        break;
                    case "ATTACK":
                        break;
                    default:
                        break;
                }
                
                System.out.println(id+" "+message);
                GsonBuilder gsonBuilder = new GsonBuilder();
		
		        Gson gson = gsonBuilder.create();
 
		        String JSONObject = gson.toJson(Server.players);
                for (ServerHandler client : Server.clients) 
                if(client.logout == false){
                    client.dos.writeUTF(JSONObject);
                    client.dos.flush();
                }
                // Server.players.get(this.id).setAttack(false);
            }
        } catch (IOException e) {
            Server.players.get(this.id).logout = true;
            this.logout = true;
            e.printStackTrace();
        }
        
    }
    
}
