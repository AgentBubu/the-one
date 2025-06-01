package movement;

import core.Coord;
import core.Settings;

public class GridCrowdMovement extends MovementModel {

    private static final int PATH_LENGTH = 1;
    private Coord lastWaypoint;

    private int area;
    private int home;

    public GridCrowdMovement(Settings settings) {
        super(settings);
    }

    protected GridCrowdMovement(GridCrowdMovement rwp) {
        super(rwp);
    }

    @Override
    public Coord getInitialLocation() {
        assert rng != null : "MovementModel not initialized!";
        home = rng.nextInt(11) + 1; // Homes are 1 to 11
        area = home;
        lastWaypoint = getAreaCoord(home);
        return lastWaypoint.clone();
    }

    @Override
    public Path getPath() {
        Path p = new Path(generateSpeed());
        p.addWaypoint(lastWaypoint.clone());
        area = chooseArea();
        lastWaypoint = getAreaCoord(area);
        p.addWaypoint(lastWaypoint.clone());
        return p;
    }

    @Override
    public GridCrowdMovement replicate() {
        return new GridCrowdMovement(this);
    }

    private Coord getAreaCoord(int area) {
        int col = (area - 1) % 4;
        int row = (area - 1) / 4;
        double cellWidth = getMaxX() / 4.0;
        double cellHeight = getMaxY() / 3.0;
        double x = rng.nextDouble() * cellWidth + col * cellWidth;
        double y = rng.nextDouble() * cellHeight + (2 - row) * cellHeight; // 0 is top, 2 is bottom
        return new Coord(x, y);
    }

    private int chooseArea() {
        double prob = rng.nextDouble();

        if (area == home) {
            if (prob < 0.8) {
                return 12; // Gathering place
            } else {
                // Elsewhere: pick from other homes (excluding current home)
                int newArea;
                do {
                    newArea = rng.nextInt(11) + 1;
                } while (newArea == home);
                return newArea;
            }
        } else if (area == 12) {
            // From gathering place: treat like "Elsewhere"
            if (prob < 0.9) {
                return home;
            } else {
                int newArea;
                do {
                    newArea = rng.nextInt(11) + 1;
                } while (newArea == home);
                return newArea;
            }
        } else {
            // From other Elsewhere
            if (prob < 0.9) {
                return home;
            } else {
                int newArea;
                do {
                    newArea = rng.nextInt(11) + 1;
                } while (newArea == area || newArea == home);
                return newArea;
            }
        }
    }
}

