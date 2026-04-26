package org.example;

/** Árbol Binario de Búsqueda (ABB). Almacena enteros sin duplicados. */
public class ABBTree {
    private Node root;

    /** Crea un árbol vacío. */
    public ABBTree() {
        root = null;
    }

    /** Retorna true si el árbol no tiene ningún nodo. */
    public boolean isEmpty() {
        return root == null;
    }

    /** Inserta un valor entero en su posición correcta. Ignora duplicados. */
    public void insert(int value) {
        root = insertRecursive(root, value);
    }

    /** Auxiliar recursivo de insert: recorre el árbol hasta encontrar el lugar vacío correcto. */
    private Node insertRecursive(Node current, int value) {
        if (current == null) {
            return new Node(value);
        }

        if (value < current.value) {
            current.left = insertRecursive(current.left, value);
        } else if (value > current.value) {
            current.right = insertRecursive(current.right, value);
        } else {
            return current; // valor duplicado, no se inserta
        }

        return current;
    }

    /** Retorna true si el valor existe en el árbol. */
    public boolean search(int value) {
        return searchRecursive(root, value);
    }

    /** Auxiliar recursivo de search: aprovecha el orden del ABB para descartar ramas. */
    private boolean searchRecursive(Node current, int value) {
        if (current == null) {
            return false;
        }

        if (value == current.value) {
            return true;
        }

        return value < current.value
            ? searchRecursive(current.left, value)
            : searchRecursive(current.right, value);
    }

    /** Recorre en inorden (izquierda-raíz-derecha) y acumula los valores en el StringBuilder. */
    public void inorderTraversal(Node node, StringBuilder result) {
        if (node != null) {
            inorderTraversal(node.left, result);
            result.append(node.value).append(" ");
            inorderTraversal(node.right, result);
        }
    }

    /** Retorna un String con los valores en orden ascendente (inorden). */
    public String inorderString() {
        StringBuilder result = new StringBuilder();
        inorderTraversal(root, result);
        return result.toString().trim();
    }

    /** Recorre en preorden (raíz-izquierda-derecha) y acumula los valores en el StringBuilder. */
    public void preorderTraversal(Node node, StringBuilder result) {
        if (node != null) {
            result.append(node.value).append(" ");
            preorderTraversal(node.left, result);
            preorderTraversal(node.right, result);
        }
    }

    /** Retorna un String con los valores en recorrido preorden. */
    public String preorderString() {
        StringBuilder result = new StringBuilder();
        preorderTraversal(root, result);
        return result.toString().trim();
    }

    /** Recorre en postorden (izquierda-derecha-raíz) y acumula los valores en el StringBuilder. */
    public void postorderTraversal(Node node, StringBuilder result) {
        if (node != null) {
            postorderTraversal(node.left, result);
            postorderTraversal(node.right, result);
            result.append(node.value).append(" ");
        }
    }

    /** Retorna un String con los valores en recorrido postorden. */
    public String postorderString() {
        StringBuilder result = new StringBuilder();
        postorderTraversal(root, result);
        return result.toString().trim();
    }

    /** Retorna el valor mínimo del árbol (nodo más a la izquierda). Lanza excepción si está vacío. */
    public int getMinValue() {
        if (root == null) {
            throw new IllegalStateException("El árbol está vacío");
        }
        return getMinValueRecursive(root);
    }

    /** Auxiliar de getMinValue: desciende por la izquierda hasta el último nodo. */
    private int getMinValueRecursive(Node current) {
        while (current.left != null) {
            current = current.left;
        }
        return current.value;
    }

    /** Retorna el valor máximo del árbol (nodo más a la derecha). Lanza excepción si está vacío. */
    public int getMaxValue() {
        if (root == null) {
            throw new IllegalStateException("El árbol está vacío");
        }
        return getMaxValueRecursive(root);
    }

    /** Auxiliar de getMaxValue: desciende por la derecha hasta el último nodo. */
    private int getMaxValueRecursive(Node current) {
        while (current.right != null) {
            current = current.right;
        }
        return current.value;
    }

    /** Retorna la cantidad total de nodos en el árbol (peso). */
    public int getSize() {
        return sizeRecursive(root);
    }

