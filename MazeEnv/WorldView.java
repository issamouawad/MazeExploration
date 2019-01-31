package MazeEnv;

import jason.environment.grid.GridWorldView;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.BufferedWriter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Hashtable;
import java.io.FileWriter;       
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.io.*;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class WorldView extends GridWorldView {

    MazePlanet env = null;

    public WorldView(WorldModel model) {
        super(model, "MazeEnv World", 700);
        setVisible(true);
        repaint();
    }

    public void setEnv(MazePlanet env) {
        this.env = env;
        scenarios.setSelectedIndex(env.getSimId()-1);
    }

    JLabel    jlMouseLoc;
    JComboBox scenarios;
    JSlider   jSpeed;
	JTextField widthBox;
	JTextField heightBox;
 

    @Override
    public void initComponents(int width) {
        super.initComponents(width);
        scenarios = new JComboBox();
		FillCombo();
       
        JPanel args = new JPanel();
        args.setLayout(new BoxLayout(args, BoxLayout.Y_AXIS));

        JPanel sp = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sp.setBorder(BorderFactory.createEtchedBorder());
        sp.add(new JLabel("Maze:"));
        sp.add(scenarios);

        jSpeed = new JSlider();
        jSpeed.setMinimum(0);
        jSpeed.setMaximum(400);
        jSpeed.setValue(50);
        jSpeed.setPaintTicks(true);
        jSpeed.setPaintLabels(true);
        jSpeed.setMajorTickSpacing(100);
        jSpeed.setMinorTickSpacing(20);
        jSpeed.setInverted(true);
        Hashtable<Integer,Component> labelTable = new Hashtable<Integer,Component>();
        labelTable.put( 0, new JLabel("max") );
        labelTable.put( 200, new JLabel("speed") );
        labelTable.put( 400, new JLabel("min") );
        jSpeed.setLabelTable( labelTable );
        JPanel p = new JPanel(new FlowLayout());
        p.setBorder(BorderFactory.createEtchedBorder());
        p.add(jSpeed);

        args.add(sp);
        args.add(p);

        JPanel msg = new JPanel();
        msg.setLayout(new BoxLayout(msg, BoxLayout.Y_AXIS));
        msg.setBorder(BorderFactory.createEtchedBorder());

        p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.add(new JLabel("Click on a cell to toggle Obstacle/free"));
        msg.add(p);
		p = new JPanel(new FlowLayout(FlowLayout.CENTER));
		p.add(new JLabel("Create new world with dimentions:"));
		widthBox = new JTextField(2);
	
		heightBox = new JTextField(2);
		JButton newButton = new JButton("Create");
		p.add(new JLabel("width"));
		p.add(widthBox);
		p.add(new JLabel("height"));
		p.add(heightBox);
		p.add(newButton);
		newButton.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){
				 env.endSimulation();
                    env.newWorld(Integer.parseInt(widthBox.getText()),Integer.parseInt(heightBox.getText()));
				//env.newWorld(30,30);	
		}});
		p.add(newButton);
		msg.add(p);
        p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.add(new JLabel("(mouse at:"));
        jlMouseLoc = new JLabel("0,0)");
        p.add(jlMouseLoc);
        msg.add(p);
		
        p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
       
      
        msg.add(p);
		JButton b = new JButton("Save");
		b.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){
				if(env.getSimId()==0)
				{
					
					BufferedWriter br =null;
					int current = textFilesCount()+1;
				try{ br= new BufferedWriter(new FileWriter("MazeEnv/"+(current)+".txt"));
					WorldModel wm = (WorldModel)model;
					for(int i=0;i<wm.getHeight();i++)
						for(int j=0;j<wm.getWidth();j++)
							br.write(i+","+j+","+ (wm.isFree(WorldModel.OBSTACLE,j, i)?"0":"1")+"\n");
					br.close();
					FillCombo();
					env.endSimulation();
					selectWorld(current);
                    //env.initWorld(current);
				}
				catch(Exception exp){}
				}
				else{
				BufferedWriter br =null;
				try{ br= new BufferedWriter(new FileWriter("MazeEnv/"+env.getSimId()+".txt"));
					WorldModel wm = (WorldModel)model;
					br.write(wm.getNbOfAgs()+","+wm.getFinalY()+","+wm.getFinalX()+"\n");
					for(int i=0;i<wm.getNbOfAgs();i++)
					{
						br.write(wm.getAgPos(i).y+","+wm.getAgPos(i).x+"\n");
					}
					for(int i=0;i<wm.getHeight();i++)
						for(int j=0;j<wm.getWidth();j++)
							br.write(i+","+j+","+ (wm.isFree(WorldModel.OBSTACLE,j, i)?"0":"1")+"\n");
					br.close();
					int sm =env.getSimId();
					env.endSimulation();
					env.initWorld(sm);
				}
				catch(Exception exp){}
				finally{}
				}
		}});
		p.add(b);
        JPanel s = new JPanel(new BorderLayout());
        s.add(BorderLayout.WEST, args);
        s.add(BorderLayout.CENTER, msg);
        getContentPane().add(BorderLayout.SOUTH, s);

        // Events handling
        jSpeed.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (env != null) {
                    env.setSleep((int)jSpeed.getValue());
                }
            }
        });

        scenarios.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent ievt) {
				//System.out.println("printing w");
				if(scenarios.getSelectedItem()==null)
					return;
                int w = ((Integer)scenarios.getSelectedItem()).intValue();
				//System.out.println("w is" + w);
				
                if (env != null && env.getSimId() != w) {
                    env.endSimulation();
                    env.initWorld(w);
                }
            }
        });

        getCanvas().addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                int col = e.getX() / cellSizeW;
                int lin = e.getY() / cellSizeH;
                if (col >= 0 && lin >= 0 && col < getModel().getWidth() && lin < getModel().getHeight()) {
                    WorldModel wm = (WorldModel)model;
					if(wm.isFree(WorldModel.OBSTACLE, col, lin))
						wm.add(WorldModel.OBSTACLE, col, lin);
					else
						wm.remove(WorldModel.OBSTACLE, col, lin);
                    
                    update(col, lin);
                    
                }
            }
            public void mouseExited(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}
        });

        getCanvas().addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent e) { }
            public void mouseMoved(MouseEvent e) {
                int col = e.getX() / cellSizeW;
                int lin = e.getY() / cellSizeH;
                if (col >= 0 && lin >= 0 && col < getModel().getWidth() && lin < getModel().getHeight()) {
                    jlMouseLoc.setText(col+","+lin+")");
                }
            }
        });
    }



 

    @Override
    public void drawAgent(Graphics g, int x, int y, Color c, int id) {
        Color idColor = Color.black;
        
            super.drawAgent(g, x, y, c, -1);
            idColor = Color.white;
     
        g.setColor(idColor);
        drawString(g, x, y, defaultFont, String.valueOf(id+1));
    }
