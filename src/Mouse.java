import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;
import java.util.Scanner;

public class Mouse 
{
    static JFrame frame;    
    TerrainBase tb;
    private BufferedImage bImage;
    private ImageIcon image;
    private JLabel imageLabel;
    private MouseAdapter mouseListener = new MouseAdapter() 
    {
        @Override
        public void mousePressed(MouseEvent me) 
        {
//            try
//            {
//                tb.RefreshTerrain(me.getX(), me.getY(), me.isShiftDown(), me.isAltDown(), me.isControlDown());
//            }
//            catch (IOException e)
//            {
//                e.printStackTrace();
//            }
//
//            System.out.println("mouse dragged through " + me.getX() + " " + me.getY());
//
//            File file = new File("src/terrain.png");
//
//            try {
//                File file2 = new File("src/terrain.png");
//
//                bImage = ImageIO.read(file2);
//                image = new ImageIcon(bImage);
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//            image = new ImageIcon(bImage);
//
//            imageLabel.setIcon(image);
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {


            frame.getContentPane().validate();
            frame.getContentPane().repaint();
        }

        @Override
        public void mouseMoved(MouseEvent me) {
        }

        @Override
        public void mouseDragged(MouseEvent me) 
        {
            try
            {
                tb.RefreshTerrain(me.getX(), me.getY(), me.isShiftDown(), me.isAltDown(), me.isControlDown());
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

//            System.out.println("mouse dragged through " + me.getX() + " " + me.getY());

            File file = new File("src/terrain.png");

            try {
                File file2 = new File("src/terrain.png");

                bImage = ImageIO.read(file2);
                image = new ImageIcon(bImage);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            image = new ImageIcon(bImage);

            imageLabel.setIcon(image);
            
        }
    };

    public Mouse() throws IOException 
    {
    	tb = new TerrainBase();
    	
        try 
        {
        	File file = new File("src/terrain.png");
        	
            bImage = ImageIO.read(file);
            image = new ImageIcon(bImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayGUI() 
    {
        frame = new JFrame("Painting on Image");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel contentPane = new JPanel();
        imageLabel = new JLabel(image);
        imageLabel.addMouseListener(mouseListener);
        imageLabel.addMouseMotionListener(mouseListener);
//        imageLabel.addKeyListener(keyListener);

        contentPane.add(imageLabel);

        frame.setContentPane(contentPane);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);

        //frame.setFocusable(true);
        frame.addKeyListener(new java.awt.event.KeyAdapter() {


            @Override
            public void keyPressed(KeyEvent e) {
                try {
                    tb.RefreshTerrain(e);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                File file = new File("src/terrain.png");

                try {
                    File file2 = new File("src/terrain.png");

                    bImage = ImageIO.read(file2);
                    image = new ImageIcon(bImage);
                } catch (IOException ex) {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }
                image = new ImageIcon(bImage);

                imageLabel.setIcon(image);
            }
        });
    }



    public static void main(String... args) 
    {
        SwingUtilities.invokeLater(new Runnable() 
        {
            public void run() 
            {
                try {
					new Mouse().displayGUI();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
                
            }
        });


    }
}