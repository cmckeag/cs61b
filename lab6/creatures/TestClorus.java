package creatures;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.HashMap;
import java.awt.Color;
import huglife.Direction;
import huglife.Action;
import huglife.Occupant;
import huglife.Impassible;
import huglife.Empty;

/** Tests the plip class   
 *  @authr FIXME
 */

public class TestClorus {

    /* Replace with the magic word given in lab.
     * If you are submitting early, just put in "early" */
    public static final String MAGIC_WORD = "early";

    @Test
    public void testBasics() {
        Clorus p = new Clorus(2);
        assertEquals(2, p.energy(), 0.01);
        assertEquals(new Color(34, 0, 231), p.color());
        p.move();
        assertEquals(1.97, p.energy(), 0.01);
        p.move();
        assertEquals(1.94, p.energy(), 0.01);
        p.stay();
        assertEquals(1.93, p.energy(), 0.01);
        p.stay();
        assertEquals(1.92, p.energy(), 0.01);
    }

    @Test
    public void testReplicate() {
        Clorus p = new Clorus(2);
        Clorus q = p.replicate();
        assertNotSame(p, q);
        assertEquals(1.0, p.energy(), q.energy());
    }

    @Test
    public void testAttack() {
        Clorus p = new Clorus(0.5);
        HashMap<Direction, Occupant> area = new HashMap<Direction, Occupant>();
        area.put(Direction.TOP, new Plip(1));
        area.put(Direction.BOTTOM, new Empty());
        area.put(Direction.LEFT, new Empty());
        area.put(Direction.RIGHT, new Empty());

        Action did = p.chooseAction(area);
        Action should = new Action(Action.ActionType.ATTACK, Direction.TOP);
        assertEquals(should, did);
        assertEquals(1.5, p.energy(), 0.01);
    }

    @Test
    public void testChoose() {
        Clorus p = new Clorus(1.2);
        HashMap<Direction, Occupant> surrounded = new HashMap<Direction, Occupant>();
        surrounded.put(Direction.TOP, new Impassible());
        surrounded.put(Direction.BOTTOM, new Impassible());
        surrounded.put(Direction.LEFT, new Impassible());
        surrounded.put(Direction.RIGHT, new Impassible());

        //You can create new empties with new Empty();
        //Despite what the spec says, you cannot test for Cloruses nearby yet.
        //Sorry!  

        Action actual = p.chooseAction(surrounded);
        Action expected = new Action(Action.ActionType.STAY);
        assertEquals(expected, actual);
        assertEquals(1.19, p.energy(), 0.01);

        HashMap<Direction, Occupant> plipTarget = new HashMap<Direction, Occupant>();
        plipTarget.put(Direction.TOP, new Plip(1));
        plipTarget.put(Direction.LEFT, new Empty());
        plipTarget.put(Direction.RIGHT, new Empty());
        plipTarget.put(Direction.BOTTOM, new Empty());

        Action did = p.chooseAction(plipTarget);
        Action should = new Action(Action.ActionType.ATTACK, Direction.TOP);
        assertEquals(should, did);

        HashMap<Direction, Occupant> noPlips = new HashMap<Direction, Occupant>();
        noPlips.put(Direction.TOP, new Empty());
        noPlips.put(Direction.LEFT, new Impassible());
        noPlips.put(Direction.RIGHT, new Impassible());
        noPlips.put(Direction.BOTTOM, new Impassible());

        Action did1 = p.chooseAction(noPlips);
        Action should1 = new Action(Action.ActionType.REPLICATE, Direction.TOP);
        assertEquals(should1, did1);

        Clorus q = new Clorus(0.5);

        Action did2 = q.chooseAction(noPlips);
        Action should2 = new Action(Action.ActionType.MOVE, Direction.TOP);
        assertEquals(should2, did2);
    }

    public static void main(String[] args) {
        System.exit(jh61b.junit.textui.runClasses(TestClorus.class));
    }
} 