@Override
    public void draw(Graphics g, int x, int y, int object) {
        switch (object) {case 16:
            drawGold(g, x, y);
            break;      }
    }
 public void drawGold(Graphics g, int x, int y) {
        g.setColor(Color.yellow);
        g.drawRect(x * cellSizeW + 2, y * cellSizeH + 2, cellSizeW - 4, cellSizeH - 4);
        int[] vx = new int[4];
        int[] vy = new int[4];
        vx[0] = x * cellSizeW + (cellSizeW / 2);
        vy[0] = y * cellSizeH;
        vx[1] = (x + 1) * cellSizeW;
        vy[1] = y * cellSizeH + (cellSizeH / 2);
        vx[2] = x * cellSizeW + (cellSizeW / 2);
        vy[2] = (y + 1) * cellSizeH;
        vx[3] = x * cellSizeW;
        vy[3] = y * cellSizeH + (cellSizeH / 2);
        g.fillPolygon(vx, vy, 4);
    }
    public static void main(String[] args) throws Exception {
        MazePlanet env = new MazePlanet();
        env.init(new String[] {"1","34","yes"});
    }
	public void FillCombo()
	{
		int count = textFilesCount();
		scenarios.removeAllItems();
		 for (int i=1; i<=count; i++) {
            scenarios.addItem(i);
        }
	}
	public void selectWorld(int k)
	{
		
		
		 for (int i=0; i<scenarios.getItemCount(); i++) {
            if(k==((Integer)scenarios.getItemAt(i)).intValue())
			{scenarios.setSelectedItem(scenarios.getItemAt(i));
			break;}
			 
        }
	}
	public static int textFilesCount() {

  int count=0;
  File dir = new File("MazeEnv/");
  for (File file : dir.listFiles()) {
    if (file.getName().endsWith((".txt"))) {
      count++;
    }
  }
  return count;
}
}
