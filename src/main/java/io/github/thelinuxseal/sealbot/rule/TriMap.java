package io.github.thelinuxseal.sealbot.rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TriMap<A,B,C> {
    private record Pair<X,Y>(X arg1, Y arg2) {};
    private final Map<A, Pair<B,C>> data = new HashMap<>();

    public void set(A key, B val1, C val2){
        data.put(key, new Pair<>(val1,val2));
    }

    public B getFirst(A key) {
        Pair<B, C> p = data.get(key);
        return p == null ? null : p.arg1();
    }

    public C getSecond(A key) {
        Pair<B, C> p = data.get(key);
        return p == null ? null : p.arg2();
    }

    public boolean has(A key) { return data.containsKey(key); }
    public void remove(A key) { data.remove(key); }
    public void clear() { data.clear(); }

    public List<A> keyList() {
        return new ArrayList<>(data.keySet());
    }

    public List<B> valueFirstList() {
        List<B> out = new ArrayList<>(data.size());
        for (Pair<B, C> p : data.values()) out.add(p.arg1());
        return out;
    }

    public List<C> valueSecondList() {
        List<C> out = new ArrayList<>(data.size());
        for (Pair<B, C> p : data.values()) out.add(p.arg2());
        return out;
    }

    public static <A, B, C> TriMap<A, B, C> of() {
        return new TriMap<>();
    }

    public static <A, B, C> TriMap<A, B, C> of(
        A k1, B a1, C b1) {
        TriMap<A, B, C> m = of();
        m.put(k1, a1, b1);
        return m;
    }

    public static <A, B, C> TriMap<A, B, C> of(
        A k1, B a1, C b1,
        A k2, B a2, C b2) {
        TriMap<A, B, C> m = of(k1, a1, b1);
        m.put(k2, a2, b2);
        return m;
    }

    public static <A, B, C> TriMap<A, B, C> of(
        A k1, B a1, C b1,
        A k2, B a2, C b2,
        A k3, B a3, C b3) {
        TriMap<A, B, C> m = of(k1, a1, b1, k2, a2, b2);
        m.put(k3, a3, b3);
        return m;
    }

    public static <A, B, C> TriMap<A, B, C> of(
        A k1, B a1, C b1,
        A k2, B a2, C b2,
        A k3, B a3, C b3,
        A k4, B a4, C b4) {
        TriMap<A, B, C> m = of(k1, a1, b1, k2, a2, b2, k3, a3, b3);
        m.put(k4, a4, b4);
        return m;
    }

    public static <A, B, C> TriMap<A, B, C> of(
        A k1, B a1, C b1,
        A k2, B a2, C b2,
        A k3, B a3, C b3,
        A k4, B a4, C b4,
        A k5, B a5, C b5) {
        TriMap<A, B, C> m = of(k1, a1, b1, k2, a2, b2, k3, a3, b3, k4, a4, b4);
        m.put(k5, a5, b5);
        return m;
    }

    public static <A, B, C> TriMap<A, B, C> of(
        A k1, B a1, C b1,
        A k2, B a2, C b2,
        A k3, B a3, C b3,
        A k4, B a4, C b4,
        A k5, B a5, C b5,
        A k6, B a6, C b6) {
        TriMap<A, B, C> m = of(k1, a1, b1, k2, a2, b2, k3, a3, b3, k4, a4, b4, k5, a5, b5);
        m.put(k6, a6, b6);
        return m;
    }

    public static <A, B, C> TriMap<A, B, C> of(
        A k1, B a1, C b1,
        A k2, B a2, C b2,
        A k3, B a3, C b3,
        A k4, B a4, C b4,
        A k5, B a5, C b5,
        A k6, B a6, C b6,
        A k7, B a7, C b7) {
        TriMap<A, B, C> m = of(k1, a1, b1, k2, a2, b2, k3, a3, b3, k4, a4, b4, k5, a5, b5, k6, a6, b6);
        m.put(k7, a7, b7);
        return m;
    }

    public static <A, B, C> TriMap<A, B, C> of(
        A k1, B a1, C b1,
        A k2, B a2, C b2,
        A k3, B a3, C b3,
        A k4, B a4, C b4,
        A k5, B a5, C b5,
        A k6, B a6, C b6,
        A k7, B a7, C b7,
        A k8, B a8, C b8) {
        TriMap<A, B, C> m = of(k1, a1, b1, k2, a2, b2, k3, a3, b3, k4, a4, b4, k5, a5, b5, k6, a6, b6, k7, a7, b7);
        m.put(k8, a8, b8);
        return m;
    }

    public static <A, B, C> TriMap<A, B, C> of(
        A k1, B a1, C b1,
        A k2, B a2, C b2,
        A k3, B a3, C b3,
        A k4, B a4, C b4,
        A k5, B a5, C b5,
        A k6, B a6, C b6,
        A k7, B a7, C b7,
        A k8, B a8, C b8,
        A k9, B a9, C b9) {
        TriMap<A, B, C> m = of(k1, a1, b1, k2, a2, b2, k3, a3, b3, k4, a4, b4, k5, a5, b5, k6, a6, b6, k7, a7, b7, k8, a8, b8);
        m.put(k9, a9, b9);
        return m;
    }

    public static <A, B, C> TriMap<A, B, C> of(
        A k1, B a1, C b1,
        A k2, B a2, C b2,
        A k3, B a3, C b3,
        A k4, B a4, C b4,
        A k5, B a5, C b5,
        A k6, B a6, C b6,
        A k7, B a7, C b7,
        A k8, B a8, C b8,
        A k9, B a9, C b9,
        A k10, B a10, C b10) {
        TriMap<A, B, C> m = of(k1, a1, b1, k2, a2, b2, k3, a3, b3, k4, a4, b4, k5, a5, b5, k6, a6, b6, k7, a7, b7, k8, a8, b8, k9, a9, b9);
        m.put(k10, a10, b10);
        return m;
    }

    // Like Map.of, reject duplicate keys instead of silently overwriting
    private void put(A key, B val1, C val2) {
        if (data.containsKey(key)) {
            throw new IllegalArgumentException("duplicate key: " + key);
        }
        data.put(key, new Pair<>(val1, val2));
    }

    public static <A, B, C> TriMap<A, B, C> ofLists(
        List<? extends A> keys,
        List<? extends B> firsts,
        List<? extends C> seconds) {

        if (keys.size() != firsts.size() || keys.size() != seconds.size()) {
            throw new IllegalArgumentException(
                "List sizes must match: keys=" + keys.size()
                    + ", firsts=" + firsts.size()
                    + ", seconds=" + seconds.size());
        }

        TriMap<A, B, C> m = new TriMap<>();
        for (int i = 0; i < keys.size(); i++) {
            m.put(keys.get(i), firsts.get(i), seconds.get(i));   // rejects duplicate keys
        }
        return m;
    }
}
