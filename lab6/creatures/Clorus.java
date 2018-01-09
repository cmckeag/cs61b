package creatures;
import huglife.Creature;
import huglife.Direction;
import huglife.Action;
import huglife.Occupant;
import huglife.HugLifeUtils;
import java.awt.Color;
import java.util.Map;
import java.util.List;

public class Clorus extends Creature {
    private int r = 34;
    private int g = 0;
    private int b = 231;
    private double moveProbability = 1;

    public Clorus(double e) {
        super("clorus");
        energy = e;
    }

    public Clorus() {
        this(1);
    }

    public Color color() {
        return color(r, g, b);
    }

    public void attack(Creature c) {
        this.energy += c.energy();
    }

    public void move() {
        this.energy -= 0.03;
    }

    public void stay() {
        this.energy -= 0.01;
    }

    public Clorus replicate() {
        Clorus q = new Clorus(this.energy / 2);
        this.energy = this.energy / 2;
        return q;
    }

    public Action chooseAction(Map<Direction, Occupant> neighbors) {
        List<Direction> emptySpots = getNeighborsOfType(neighbors, "empty");
        List<Direction> plipNeighbors = getNeighborsOfType(neighbors, "plip");
        if (emptySpots.size() == 0) {
            this.stay();
            return new Action(Action.ActionType.STAY);
        }
        if (plipNeighbors.size() > 0) {
            //pick a random direction, in which there is a plip
            Direction attackDir = HugLifeUtils.randomEntry(plipNeighbors);
            //need to access the plip in that location
            Creature target = (Creature) neighbors.get(attackDir);
            this.attack(target);
            return new Action(Action.ActionType.ATTACK, attackDir);
        }
        if (this.energy >= 1) {
            this.replicate();
            return new Action(Action.ActionType.REPLICATE, HugLifeUtils.randomEntry(emptySpots));
        }
        return new Action(Action.ActionType.MOVE, HugLifeUtils.randomEntry(emptySpots));
    }
}
