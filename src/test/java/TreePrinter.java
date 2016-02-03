import top.yatt.dfargx.tree.node.LNull;
import top.yatt.dfargx.tree.node.Node;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Created on 2015/5/9.
 */
public class TreePrinter {

    private static TreePrinter instance = new TreePrinter();

    public static TreePrinter getInstance() {
        return instance;
    }


    public void printTree(Node root) {
        Node copy = copyTree(root);
        int h = calculateDepth(copy, 0);
        complete(copy, 0, h);
        Queue<Node> q = new ArrayDeque<>();
        q.offer(copy);
        int t = 1;
        int j = 0;
        while (!q.isEmpty()) {
            Node node = q.poll();
            int width = 4 * (int) Math.pow(2, h - t);
            if (node instanceof LNull) {
                for (int i = 0; i < width * 2; i++) {
                    System.out.print(' ');
                }
            } else {
                String s = node.toString();
                for (int i = 0; i < width; i++) {
                    if (!(node.left() instanceof LNull)) {
                        if (i < width / 2 || t == h) {
                            System.out.print(' ');
                        } else if (i == width / 2) {
                            System.out.print('|');
                        } else System.out.print('-');
                    } else System.out.print(' ');
                }
                System.out.print(s);
                for (int i = s.length(); i < width; i++) {
                    if (!(node.right() instanceof LNull)) {
                        if (i < width / 2 && t != h) {
                            System.out.print('-');
                        } else if (i == width / 2 && t != h) {
                            System.out.print('|');
                        } else System.out.print(' ');
                    } else System.out.print(' ');
                }
            }
            if (++j == Math.pow(2, t) - 1) {
                System.out.println();
                t += 1;
            }
            if (node.hasLeft()) {
                q.offer(node.left());
            }
            if (node.hasRight()) {
                q.offer(node.right());
            }
        }
    }

    private void complete(Node node, int k, int h) {
        if (k + 1 < h) {
            if (!node.hasLeft()) {
                node.setLeft(new LNull());
            }
            if (!node.hasRight()) {
                node.setRight(new LNull());
            }
            complete(node.left(), k + 1, h);
            complete(node.right(), k + 1, h);
        }
    }

    private int calculateDepth(Node node, int depth) {
        depth += 1;
        int l = depth, r = depth;
        if (node.hasLeft()) {
            l = calculateDepth(node.left(), depth);
        }
        if (node.hasRight()) {
            r = calculateDepth(node.right(), depth);
        }
        return l > r ? l : r > depth ? r : depth;
    }

    private Node copyTree(Node root) {
        Node newRoot = root.copy();
        if (root.hasLeft()) {
            newRoot.setLeft(copyTree(root.left()));
        }
        if (root.hasRight()) {
            newRoot.setRight(copyTree(root.right()));
        }
        return newRoot;
    }
}
