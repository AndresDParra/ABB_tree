package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class BPlusTree<K extends Comparable<K>, V> {
    private final int order;
    private Node root;

    public BPlusTree(int order) {
        if (order < 3) {
            throw new IllegalArgumentException("B+ tree order must be at least 3");
        }
        this.order = order;
        this.root = new LeafNode();
    }

    public void insert(K key, V value) {
        Objects.requireNonNull(key, "key");
        SplitResult split = root.insert(key, value);

        if (split != null) {
            InternalNode newRoot = new InternalNode();
            newRoot.keys.add(split.promotedKey);
            newRoot.children.add(root);
            newRoot.children.add(split.rightNode);
            root = newRoot;
        }
    }

    public V search(K key) {
        Objects.requireNonNull(key, "key");
        return root.search(key);
    }

    public TreeSnapshot snapshot() {
        return new TreeSnapshot(root.toView());
    }

    public void printTree() {
        System.out.println(describeTree());
    }

    public String describeTree() {
        StringBuilder out = new StringBuilder();
        List<Node> level = new ArrayList<>();
        level.add(root);

        while (!level.isEmpty()) {
            List<Node> next = new ArrayList<>();
            StringJoiner joiner = new StringJoiner("  ");

            for (Node node : level) {
                joiner.add(node.keys.toString());
                if (!node.isLeaf()) {
                    next.addAll(((InternalNode) node).children);
                }
            }

            out.append(joiner).append(System.lineSeparator());
            level = next;
        }

        return out.toString().trim();
    }

    public String describeLeaves() {
        StringBuilder out = new StringBuilder();
        LeafNode leaf = leftmostLeaf();

        while (leaf != null) {
            out.append(leaf.entriesAsString()).append(" -> ");
            leaf = leaf.next;
        }

        if (out.length() >= 4) {
            out.setLength(out.length() - 4);
        }

        return out.toString();
    }

    private LeafNode leftmostLeaf() {
        Node current = root;
        while (!current.isLeaf()) {
            current = ((InternalNode) current).children.get(0);
        }
        return (LeafNode) current;
    }

    private abstract class Node {
        protected final List<K> keys = new ArrayList<>();

        abstract SplitResult insert(K key, V value);
        abstract V search(K key);
        abstract boolean isLeaf();
        abstract NodeView toView();

        int maxKeys() {
            return order - 1;
        }
    }

    private final class LeafNode extends Node {
        private final List<V> values = new ArrayList<>();
        private LeafNode next;

        @Override
        SplitResult insert(K key, V value) {
            int index = Collections.binarySearch(keys, key);

            if (index >= 0) {
                values.set(index, value);
                return null;
            }

            int insertPos = -index - 1;
            keys.add(insertPos, key);
            values.add(insertPos, value);

            return keys.size() > maxKeys() ? split() : null;
        }

        private SplitResult split() {
            int mid = keys.size() / 2;

            LeafNode right = new LeafNode();
            right.keys.addAll(new ArrayList<>(keys.subList(mid, keys.size())));
            right.values.addAll(new ArrayList<>(values.subList(mid, values.size())));

            keys.subList(mid, keys.size()).clear();
            values.subList(mid, values.size()).clear();

            right.next = this.next;
            this.next = right;

            return new SplitResult(right.keys.get(0), right);
        }

        String entriesAsString() {
            if (keys.isEmpty()) {
                return "[]";
            }

            StringJoiner joiner = new StringJoiner(" | ");
            for (int i = 0; i < keys.size(); i++) {
                joiner.add(keys.get(i) + ":" + values.get(i));
            }
            return joiner.toString();
        }

        @Override
        V search(K key) {
            int index = Collections.binarySearch(keys, key);
            return index >= 0 ? values.get(index) : null;
        }

        @Override
        boolean isLeaf() {
            return true;
        }

        @Override
        NodeView toView() {
            List<String> keyStrings = new ArrayList<>(keys.size());
            List<String> valueStrings = new ArrayList<>(values.size());

            for (int i = 0; i < keys.size(); i++) {
                keyStrings.add(String.valueOf(keys.get(i)));
                valueStrings.add(String.valueOf(values.get(i)));
            }

            return NodeView.leaf(keyStrings, valueStrings);
        }
    }

    private final class InternalNode extends Node {
        private final List<Node> children = new ArrayList<>();

        @Override
        SplitResult insert(K key, V value) {
            int childIndex = findChildIndex(key);
            SplitResult split = children.get(childIndex).insert(key, value);

            if (split == null) {
                return null;
            }

            keys.add(childIndex, split.promotedKey);
            children.add(childIndex + 1, split.rightNode);

            return keys.size() > maxKeys() ? splitInternal() : null;
        }

        private int findChildIndex(K key) {
            int i = 0;
            while (i < keys.size() && key.compareTo(keys.get(i)) >= 0) {
                i++;
            }
            return i;
        }

        private SplitResult splitInternal() {
            int mid = keys.size() / 2;
            K promoted = keys.get(mid);

            InternalNode right = new InternalNode();
            right.keys.addAll(new ArrayList<>(keys.subList(mid + 1, keys.size())));
            right.children.addAll(new ArrayList<>(children.subList(mid + 1, children.size())));

            keys.subList(mid, keys.size()).clear();
            children.subList(mid + 1, children.size()).clear();

            return new SplitResult(promoted, right);
        }

        @Override
        V search(K key) {
            return children.get(findChildIndex(key)).search(key);
        }

        @Override
        boolean isLeaf() {
            return false;
        }

        @Override
        NodeView toView() {
            List<NodeView> childViews = new ArrayList<>(children.size());
            for (Node child : children) {
                childViews.add(child.toView());
            }
            List<String> keyStrings = new ArrayList<>(keys.size());
            for (K key : keys) {
                keyStrings.add(String.valueOf(key));
            }
            return NodeView.internal(keyStrings, childViews);
        }
    }

    public static final class TreeSnapshot {
        private final NodeView root;

        private TreeSnapshot(NodeView root) {
            this.root = root;
        }

        public NodeView getRoot() {
            return root;
        }
    }

    public static final class NodeView {
        private final boolean leaf;
        private final List<String> keys;
        private final List<String> values;
        private final List<NodeView> children;

        private NodeView(boolean leaf, List<String> keys, List<String> values, List<NodeView> children) {
            this.leaf = leaf;
            this.keys = Collections.unmodifiableList(new ArrayList<>(keys));
            this.values = Collections.unmodifiableList(new ArrayList<>(values));
            this.children = Collections.unmodifiableList(new ArrayList<>(children));
        }

        public static NodeView leaf(List<String> keys, List<String> values) {
            return new NodeView(true, keys, values, Collections.<NodeView>emptyList());
        }

        public static NodeView internal(List<String> keys, List<NodeView> children) {
            return new NodeView(false, keys, Collections.<String>emptyList(), children);
        }

        public boolean isLeaf() {
            return leaf;
        }

        public List<String> getKeys() {
            return keys;
        }

        public List<String> getValues() {
            return values;
        }

        public List<NodeView> getChildren() {
            return children;
        }

        public List<String> getDisplayLines() {
            if (leaf) {
                if (keys.isEmpty()) {
                    return Collections.singletonList("[]");
                }

                StringJoiner joiner = new StringJoiner(" | ");
                for (int i = 0; i < keys.size(); i++) {
                    joiner.add(keys.get(i) + ":" + values.get(i));
                }
                return Collections.singletonList(joiner.toString());
            }

            return Collections.singletonList(String.join(" | ", keys));
        }
    }

    private final class SplitResult {
        private final K promotedKey;
        private final Node rightNode;

        private SplitResult(K promotedKey, Node rightNode) {
            this.promotedKey = promotedKey;
            this.rightNode = rightNode;
        }
    }
}

