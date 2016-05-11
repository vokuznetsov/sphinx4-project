package good.of.pronunciation;

import org.apache.commons.collections4.MultiMap;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.map.MultiValueMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;

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

    private Map<String, Integer> baseLeftRight;
    private Map<Integer, String> p;
    private Map<Integer, Integer> tmat;
    private Map<Integer, List<Integer>> stateId;

    private static Map<String, Integer> baseCorrespondTmat;
    private static MultiValuedMap<Integer, Integer> tmatStateId;

    public MDEF() {
        baseLeftRight = new HashMap<>();
        p = new HashMap<>();
        tmat = new HashMap<>();
        stateId = new HashMap<>();

        baseCorrespondTmat = new TreeMap<>();
        tmatStateId = new HashSetValuedHashMap<>();
    }

    public Map<String, Integer> getBaseLeftRight() {
        return baseLeftRight;
    }

    public void setBaseLeftRight(Map<String, Integer> baseLeftRight) {
        this.baseLeftRight = baseLeftRight;
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

    public static void setBaseCorrespondTmat(Map<String, Integer> baseCorrespondTmat) {
        MDEF.baseCorrespondTmat = baseCorrespondTmat;
    }

    public static MultiValuedMap<Integer, Integer> getTmatStateId() {
        return tmatStateId;
    }

    public static void setTmatStateId(MultiValuedMap<Integer, Integer> tmatStateId) {
        MDEF.tmatStateId = tmatStateId;
    }
}
