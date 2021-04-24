package algorithms;

import java.awt.Point;
import java.util.ArrayList;

public class DefaultTeam {
    public Tree2D calculSteiner(ArrayList<Point> points, int edgeThreshold, ArrayList<Point> hitPoints){
        return new SansBuget().calculSteiner(points, edgeThreshold, hitPoints);
    }

    public Tree2D calculSteinerBudget(ArrayList<Point> points, int edgeThreshold, ArrayList<Point> hitPoints) {
        return new AvecBudget().calculSteinerBudget(points, edgeThreshold, hitPoints);
    }
}
