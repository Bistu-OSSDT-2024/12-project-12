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
   private int score;
   private String gameMode;
  
   
 
   public BallEaterGame(JFrame frame,String gameMode) {
	   score = 0;
       this.frame = frame;
       this.gameMode = gameMode;
       setFocusable(true);
       keys = new boolean[4];
       player = new PlayerBall(WIDTH / 2, HEIGHT / 2);
       enemies = new ArrayList<>();
       for (int i = 0; i < 100; i++) { // ���Ӻ���������50
           // ȷ�������СС������Ĵ�С��20��
           int enemySize = new Random().nextInt(15) + 5; // �����С��5��20֮��
           enemies.add(new EnemyBall(new Random().nextInt(WIDTH), new Random().nextInt(HEIGHT), enemySize));
       }
       gameOver = false;
 
       timer = new Timer(20, this);
       timer.start();
 
       addKeyListener(this);
       enemyCount = enemies.size();
       
   }
   private void drawScore(Graphics g) {
	   Graphics2D g2d = (Graphics2D) g;
	   g2d.setFont(new Font("Helvetica", Font.BOLD, 24)); // ��������
	   g2d.setColor(Color.BLACK);
	   String scoreText = "���ſ���Ե���: " + score+"����";
	   // ���Ƶ÷�����Ļ�����Ͻ�
	   g2d.drawString(scoreText, 10, 30); 
	}
   private void incrementScore(int points) {
	   score += points;
	   repaint(); 
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
           int result = JOptionPane.showConfirmDialog(frame, "��Ϸ������\n�Ƿ����¿�ʼ��Ϸ��", "��Ϸ����", JOptionPane.YES_NO_OPTION);
           if (result == JOptionPane.YES_OPTION) {
               resetGame();
           }
       }
 
       repaint();
   }
  
 
   @Override
   public void paintComponent(Graphics g) {
       super.paintComponent(g);
       if (!gameOver) {
           player.draw(g);
           for (EnemyBall enemy : enemies) {
               enemy.draw(g);
           }
           drawScore(g); // ���Ƶ÷�
       }
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
    score = 0;
    for (int i = 0; i < 50; i++) {
    	 int enemySize = new Random().nextInt(15) + 5; // �����С��5��20֮��
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
                if (gameMode.equals("�޾�ģʽ")) {
                    createNewEnemyBall(); // ���޾�ģʽ�д����µĺ���
                }
                enemyCount--; // ���ٵ�������
                incrementScore(1); 
                if (enemyCount <= 0) {
                    gameOver = true; // �������㣬���ʤ��
                    gameWon(); // ������Ϸʤ���Ĵ�����
                    return;// Add to the list to remove
            }
        }
      }
    }
    enemies.removeAll(toRemove);
 }
   private void createNewEnemyBall() {
       // ȷ�������ɵĺ����СС������Ĵ�С
       int maxSize = 40; // ������������С������С 5
       int newSize = new Random().nextInt(maxSize) + 5; // �º���Ĵ�С�� 5 �� maxSize ֮��
       int x = new Random().nextInt(WIDTH);
       int y = new Random().nextInt(HEIGHT);
       enemies.add(new EnemyBall(x, y, newSize));
   }
 
  private void gameWon() {
   // ֹͣ��Ϸѭ��
   if (timer != null) {
       timer.stop();
   }
   // ��ʾ��Ϸʤ������Ϣ
   JOptionPane.showMessageDialog(frame, "��ϲ�㣡��Ӯ������Ϸ��", "��Ϸʤ��", JOptionPane.INFORMATION_MESSAGE);  
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
    	   String gameMode = GameModeChooser.chooseMode(frame);
    	   if (gameMode != null) {
    	       BallEaterGame game = new BallEaterGame(frame, gameMode);
    	       frame.add(game);
    	       frame.setVisible(true);
    	   } else {
    	       System.exit(0); // ����û�ȡ�������˳���Ϸ
    	   }
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
    	   // ǿ��ת��Ϊ Graphics2D ��ʹ�ø߼���ͼ����
    	   Graphics2D g2d = (Graphics2D) g;
    	 
    	   // �����ͼ�Ƿ�Ϊ��
    	   if (texture != null) {
    	       // ��ȡԭʼͼƬ�Ŀ�Ⱥ͸߶�
    	       int imageWidth = texture.getWidth(null);
    	       int imageHeight = texture.getHeight(null);
    	 
    	       // ������� size ��������ͼƬ
    	       double scaleX = (double) size / imageWidth;
    	       double scaleY = (double) size / imageHeight;
    	 
    	       // ���� AffineTransform ʵ������������
    	       AffineTransform at = AffineTransform.getScaleInstance(scaleX, scaleY);
    	       
    	       // ����ͼƬ����ʱ����ʼ���꣨���������Ϊ��׼��
    	       int xStart = x - (int) (imageWidth * scaleX / 2);
    	       int yStart = y - (int) (imageHeight * scaleY / 2);
    	 
    	       // �������ź��ͼƬ
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
  class GameModeChooser {
	   public static String chooseMode(JFrame frame) {
	       Object[] options = {"����ģʽ", "�޾�ģʽ"};
	       int response = JOptionPane.showOptionDialog(frame,
	               "��ѡ����Ϸģʽ",
	               "��Ϸģʽѡ��",
	               JOptionPane.DEFAULT_OPTION,
	               JOptionPane.PLAIN_MESSAGE,
	               null,
	               options,
	               options[0]);
	       if (response == 0) {
	           return "����ģʽ";
	       } else if (response == 1) {
	           return "�޾�ģʽ";
	       } else {
	           return null; // ����û�ȡ�����򷵻� null
	       }
	   }
	}