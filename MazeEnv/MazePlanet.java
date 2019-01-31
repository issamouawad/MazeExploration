package MazeEnv;

// Environment code for project jasonTeamSimLocal.mas2j

import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import jason.environment.grid.Location;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MazePlanet extends jason.environment.Environment {

    private Logger logger = Logger.getLogger("jasonTeamSimLocal.mas2j." + MazePlanet.class.getName());

    WorldModel  model;
    WorldView   view;

    int     nbWorlds = 5;
	int     simId    = 1;
    int     sleep    = 0;
    boolean running  = true;
    boolean hasGUI   = true;

    public static final int SIM_TIME = 60;  // in seconds

    Term                    up       = Literal.parseLiteral("do(up)");
    Term                    down     = Literal.parseLiteral("do(down)");
    Term                    right    = Literal.parseLiteral("do(right)");
    Term                    left     = Literal.parseLiteral("do(left)");
   

    public enum Move {
        UP, DOWN, RIGHT, LEFT
    };

    @Override
    public void init(String[] args) {
        hasGUI = args[2].equals("yes");
        sleep  = Integer.parseInt(args[1]);
        initWorld(Integer.parseInt(args[0]));
    }

    public int getSimId() {
        return simId;
    }

    public void setSleep(int s) {
        sleep = s;
    }

    @Override
    public void stop() {
        running = false;
        super.stop();
    }

    @Override
    public boolean executeAction(String ag, Structure action) {
        boolean result = false;
        try {
            if (sleep > 0) {
                Thread.sleep(sleep);
            }

            // get the agent id based on its name
            int agId = getAgIdBasedOnName(ag);

            if (action.equals(up)) {
                result = model.move(Move.UP, agId);
            } else if (action.equals(down)) {
                result = model.move(Move.DOWN, agId);
            } else if (action.equals(right)) {
                result = model.move(Move.RIGHT, agId);
            } else if (action.equals(left)) {
                result = model.move(Move.LEFT, agId);
          } else {
                logger.info("executing: " + action + ", but not implemented!");
            }
            if (result) {
                updateAgPercept(agId);
                return true;
            }
        } catch (InterruptedException e) {
        } catch (Exception e) {
            logger.log(Level.SEVERE, "error executing " + action + " for " + ag, e);
        }
        return false;
    }

    private int getAgIdBasedOnName(String agName) {
        return (Integer.parseInt(agName.substring(10))) - 1;
    }
	public void newWorld(int width,int height)
	{
		 simId = 0;
        try {
            model = WorldModel.newWorld(width,height);
            clearPercepts();
            
			addPercept(Literal.parseLiteral("final("+model.getFinalX()+","+model.getFinalY()+")"));
            if (hasGUI) {
                view = new WorldView(model);
                view.setEnv(this);
                
            }
            updateAgsPercept();
            informAgsEnvironmentChanged();
        } catch (Exception e) {
            logger.warning("Error creating world "+e);
        }
	}
    public void initWorld(int w) {
        simId = w;
        try {
			System.out.println("here");
            model = WorldModel.readWorldFromFile(w);
            clearPercepts();
            
			addPercept(Literal.parseLiteral("final("+model.getFinalX()+","+model.getFinalY()+")"));
            if (hasGUI) {
                view = new WorldView(model);
                view.setEnv(this);
                
            }
            updateAgsPercept();
            informAgsEnvironmentChanged();
        } catch (Exception e) {
            logger.warning("Error creating world "+e);
        }
    }

    public void endSimulation() {
        addPercept(Literal.parseLiteral("end_of_simulation(" + simId + ",0)"));
        informAgsEnvironmentChanged();
        if (view != null) view.setVisible(false);
        WorldModel.destroy();
    }

    private void updateAgsPercept() {
        for (int i = 0; i < model.getNbOfAgs(); i++) {
            updateAgPercept(i);
        }
    }

    private void updateAgPercept(int ag) {
        updateAgPercept("mazerunner" + (ag + 1), ag);
		addPercept("mazerunner"+(ag+1),Literal.parseLiteral("me("+ag+")"));
		addPercept("!moveavail");
    }

    private void updateAgPercept(String agName, int ag) {
        clearPercepts(agName);
        // its location
        Location l = model.getAgPos(ag);
        addPercept(agName, Literal.parseLiteral("pos(" + l.x + "," + l.y + ")"));

        
        // what's around
		if (l.y<model.getHeight()-1&&!model.hasObject(WorldModel.OBSTACLE, l.x, l.y+1))
			addPercept(agName, Literal.parseLiteral("avail(down)"));
		else 
			removePercept(agName, Literal.parseLiteral("avail(down)"));
		if (l.x<model.getWidth()-1&&!model.hasObject(WorldModel.OBSTACLE, l.x+1, l.y))
			addPercept(agName, Literal.parseLiteral("avail(right)"));
		else 
			removePercept(agName, Literal.parseLiteral("avail(right)"));
		if (l.y>0&&!model.hasObject(WorldModel.OBSTACLE, l.x, l.y-1))
			addPercept(agName, Literal.parseLiteral("avail(up)"));
		else 
			removePercept(agName, Literal.parseLiteral("avail(up)"));
		if (l.x>0&&!model.hasObject(WorldModel.OBSTACLE, l.x-1, l.y))
			addPercept(agName, Literal.parseLiteral("avail(left)"));
		else 
			removePercept(agName, Literal.parseLiteral("avail(left)"));
        updateAgPercept(agName, l.x - 1, l.y - 1);
        updateAgPercept(agName, l.x - 1, l.y);
        updateAgPercept(agName, l.x - 1, l.y + 1);
        updateAgPercept(agName, l.x, l.y - 1);
        updateAgPercept(agName, l.x, l.y);
        updateAgPercept(agName, l.x, l.y + 1);
        updateAgPercept(agName, l.x + 1, l.y - 1);
        updateAgPercept(agName, l.x + 1, l.y);
        updateAgPercept(agName, l.x + 1, l.y + 1);
    }


    private void updateAgPercept(String agName, int x, int y) {
        if (model == null || !model.inGrid(x,y)) return;
        if (model.hasObject(WorldModel.OBSTACLE, x, y)) {
            addPercept(agName, Literal.parseLiteral("cell(" + x + "," + y + ",obstacle)"));
        } 
    }

}
