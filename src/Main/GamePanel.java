package Main;

import java.awt.Dimension;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.awt.*;

import javax.net.ssl.TrustManagerFactorySpi;
import javax.swing.JPanel;

import com.google.gson.Gson;

import entity.Player;
import entity.PlayerUI;
import tile.TileManager;

public class GamePanel extends JPanel implements Runnable{
    //SCREEN SETTING
    final int originalTileSize = 16; // 16*16 tile
    final int scale = 3;
    public final int tileSize = originalTileSize * scale;
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;
    
    //World setting
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;
    public final int worldWidth = tileSize * maxWorldCol;
    public final int worldHeight = tileSize * maxWorldRow;

    // FPS
    int FPS = 60;

    TileManager tileM = new TileManager(this);
    KeyHandler keyH = new KeyHandler();
    Thread gameThread;
    public CollisionChecker cChecker = new CollisionChecker(this);
    // public Player player = new Player(this, keyH);

    public ArrayList<Player> players = new ArrayList<Player>();
    Player playerCurrent = null;
    public PlayerUI playerUI = null;
    Socket socket = null;
    DataInputStream dis = null;
    DataOutputStream dos = null;
    int ip = 8080;

    public int id;

    public GamePanel(){

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
    }

    public void startGameThread(){
        gameThread = new Thread(this);
        gameThread.start();
    }
    // @Override
    // public void run() {
    //     double drawInterval = 1000000000/FPS; // 0.01666s
    //     double nextDrawTime = System.nanoTime() + drawInterval;
    //     while (gameThread != null){

    //         // Update: update information
    //         update();
    //         // DRAW: draw the screen with information
    //         repaint();
    
    //         try {
    //             double remainingTime = nextDrawTime -System.nanoTime();
    //             remainingTime = remainingTime/1000000;

    //             if(remainingTime < 0){
    //                 remainingTime = 0;
    //             }
    //             Thread.sleep((long) remainingTime);

    //             nextDrawTime += drawInterval;
    //         } catch (InterruptedException e) {
    //             // TODO Auto-generated catch block
    //             e.printStackTrace();
    //         }
    //     }
    // }
    public void run() {
        double drawInterval = 1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0 ;
        int drawCount =0;

        
        while (gameThread != null) {
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;
            if (delta >=1){
                update();
                repaint();
                delta--;
                drawCount++;
            }
            if (timer >=1000000000){
                System.out.println("FPS:"+ drawCount);
                drawCount = 0;
                timer = 0;
            }
        }
    }

    public void update() {
        playerUI.update();
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        tileM.draw(g2);
        for(Player player: players)
        if(!player.logout){
            new PlayerUI(this, keyH, id).draw(g2);;
        }
        g2.dispose();
    }

    //create socket clients and connect to server
    public void connectToServer(){
        try{
            socket = new Socket("localhost", ip);
            System.out.println("Connected to server");
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            this.id = Integer.parseInt(dis.readUTF());
            System.out.println("Your id is: " + this.id);
            Gson gson = new Gson();
            Player[] players = gson.fromJson(dis.readUTF(), Player[].class);
            this.players = new ArrayList<Player>();
            for(Player player: players){
                if(player.getId() == this.id)
                this.playerCurrent = player;
                this.players.add(player);
            }
            System.out.println("Received players: " + this.players.size());
        }catch(Exception e){
            System.out.println("Could not connect to server");
        }
    }

       //send data to server and receive data from server
    //
    public void sendData(String data){
        Thread thread = new Thread(new Runnable(){
            public void run(){
                try{ 
                    dos.writeUTF(data);
                }catch(Exception e){
                    System.out.println("Could not send data to server");
                }
            }
        });
        thread.start();
    }

    // create thread to receive arraylist json from server
    public void receiveData(){
        Thread thread = new Thread(new Runnable(){
            public void run(){
                while (true) {
                    try{
                    Gson gson = new Gson();
                    Player[] p = gson.fromJson(dis.readUTF(), Player[].class);
                    players = new ArrayList<Player>();
                    for(Player player: p){
                        if(player.getId() == id)
                        playerCurrent = player;
                        players.add(player);
                        
                    }
                }catch(Exception e){
                    System.out.println("Could not receive data from server");
                }
                }
                
            }
        });
        thread.start();
    }
    //set players
    public void setPlayers(ArrayList<Player> players){
        this.players = players;
    }
}
