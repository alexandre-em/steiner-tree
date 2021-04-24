package algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Kruskal {
    public Tree2D calculSteiner(ArrayList<Point> points) {
//        Tree2D myResult = approcheNaive(points);
//        Tree2D myResult = kruskal(points);
        Tree2D myResult = barycenter(points);
        return myResult;
    }

    public double distancePoints(Point x, Point y) {
        return y.distance(x);
    }

    public Tree2D approcheNaive(ArrayList<Point> points) {
        ArrayList<Point> result = (ArrayList<Point>) points.clone();
        int irand = 0;
        Point prand = result.remove(0);
        ArrayList<Tree2D> subTrees = new ArrayList<Tree2D>();
        Tree2D resultTree = new Tree2D(prand, subTrees);
        Point visite = prand;
        while (!result.isEmpty()) {
            int idist_min = 0;
            for (int i = 0; i < result.size(); i++) {
                if (distancePoints(visite, result.get(i))
                        < distancePoints(visite, result.get(idist_min))) {
                    idist_min = i;
                }
            }
            visite = result.remove(idist_min);
            subTrees = new ArrayList<Tree2D>();
            subTrees.add(resultTree);
            resultTree = new Tree2D(visite, subTrees);
        }
        return resultTree;
    }

    public Tree2D kruskal(ArrayList<Point> points) {
        //KRUSKAL ALGORITHM, NOT OPTIMAL FOR STEINER!
        ArrayList<Edge> edges = new ArrayList<Edge>();
        for (Point p : points) {
            for (Point q : points) {
                if (p.equals(q) || contains(edges, p, q)) continue;
                edges.add(new Edge(p, q));
            }
        }
        edges = sort(edges);

        ArrayList<Edge> kruskal = new ArrayList<Edge>();
        Edge current;
        NameTag forest = new NameTag(points);
        while (edges.size() != 0) {
            current = edges.remove(0);
            if (forest.tag(current.p) != forest.tag(current.q)) {
                kruskal.add(current);
                forest.reTag(forest.tag(current.p), forest.tag(current.q));
            }
        }

        return edgesToTree(kruskal, kruskal.get(0).p);
    }

    public ArrayList<Edge> kruskalEdge(ArrayList<Point> points) {
        //KRUSKAL ALGORITHM, NOT OPTIMAL FOR STEINER!
        ArrayList<Edge> edges = new ArrayList<Edge>();
        for (Point p : points) {
            for (Point q : points) {
                if (p.equals(q) || contains(edges, p, q)) continue;
                edges.add(new Edge(p, q));
            }
        }
        edges = sort(edges);

        ArrayList<Edge> kruskal = new ArrayList<Edge>();
        Edge current;
        NameTag forest = new NameTag(points);
        while (edges.size() != 0) {
            current = edges.remove(0);
            if (forest.tag(current.p) != forest.tag(current.q)) {
                kruskal.add(current);
                forest.reTag(forest.tag(current.p), forest.tag(current.q));
            }
        }

        return kruskal;
    }

    private boolean contains(ArrayList<Edge> edges, Point p, Point q) {
        for (Edge e : edges) {
            if (e.p.equals(p) && e.q.equals(q) ||
                    e.p.equals(q) && e.q.equals(p)) return true;
        }
        return false;
    }

    public Tree2D edgesToTree(ArrayList<Edge> edges, Point root) {
        ArrayList<Edge> remainder = new ArrayList<Edge>();
        ArrayList<Point> subTreeRoots = new ArrayList<Point>();
        Edge current;
        while (edges.size() != 0) {
            current = edges.remove(0);
            if (current.p.equals(root)) {
                subTreeRoots.add(current.q);
            } else {
                if (current.q.equals(root)) {
                    subTreeRoots.add(current.p);
                } else {
                    remainder.add(current);
                }
            }
        }

        ArrayList<Tree2D> subTrees = new ArrayList<Tree2D>();
        for (Point subTreeRoot : subTreeRoots)
            subTrees.add(edgesToTree((ArrayList<Edge>) remainder.clone(), subTreeRoot));

        return new Tree2D(root, subTrees);
    }

    private ArrayList<Edge> sort(ArrayList<Edge> edges) {
        if (edges.size() == 1) return edges;

        ArrayList<Edge> left = new ArrayList<Edge>();
        ArrayList<Edge> right = new ArrayList<Edge>();
        int n = edges.size();
        for (int i = 0; i < n / 2; i++) {
            left.add(edges.remove(0));
        }
        while (edges.size() != 0) {
            right.add(edges.remove(0));
        }
        left = sort(left);
        right = sort(right);

        ArrayList<Edge> result = new ArrayList<Edge>();
        while (left.size() != 0 || right.size() != 0) {
            if (left.size() == 0) {
                result.add(right.remove(0));
                continue;
            }
            if (right.size() == 0) {
                result.add(left.remove(0));
                continue;
            }
            if (left.get(0).distance() < right.get(0).distance()) result.add(left.remove(0));
            else result.add(right.remove(0));
        }
        return result;
    }

    public Tree2D barycenter(ArrayList<Point> points) {
        ArrayList<Point> rest = (ArrayList<Point>) points.clone();
        ArrayList<Triange> triangle = new ArrayList<>();
        Point a = rest.remove(0);


        while (!rest.isEmpty()) {
            Point b = null, c = null;
            for (int i = 0; i< rest.size(); i++) {
                Point p = rest.get(i);
                if (b == null || (distancePoints(b, a) > distancePoints(p, a)))
                    b = rest.get(i);
            }
            if (b != null) rest.remove(b);
            for (int i = 0; i< rest.size(); i++) {
                Point p = rest.get(i);
                if (c == null || (distancePoints(c, a) > distancePoints(p, a)))
                    c = rest.get(i);
            }
            if((b != null) && (c != null)) {
                triangle.add(new Triange(a, b, c));
                rest.remove(c);
            }
            if(rest.size() != 0) {
                a = rest.remove(0);
            }
        }
//        ArrayList<Edge> edges = new ArrayList<Edge>();
        Point root = triangle.get(0).a;
        Tree2D tree= new Tree2D(root, new ArrayList<Tree2D>());
        ArrayList<Tree2D> subTrees = new ArrayList<Tree2D>();
        while(!triangle.isEmpty()){
            Triange current = triangle.remove(0);
            if((current != null) && (current.betterScore())){
                Point I = current.baricenter();
                ArrayList<Tree2D> newL = new ArrayList<>();
                newL.add(new Tree2D(current.b, new ArrayList<Tree2D>()));
                newL.add(new Tree2D(current.c, new ArrayList<Tree2D>()));
                subTrees.add(new Tree2D(I, newL));
            } else {
                subTrees.add(new Tree2D(current.b, new ArrayList<Tree2D>()));
                subTrees.add(new Tree2D(current.c, new ArrayList<Tree2D>()));
            }
            tree = new Tree2D(current.a, subTrees);
            subTrees = new ArrayList<Tree2D>();
            subTrees.add(tree);
        }
        tree = new Tree2D(a, subTrees);
        return tree;
    }

}

