package mn.von.gestalt.utility.grimoire;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;

public class PhysicsUtils {

    public static void simulateB2T() {

        World world = new World();
        world.setGravity(Vector2.create(0,0));

        Body wall1 = new Body();
        wall1.addFixture(Geometry.createRectangle(1,2000));
        wall1.setGravityScale(0);
        wall1.translate(0,0);

        Body wall2 = new Body();
        wall2.addFixture(Geometry.createRectangle(2000,1));
        wall2.setGravityScale(0);
        wall2.translate(1,0);

        Body wall3 = new Body();
        wall3.addFixture(Geometry.createRectangle(1,2000));
        wall3.setGravityScale(0);
        wall3.translate(1001,0);


    }

}
