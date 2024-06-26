package project;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Random;
 
public class BallEaterGame extends JPanel implements ActionListener, KeyListener {
final static int WIDTH = 1600;
final static int HEIGHT = 1200;
   Timer timer;
   boolean[] keys;
   PlayerBall player;
   ArrayList<EnemyBall> enemies;
   boolean gameOver;
   JFrame frame;
   private int enemyCount; 
 
   public BallEaterGame(JFrame frame) {
       this.frame = frame;
       setFocusable(true);
       keys = new boolean[4];
       player = new PlayerBall(WIDTH / 2, HEIGHT / 2);
       enemies = new ArrayList<>();
       for (int i = 0; i < 100; i++) { // 增加红球数量到50
           // 确保红球大小小于蓝球的大小（20）
           int enemySize = new Random().nextInt(15) + 5; // 红球大小在5到20之间
           enemies.add(new EnemyBall(new Random().nextInt(WIDTH), new Random().nextInt(HEIGHT), enemySize));
       }
       gameOver = false;
 
       timer = new Timer(20, this);
       timer.start();
 
       addKeyListener(this);
       enemyCount = enemies.size(); 
   }
 
   public void actionPerformed(ActionEvent e) {
       if (gameOver) return;
 
       // Player movement
       if (keys[0]) player.move(-1, 0);
       if (keys[1]) player.move(1, 0);
       if (keys[2]) player.move(0, -1);
       if (keys[3]) player.move(0, 1);
 
       // Enemy movement and collision detection
       for (int i = 0; i < enemies.size(); i++) {
           EnemyBall enemy = enemies.get(i);
           enemy.move();
       }
       checkCollisions();
 
       // Check for game over conditions
       if (player.x - player.size / 2 < 0 || player.x + player.size / 2 > WIDTH ||
       player.y - player.size / 2 < 0 || player.y + player.size / 2 > HEIGHT) {
       gameOver = true;
   }
 
       if (gameOver) {
           timer.stop();
           int result = JOptionPane.showConfirmDialog(frame, "游戏结束！\n是否重新开始游戏？", "游戏结束", JOptionPane.YES_NO_OPTION);
           if (result == JOptionPane.YES_OPTION) {
               resetGame();
           }
       }
 
       repaint();
   }
 
   public void paintComponent(Graphics g) {
       super.paintComponent(g);
       if (!gameOver) {
           player.draw(g);
           for (EnemyBall enemy : enemies) {
               enemy.draw(g);
           }
       }
   }
 
   // KeyListener methods
   public void keyPressed(KeyEvent e) {
       switch (e.getKeyCode()) {
           case KeyEvent.VK_LEFT:
               keys[0] = true;
               break;
           case KeyEvent.VK_RIGHT:
               keys[1] = true;
               break;
           case KeyEvent.VK_UP:
               keys[2] = true;
               break;
           case KeyEvent.VK_DOWN:
               keys[3] = true;
               break;
       }
   }
 
   public void keyReleased(KeyEvent e) {
       switch (e.getKeyCode()) {
           case KeyEvent.VK_LEFT:
               keys[0] = false;
               break;
           case KeyEvent.VK_RIGHT:
               keys[1] = false;
               break;
           case KeyEvent.VK_UP:
               keys[2] = false;
               break;
           case KeyEvent.VK_DOWN:
               keys[3] = false;
               break;
       }
   }
 
   public void keyTyped(KeyEvent e) { }
 
   // Reset game
   private void resetGame() {
    player = new PlayerBall(WIDTH / 2, HEIGHT / 2);
    enemies.clear();
    for (int i = 0; i < 50; i++) {
    	 int enemySize = new Random().nextInt(15) + 5; // 红球大小在5到20之间
    	 enemies.add(new EnemyBall(new Random().nextInt(WIDTH), new Random().nextInt(HEIGHT), enemySize));
    }
    gameOver = false;
    keys = new boolean[4]; // Reset keys to stop any movement
    timer.start();
 }
 
   // Check collisions
   private void checkCollisions() {
    ArrayList<EnemyBall> toRemove = new ArrayList<>(); // List to store enemies to remove
    for (int i = 0; i < enemies.size(); i++) {
        EnemyBall enemy1 = enemies.get(i);
        for (int j = i + 1; j < enemies.size(); j++) {
            EnemyBall enemy2 = enemies.get(j);
            if (enemy1.collidesWith(enemy2)) {
                if (enemy1.size > enemy2.size) {
                    enemy1.eat(enemy2);
                    toRemove.add(enemy2); // Add to the list to remove
                    createNewEnemyBall(enemy1, enemy2); // Create new enemy
                } else if (enemy2.size > enemy1.size) {
                    enemy2.eat(enemy1);
                    toRemove.add(enemy1); // Add to the list to remove
                    createNewEnemyBall(enemy2, enemy1); // Create new enemy
                    break; // No need to continue as enemy1 will be removed
                }
            }
        }
        if (player.collidesWith(enemy1)) {
            if (player.size <= enemy1.size) {
                gameOver = true;
                return; // No need to continue if game is over
            } else {
                player.eat(enemy1);
                toRemove.add(enemy1); 
                enemyCount--; // 减少敌人数量
                if (enemyCount <= 0) {
                    gameOver = true; // 红球清零，玩家胜利
                    gameWon(); // 调用游戏胜利的处理方法
                    return;// Add to the list to remove
            }
        }
      }
    }
    enemies.removeAll(toRemove);
 }
 