class Triange {
    protected Point a, b, c;

    protected Triange(Point a, Point b, Point c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public Point baricenter() {
        int x1 = Math.min(a.x , Math.min(b.x, c.x)),
                x2 = Math.max(a.x, Math.max(b.x, c.x)),
                x3 = Math.min(a.y, Math.min(b.y, c.y)),
                x4 = Math.max(a.y, Math.max(b.y, c.y));
        Point pmin=null;
        for (int i = x1; i < x2; i++){
            for (int j = x3; j < x4; j++){
                Point current = new Point(i, j);
                if ( pmin == null){
                    pmin = current;
                    continue;
                }
                double dpmin = pmin.distance(a) + pmin.distance(b) + pmin.distance(c),
                        dcurr = current.distance(a) + current.distance(b) + current.distance(c);
                if ( dcurr < dpmin )
                    pmin = current;
            }
        }

        return pmin;
    }

    public boolean betterScore(){
        Point i = baricenter();
        return (a.distance(b)+ a.distance(c)) >= (a.distance(i) + b.distance(i) + c.distance(i));
    }

    public String toString(){
        return "["+a+","+b+","+c+","+baricenter()+"]";
    }
}

class Edge implements Comparable<Edge> {
    protected Point p, q;

    protected Edge(Point p, Point q) {
        this.p = p;
        this.q = q;
    }

    protected double distance() {
        return p.distance(q);
    }

    @Override
    public int compareTo(Edge edge) {
        double dist = edge.distance();
        return (int) (this.distance() - dist);
    }

    public String toString() {
        return p.toString() + q.toString() + ": " + distance() + "\n";
    }
}

class NameTag {
    private ArrayList<Point> points;
    private int[] tag;

    protected NameTag(ArrayList<Point> points) {
        this.points = (ArrayList<Point>) points.clone();
        tag = new int[points.size()];
        for (int i = 0; i < points.size(); i++) tag[i] = i;
    }

    protected void reTag(int j, int k) {
        for (int i = 0; i < tag.length; i++) if (tag[i] == j) tag[i] = k;
    }

    protected int tag(Point p) {
        for (int i = 0; i < points.size(); i++) if (p.equals(points.get(i))) return tag[i];
        return 0xBADC0DE;
    }
}