package MazeEnv;

import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Location;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
//import System.out;

import MazeEnv.MazePlanet.Move;

public class WorldModel extends GridWorldModel {

    
   
  
	private  static int finalX=0;
	private  static int finalY =0;
   public static final int   GOLD  = 16;
    

    private Logger            logger   = Logger.getLogger("jasonTeamSimLocal.mas2j." + WorldModel.class.getName());

    private String            id = "WorldModel";

    // singleton pattern
    protected static WorldModel model = null;
	public int getFinalY()
	{
	return finalY;}
	public int getFinalX()
	{
		return finalX;
	}
    synchronized public static WorldModel create(int w, int h, int nbAgs) {
        if (model == null) {
            model = new WorldModel(w, h, nbAgs);
        }
        return model;
    }

    public static WorldModel get() {
        return model;
    }

    public static void destroy() {
        model = null;
    }

    private WorldModel(int w, int h, int nbAgs) {
        super(w, h, nbAgs);
        
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String toString() {
        return id;
    }

    /** Actions **/
	Move[] getPossibleMoves(int ag) throws Exception{
		Location l = getAgPos(ag);
		ArrayList<Move> moves = new ArrayList<Move>();
		if (isFree(l.x, l.y - 1))
			moves.add(Move.UP);
		 if (isFree(l.x, l.y + 1))
			 moves.add(Move.DOWN);
		 if (isFree(l.x + 1, l.y))
			moves.add(Move.RIGHT);
		 if (isFree(l.x - 1, l.y))
			 moves.add(Move.LEFT);
		 Move[] movesArr = new Move[moves.size()];
		 
		 return moves.toArray(movesArr);
	}
    boolean move(Move dir, int ag) throws Exception {
        Location l = getAgPos(ag);
        switch (dir) {
        case UP:
            if (isFree(WorldModel.OBSTACLE,l.x, l.y - 1)) {
                setAgPos(ag, l.x, l.y - 1);
            }
            break;
        case DOWN:
            if (isFree(WorldModel.OBSTACLE,l.x, l.y + 1)) {
                setAgPos(ag, l.x, l.y + 1);
            }
            break;
        case RIGHT:
            if (isFree(WorldModel.OBSTACLE,l.x + 1, l.y)) {
                setAgPos(ag, l.x + 1, l.y);
            }
            break;
        case LEFT:
            if (isFree(WorldModel.OBSTACLE,l.x - 1, l.y)) {
                setAgPos(ag, l.x - 1, l.y);
            }
            break;
        }
        return true;
    }

    /** no gold/no obstacle world */
    static WorldModel readWorldFromFile(int f) throws Exception {
		BufferedReader br;                                
	
		br= new BufferedReader(new FileReader("MazeEnv/"+(f)+".txt"));
		List<Integer> xCells = new ArrayList<Integer>();
		List<Integer> yCells = new ArrayList<Integer>();
		List<Integer> values = new ArrayList<Integer>();
		List<Integer> xAgent= new ArrayList<Integer>();
		List<Integer> yAgent = new ArrayList<Integer>();
		int agentsCount =0;
		int finalRow =0;
		int finalCol = 0;
		String line = br.readLine();
		
		String[] parts = line.split(",");
		agentsCount =Integer.parseInt(parts[0]);
		finalY = Integer.parseInt(parts[2]);
		finalX = Integer.parseInt(parts[1]);
		
		int i =0;
		System.out.println("number of agents is "+agentsCount);
		while(i<agentsCount)
		{
			line = br.readLine();
			parts = line.split(",");
			
			yAgent.add(Integer.parseInt(parts[0]));
			xAgent.add(Integer.parseInt(parts[1]));
			i++;
		}
		
		line = br.readLine();
		
		while (line != null) {
        parts = line.split(",");
		
		xCells.add(Integer.parseInt(parts[1]));
		yCells.add(Integer.parseInt(parts[0]));
		values.add(Integer.parseInt(parts[2]));
		
		line = br.readLine();
    }
		
        WorldModel model = WorldModel.create(Collections.max(xCells)+1,Collections.max(yCells)+1, agentsCount);
		model.add(16,finalX,finalY);
		System.out.println("model created with "+agentsCount + " agents");
       model.setId("Scenario 4");
		for( i=0;i<xCells.size();i++)
			if(values.get(i)==1)
				model.add(WorldModel.OBSTACLE,xCells.get(i),yCells.get(i));
		for(i=0;i<agentsCount;i++)
			model.setAgPos(i,xAgent.get(i),yAgent.get(i));
		//model.setAgPos(2, 5, 1);
		
       
        return model;
    }

    static WorldModel newWorld(int width, int height) throws Exception{
		WorldModel model = WorldModel.create(width, height, 4);
        model.setId("Simulation 1");
       
        //model.setAgPos(0, 1, 0);
        for(int i=0;i<height;i+=2)
			for(int j=0;j<width;j+=2)
		{
				model.add(WorldModel.OBSTACLE,j,i);
				model.add(WorldModel.OBSTACLE,j+1,i);
				model.add(WorldModel.OBSTACLE,j,i+1);
		}
        System.out.println("problem here");
        
        return model;
	}

}
