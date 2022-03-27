package fun.fifu.onetimepad.collection;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 这是一只满二叉树
 *
 * @param <T> 二叉树存储的类型
 * @author NekokeCore
 */
public class FullBinaryTree<T> {
    Integer baseLayer;
    public List<Pojo<T>> tree;

    @Data
    public static class Pojo<T> {
        int index;
        T value;
    }

    public void updateTreeIndex() {
        AtomicInteger i = new AtomicInteger(-1);
        tree.forEach(p -> {
            p.setIndex(i.incrementAndGet());
        });
    }

    /**
     * 获得对应索引的Pojo
     *
     * @param index 要获得的Pojo的索引
     * @return 对应索引的Pojo
     */
    public Pojo<T> getPojo(int index) {
        return tree.get(index);
    }

    /**
     * 根据层数创建二叉树
     *
     * @param layer 层数
     */
    public FullBinaryTree(int layer) {
        this.baseLayer = layer;
        int nodeTotal = (int) Math.pow(2, layer);
        tree = new ArrayList<>(nodeTotal);
        for (int i = 0; i < nodeTotal; i++) {
            tree.add(new Pojo<>());
        }
        updateTreeIndex();
    }

    /**
     * 在本树上添加一层
     *
     * @return 添加后的总层数
     */
    public int addLayer() {
        ++baseLayer;
        for (int i = 0; i < getNodeTotal(baseLayer); i++) {
            tree.add(new Pojo<>());
        }
        updateTreeIndex();
        return baseLayer;
    }

    /**
     * 获取这棵二叉树的总层数
     *
     * @return 这棵二叉树的总层数
     */
    public int getLayerTotal() {
        return baseLayer;
    }

    /**
     * 判断节点是否为左节点
     *
     * @param index 节点索引
     * @return true：左节点 faalse：右节点
     */
    public boolean isLeft(int index) {
        return (index & 1) != 1;
    }

    /**
     * 判断节点是否为右节点
     *
     * @param index 节点索引
     * @return true：右节点 faalse：左节点
     */
    public boolean isRight(int index) {
        return !isLeft(index);
    }

    /**
     * 获取父节点
     *
     * @param index 节点索引
     * @return 该节点的父节点
     */
    public Pojo<T> getFather(int index) {
        if (index == 1) return null;
        if (isLeft(index)) {
            return getPojo(index / 2);
        } else {
            return getPojo((index - 1) / 2);
        }
    }

    /**
     * 获取妹妹节点
     *
     * @param index 节点索引
     * @return 该节点的妹妹节点
     */
    public Pojo<T> getSisters(int index) {
        if (index == 1) return null;
        if (isLeft(index)) {
            return getPojo(index + 1);
        } else {
            return getPojo(index - 1);
        }
    }

    /**
     * 获取左女儿节点
     *
     * @param index 节点索引
     * @return 该节点的左女儿节点
     */
    public Pojo<T> getLeftDaughter(int index) {
        return getPojo(index * 2);
    }

    /**
     * 获取右女儿节点
     *
     * @param index 节点索引
     * @return 该节点的右女儿节点
     */
    public Pojo<T> getRightDaughter(int index) {
        return getPojo(index * 2 + 1);
    }


    /**
     * 获取二叉树的节点总数
     *
     * @return 本二叉树的节点总数
     */
    public int getNodeTotal() {
        return getNodeTotal(this.baseLayer);
    }

    /**
     * 获取二叉树在第layer层的节点数
     *
     * @return 本二叉树在第layer层的节点数
     */
    public int getNodeTotal(int layer) {
        return (int) Math.pow(2, layer) - 1;
    }

    /**
     * 获取指定层的索引列表
     *
     * @param layer 要获取索引列表的层
     * @return 目标层的索引列表
     */
    public int[] getLayerIndex(int layer) {
        int[] layerIndex = new int[(int) Math.pow(2, layer - 1)];
        for (int i = 0; i < layerIndex.length; i++) {
            layerIndex[i] = layerIndex.length + i;
        }
        return layerIndex;
    }

    /**
     * 用目标的索引或该层元素个数获他在第几层
     *
     * @param indexOrNumber 目标索引或该层元素个数
     * @return 在第几层
     */
    public static int getLayer(int indexOrNumber) {
        return log2(indexOrNumber) + 1;
    }

    /**
     * 位运算计算 log2(n)
     *
     * @param n n
     * @return log2(n)
     */
    public static int log2(int n) {
        int count = 0;
        while (n > 0) {
            n >>= 1;
            count++;
        }
        return count - 1;
    }

}