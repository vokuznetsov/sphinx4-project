package good.of.pronunciation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * First parameter in every map is Integer. It is necessary to find quick some parameters, when we know other.
 *
 * @author vkuzn on 18.04.2016.
 */
public class MDEF {

    private Map<Integer, String> base;
    private Map<Integer, String> left;
    private Map<Integer, String> right;
    private Map<Integer, String> p;
    private Map<Integer, Integer> tmat;
    private Map<Integer, List<Integer>> stateId;

    private static Map<String, Integer> baseCorrespondTmat;

    public MDEF(){
        base = new HashMap<>();
        left = new HashMap<>();
        right = new HashMap<>();
        p = new HashMap<>();
        tmat = new HashMap<>();
        stateId = new HashMap<>();

        baseCorrespondTmat = new TreeMap<>();
    }

    public Map<Integer, String> getBase() {
        return base;
    }

    public void setBase(Map<Integer, String> base) {
        this.base = base;
    }

    public Map<Integer, String> getLeft() {
        return left;
    }

    public void setLeft(Map<Integer, String> left) {
        this.left = left;
    }

    public Map<Integer, String> getRight() {
        return right;
    }

    public void setRight(Map<Integer, String> right) {
        this.right = right;
    }

    public Map<Integer, String> getP() {
        return p;
    }

    public void setP(Map<Integer, String> p) {
        this.p = p;
    }

    public Map<Integer, Integer> getTmat() {
        return tmat;
    }

    public void setTmat(Map<Integer, Integer> tmat) {
        this.tmat = tmat;
    }

    public Map<Integer, List<Integer>> getStateId() {
        return stateId;
    }

    public void setStateId(Map<Integer, List<Integer>> stateId) {
        this.stateId = stateId;
    }

    public static Map<String, Integer> getBaseCorrespondTmat() {
        return baseCorrespondTmat;
    }
}