  private void gameWon() {
   // 停止游戏循环
   if (timer != null) {
       timer.stop();
   }
   // 显示游戏胜利的消息
   JOptionPane.showMessageDialog(frame, "恭喜你！你赢得了游戏！", "游戏胜利", JOptionPane.INFORMATION_MESSAGE);  
  }
 
    // Create new enemy ball
    private void createNewEnemyBall(EnemyBall eater, EnemyBall eaten) {
        int halfSize = eaten.size / 2;
        // Ensure halfSize is at least 1 to avoid IllegalArgumentException in Random.nextInt
        halfSize = halfSize > 0 ? halfSize : 1;
        int newSize = halfSize + new Random().nextInt(halfSize);
        // ...
     }
   
    public static void main(String[] args) {
        JFrame frame = new JFrame("Ball Eater Game");
        frame.setSize(new Dimension(WIDTH, HEIGHT));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BallEaterGame game = new BallEaterGame(frame);
        frame.add(game);
        frame.setVisible(true);
    }
  }
   
  class PlayerBall {
    int x, y, size;
    private Image texture;
    public PlayerBall(int x, int y) {
        this.x = x;
        this.y = y;
        this.size = 40;
        this.texture = loadImage("src/xiaohui.png");
    }
    private Image loadImage(String path) {
        ImageIcon icon = new ImageIcon(path);
        return icon.getImage();
    }
  
    public void draw(Graphics g) {
    	   // 强制转换为 Graphics2D 以使用高级绘图方法
    	   Graphics2D g2d = (Graphics2D) g;
    	 
    	   // 检查贴图是否不为空
    	   if (texture != null) {
    	       // 获取原始图片的宽度和高度
    	       int imageWidth = texture.getWidth(null);
    	       int imageHeight = texture.getHeight(null);
    	 
    	       // 根据球的 size 变量缩放图片
    	       double scaleX = (double) size / imageWidth;
    	       double scaleY = (double) size / imageHeight;
    	 
    	       // 创建 AffineTransform 实例并进行缩放
    	       AffineTransform at = AffineTransform.getScaleInstance(scaleX, scaleY);
    	       
    	       // 计算图片绘制时的起始坐标（以球的中心为基准）
    	       int xStart = x - (int) (imageWidth * scaleX / 2);
    	       int yStart = y - (int) (imageHeight * scaleY / 2);
    	 
    	       // 绘制缩放后的图片
    	       g2d.drawImage(texture, xStart, yStart, (int) (imageWidth * scaleX), (int) (imageHeight * scaleY), null);
    	   }
    	}
    public void move(int dx, int dy) {
        x += dx * 5;
        y += dy * 5;
    }
   
    public void eat(EnemyBall enemy) {
        size += enemy.size / 2; // Increase player size when eating an enemy
    }
   
    public boolean collidesWith(EnemyBall enemy) {
        int dx = x - enemy.x;
        int dy = y - enemy.y;
        return dx * dx + dy * dy < (size / 2 + enemy.size / 2) * (size / 2 + enemy.size / 2);
    }
   
    public void draw(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillOval(x - size / 2, y - size / 2, size, size);
    }
  }
   
  class EnemyBall {
    int x, y, size;
    Random random;
   
    public EnemyBall(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.random = new Random();
    }
   
    public void move() {
        x += (random.nextInt(3) - 1) * 2; // -2, 0, 2
        y += (random.nextInt(3) - 1) * 2; // -2, 0, 2
        // Keep the enemy ball within the bounds
        x = Math.max(size / 2, Math.min(BallEaterGame.WIDTH - size / 2, x));
        y = Math.max(size / 2, Math.min(BallEaterGame.HEIGHT - size / 2, y));
    }
   
    public void eat(EnemyBall smallerEnemy) {
        size += smallerEnemy.size / 2; // Increase size when eating another enemy
    }
   
    public boolean collidesWith(EnemyBall other) {
        int dx = x - other.x;
        int dy = y - other.y;
        return dx * dx + dy * dy < (size / 2 + other.size / 2) * (size / 2 + other.size / 2);
    }
   
    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(x - size / 2, y - size / 2, size, size);
    }
  }