package org.example;

/** Representa un nodo del árbol binario de búsqueda con un valor entero y referencias a sus hijos. */
public class Node {
    public int  value;
    public Node left;
    public Node right;

    /** Campo auxiliar usado por TreePanel para calcular la posición horizontal del nodo al dibujarlo. */
    public int x;

    /** Crea un nodo con el valor dado, sin hijos. */
    public Node(int value) {
        this.value = value;
        this.left  = null;
        this.right = null;
    }
}