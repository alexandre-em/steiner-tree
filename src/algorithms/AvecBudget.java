package algorithms;

import com.sun.source.tree.Tree;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class AvecBudget {
    protected final int BUDGET = 1664;

    // Solution de l'ordre de O(n^3)
    public Tree2D calculSteinerBudget(ArrayList<Point> points, int edgeThreshold, ArrayList<Point> hitPoints) {
        int[][] paths = new SansBuget().calculShortestPaths(points, edgeThreshold); // calcul des plus court chemin de chaque points dans `G`, de l'ordre de O(n^3)
        double dist = .0; // sert a mesurer la distance de toutes les arretes
        ArrayList<Point> clone = (ArrayList<Point>) points.clone();
        // edges correspond au arretes du graphe de `hitpoints` calculer par l'algo de Kruskal en O(n^2)
        // `edgesBudget` correspond au sous graphe de `edges` dont la distance total de ses arretes soit < `BUDGET`
        ArrayList<Edge> edges = new Kruskal().kruskalEdge(hitPoints), edgesBudget = new ArrayList<>();
        // `pointsBudget`, permet de voir les points que l'on a ajouter au nouveau graphe "budget"
        Set<Point> pointsBudget = new HashSet<>();
        pointsBudget.add(hitPoints.get(0));
        outerloop: // tag permettant de break 2 loops simultanement
        {
            // Boucle qui parcourt la liste d'arrete du graphe de Kruskal de hitpoint
            // on commence par du point maison et on regarde ses voisins
            // puis on repete la procedure avec ses voisins jusqu'a ce que `dist` >= `BUDGET` ou que tout les points aient valide
            // complexite total dans le pire des cas de la boucle est de l'ordre de O(n^3) avec `n` le nombre de points dans `hitpoints`
            while (dist < this.BUDGET && pointsBudget.size() < hitPoints.size()) {
                for (Edge edge : edges) { // complexite de l'ordre de O(n^2)
                    // si `p` et `q` sont dans `pointsBudget` cela veut dire que l'arrete [p,q] a deja ete ajoute
                    if (pointsBudget.contains(edge.p) && pointsBudget.contains(edge.q)) continue;
                    // si `p` ou `q` est dans `pointsBudget`, donc `p` est voisin de `q`, qui est dans le nouveau graphe ou inversement
                    // alors on ajoute l'arrete [p,q] (ou [q,p]) et on met a jour la distance
                    if ((pointsBudget.contains(edge.p) || pointsBudget.contains(edge.q)) && (dist + edge.distance() < this.BUDGET)) {
                        edgesBudget.add(edge);
                        dist += edge.distance();
                    }
                    // lorsque le budget est depasse, le programme n'ajoute pas l'arrete et il stop les deux loops.
                    if (dist + edge.distance() > this.BUDGET) {
                        break outerloop;
                    }
                }
                // Ajout des points des arretes que l'on vient d'ajouter,
                // boucle a part pour ne pas fausser la condition de la premiere boucle
                for (Edge edge : edgesBudget) {
                    pointsBudget.add(edge.p);
                    pointsBudget.add(edge.q);
                }
            }
        }
        Tree2D kruskal = new Kruskal().edgesToTree(edgesBudget, edgesBudget.get(0).p); // transformation de la liste d'arretes en graphe
        return new SansBuget().getGraph(kruskal, paths, clone); // transformation du graph `K` pour que chacune de ses arretes appartiennent a `G`, en O(n^)
    }
}
