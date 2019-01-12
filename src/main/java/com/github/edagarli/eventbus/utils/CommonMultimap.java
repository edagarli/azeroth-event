package com.github.edagarli.eventbus.utils;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author edagarli(卤肉)
 * Email: lizhi@edagarli.com
 * github: http://github.com/edagarli
 * Date: 2017/12/14
 * Time: 23:40
 * Desc: 重复key的map，使用监听的type，取出所有的监听器
 */
public class CommonMultimap<K, V> {

    private final Map<K, List<V>> map;

    /**
     * 构造器
     */
    public CommonMultimap() {
        map = new HashMap<>();
    }

    List<V> createList() {
        return new ArrayList<>();
    }

    /**
     * put to map
     *
     * @param key   键
     * @param value 值
     * @return boolean
     */
    public boolean put(K key, V value) {
        List<V> list = map.get(key);
        if (list == null) {
            list = createList();
            map.put(key, list);
        }
        return list.add(value);
    }

    /**
     * get List by key
     *
     * @param key 键
     * @return List
     */
    public List<V> get(K key) {
        List<V> list = map.get(key);
        if (list == null) {
            list = createList();
        }
        return list;
    }

    /**
     * map大小
     *
     * @return
     */
    public int size() {
        return map.size();
    }

    /**
     * 判断是否为空
     */
    public boolean isEmpty() {
        return CollectionUtils.isEmpty(map);
    }

    /**
     * clear map
     */
    public void clear() {
        map.clear();
    }

}