    /** Auxiliar recursivo de getSize: suma 1 por cada nodo visitado. */
    private int sizeRecursive(Node node) {
        if (node == null) {
            return 0;
        }
        return 1 + sizeRecursive(node.left) + sizeRecursive(node.right);
    }

    /** Retorna la altura del árbol. Árbol vacío = -1, solo raíz = 0. */
    public int getHeight() {
        return heightRecursive(root);
    }

    /** Auxiliar recursivo de getHeight: calcula la rama más larga entre izquierda y derecha. */
    private int heightRecursive(Node node) {
        if (node == null) {
            return -1;
        }

        int leftHeight = heightRecursive(node.left);
        int rightHeight = heightRecursive(node.right);

        return 1 + Math.max(leftHeight, rightHeight);
    }

    /** Retorna el nivel donde se encuentra el valor (raíz = 0). Retorna -1 si no existe. */
    public int getLevel(int value) {
        return getLevelRecursive(root, value, 0);
    }

    /** Auxiliar recursivo de getLevel: desciende contando niveles hasta encontrar el valor. */
    private int getLevelRecursive(Node node, int value, int level) {
        if (node == null) {
            return -1;
        }
        if (value == node.value) {
            return level;
        }
        return value < node.value
            ? getLevelRecursive(node.left,  value, level + 1)
            : getLevelRecursive(node.right, value, level + 1);
    }

    /** Retorna la cantidad de hojas del árbol (nodos sin ningún hijo). */
    public int countLeaves() {
        return countLeavesRecursive(root);
    }

    /** Auxiliar recursivo de countLeaves: identifica nodos sin hijos y los cuenta. */
    private int countLeavesRecursive(Node node) {
        if (node == null) {
            return 0;
        }

        if (node.left == null && node.right == null) {
            return 1;
        }

        return countLeavesRecursive(node.left) + countLeavesRecursive(node.right);
    }

    /** Retorna el nodo padre del nodo buscado. Retorna null si es la raíz o no se encuentra. */
    public Node findParent(Node root, Node nodeToFind) {
        if (root == null || root == nodeToFind) {
            return null;
        }

        if ((root.left == nodeToFind) || (root.right == nodeToFind)) {
            return root;
        }

        if (nodeToFind.value < root.value) {
            return findParent(root.left, nodeToFind);
        } else {
            return findParent(root.right, nodeToFind);
        }
    }

    /** Retorna el nodo con el valor mínimo dentro del subárbol recibido. */
    public Node findMinimum(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    /** Elimina el nodo con el valor dado dentro del subárbol y retorna la nueva raíz del subárbol. */
    public Node delete(Node root, int value) {
        if (root == null) {
            return root;
        }

        if (value < root.value) {
            root.left = delete(root.left, value);
        } else if (value > root.value) {
            root.right = delete(root.right, value);
        } else {
            // Nodo con un solo hijo o sin hijos
            if (root.left == null) {
                return root.right;
            } else if (root.right == null) {
                return root.left;
            }

            // Nodo con dos hijos: se sustituye con el sucesor inorden (mínimo del subárbol derecho)
            Node inorderSuccessor = findMinimum(root.right);
            root.value = inorderSuccessor.value;
            root.right = delete(root.right, inorderSuccessor.value);
        }
        return root;
    }

    /** Elimina el nodo con el valor dado a partir de la raíz del árbol. */
    public void delete(int value) {
        root = delete(root, value);
    }

    /** Elimina todos los nodos del árbol dejándolo vacío. */
    public void clear() {
        root = null;
    }

    /** Retorna la raíz del árbol. */
    public Node getRoot() {
        return root;
    }

    /** Retorna un String con los valores recorridos por niveles de arriba hacia abajo (BFS). */
    public String printByLevel() {
        if (root == null) {
            return "";
        }

        java.util.Queue<Node> queue = new java.util.LinkedList<>();
        StringBuilder result = new StringBuilder();
        queue.offer(root);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            result.append(current.value).append(" ");

            if (current.left != null) {
                queue.offer(current.left);
            }

            if (current.right != null) {
                queue.offer(current.right);
            }
        }

        return result.toString().trim();
    }
}