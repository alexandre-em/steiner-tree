package algorithms;

import java.awt.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class SansBuget {
    /**
     * Calcul le plus court chemin entre chaque points dans `G`
     * complexite dans le pire des cas en O(points.size()^3), avec les 3 dernieres boucles
     */
    public int[][] calculShortestPaths(ArrayList<Point> points, int edgeThreshold) {
        int[][] paths = new int[points.size()][points.size()];
        for (int i = 0; i < paths.length; i++) for (int j = 0; j < paths.length; j++) paths[i][j] = i;

        double[][] dist = new double[points.size()][points.size()];

        // calcul des distances des points `pi`, `pj` et initialisation de path au point `pi`,`pj`
        for (int i = 0; i < paths.length; i++) {
            for (int j = 0; j < paths.length; j++) {
                if (i == j) {
                    dist[i][i] = 0;
                    continue;
                }
                if (points.get(i).distance(points.get(j)) <= (double) edgeThreshold)
                    dist[i][j] = points.get(i).distance(points.get(j));
                else dist[i][j] = Double.POSITIVE_INFINITY;
                paths[i][j] = j;
            }
        }

        // mise a jour de `dist` et `path` par le plus court chemin
        for (int k = 0; k < paths.length; k++) {
            for (int i = 0; i < paths.length; i++) {
                for (int j = 0; j < paths.length; j++) {
                    if (dist[i][j] > dist[i][k] + dist[k][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                        paths[i][j] = paths[i][k];
                    }
                }
            }
        }

        return paths;
    }

    /**
     * Cette fonction permet d'ajouter les arretes intermediaire entre deux points au graphe `K`
     * Complexite de l'ordre de O(n), car on boucle sur une liste de points appartenant a G
     */
    public Tree2D addPath(Tree2D K, Tree2D Q, int[][] paths, ArrayList<Point> points) {
        int current = points.indexOf(Q.getRoot());
        // Si le plus court chemin dans `G` est le point de Q alors le programme quitte la fonction.
        if (points.indexOf(K.getRoot()) == paths[current][points.indexOf(K.getRoot())]) return K;

        Tree2D result = new Tree2D(Q.getRoot(), Q.getSubTrees());
        ArrayList<Tree2D> sub = new ArrayList<>();
        // Parcours du chemin le plus court dans `G` grace a `path` pour ensuite ajouter les points intermediaires dans l'arbre `K`
        while (points.indexOf(K.getRoot()) != paths[current][points.indexOf(K.getRoot())]) {
            sub = new ArrayList<>();
            sub.add(result);
            result = new Tree2D(points.get(paths[current][points.indexOf(K.getRoot())]), sub);
            current = paths[current][points.indexOf(K.getRoot())];
        }
        Tree2D finalResult = result;
        // Reconstruction de `K` avec les points intermediaires stockes dans `result` et suppression de son enfant direct: `Q`
        return new Tree2D(K.getRoot(), (ArrayList<Tree2D>) K.getSubTrees().stream().map(val -> {
            if (val.equals(Q))
                return finalResult;
            return val;
        }).collect(Collectors.toList()));
    }

    /**
     * Permet de reconstruire le Graphe `K` pour que chacune de ses arretes appartiennent a `G`
     * Complexite en O(k*m*n) avec:
     * k: le nombre de points dans `K`
     * m: le nombre maximum d'enfant d'un noeud dans `K`
     * n: le nombre de points dans `V`
     */
    public Tree2D getGraph(Tree2D K, int[][] paths, ArrayList<Point> points) {
        Tree2D result = new Tree2D((Point) K.getRoot().clone(), (ArrayList<Tree2D>) K.getSubTrees().clone());
        ArrayList<Tree2D> subTree = new ArrayList<>();
        // Lorsque l'on tombe sur une feuille de l'arbre, la reccursion s'arrete
        if (K.getSubTrees().isEmpty()) {
            return K;
        }

        // Remplacement des arretes `(K, sub)` par des plus court chemins de `(K, sub)` dans `G`
        for (Tree2D sub : result.getSubTrees()) {
            result = addPath(result, sub, paths, points);
        }

        // On parcours la liste des arbres enfants `sub` de `K` courant, et on appelle recursivement la fonction sur ceux ci
        // et remplacer leurs chemin egalement, pour ensuite reconstruire l'arbre a partir de `K`
        for (Tree2D sub : result.getSubTrees()) {
            result = getGraph(sub, paths, points);
            subTree.add(result);
        }
        result = new Tree2D((Point) K.getRoot().clone(), subTree); // nouveau K avec ses nouvelles arretes
        return result;
    }

    public Tree2D calculSteiner(ArrayList<Point> points, int edgeThreshold, ArrayList<Point> hitPoints) {
        int[][] paths = calculShortestPaths(points, edgeThreshold); // calcul des plus court chemin de chaque points dans `G`, de l'ordre de O(n^3)
        ArrayList<Point> rem = (ArrayList<Point>) points.clone();
        Tree2D K = new Kruskal().kruskal(hitPoints); // calcul du graphe `K` qui passe par tous les points de `S` avec l'algorithme de Kruskal , en O(n^2)
        return getGraph(K, paths, rem); // transformation du graph `K` pour que chacune de ses arretes appartiennent a `G`, en O(n^)
    }
}