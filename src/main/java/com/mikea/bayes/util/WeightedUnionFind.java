package com.mikea.bayes.util;

import java.util.Arrays;

/**
 * @author mike.aizatsky@gmail.com
 */
public class WeightedUnionFind {
    private final int[] parent;
    private final int[] componentSize;
    private int count;

    public WeightedUnionFind(int size) {
        count = size;
        parent = new int[size];
        componentSize = new int[size];
        Arrays.fill(componentSize, 1);

        for (int i = 0; i < size; i++) {
            parent[i] = i;
        }

    }

    public int getNumberOfComponents() {
        return count;
    }

    public int getComponent(int p) {
        int result = p;
        while (result != parent[p]) {
            result = parent[p];
        }
        return result;
    }

    public boolean isConnected(int p, int q) {
        return getComponent(p) == getComponent(q);
    }


    public void union(int p, int q) {
        int i = getComponent(p);
        int j = getComponent(q);
        if (i == j) return;

        if (componentSize[i] < componentSize[j]) {
            parent[i] = j;
            componentSize[j] += componentSize[i];
        } else {
            parent[j] = i;
            componentSize[i] += componentSize[j];
        }
        count--;
    }
}
