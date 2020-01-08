package decaf.backend.reg;

import java.util.*;
import java.util.logging.Level;

import decaf.backend.asm.SubroutineInfo;
import decaf.lowlevel.instr.Reg;
import decaf.lowlevel.instr.Temp;
import decaf.lowlevel.log.Log;

public class ColorGraph {
    public ColorGraph(Reg[] ar) {
        edges = new HashMap<>();
        nodes = new HashSet<>();
        result = new HashMap<>();
        node_size = 0;
        allocatableRegs = ar;
    }

    int max(int x, int y) {
        return x > y ? x : y;
    }

    public void addNode(int x) {
        // System.out.printf("add node %d\n", x);
        if (x < 0)
            return;
        if (!nodes.contains(x)) {
            edges.put(x, new HashSet<>());
            nodes.add(x);
        }
        node_size = max(node_size, x + 1);
    }

    public void addEdge(int x, int y) {
        if (x == y || x < 0 || y < 0)
            return;
        edges.get(x).add(y);
    }

    public void color() {
        // outit();
        int k = 1;
        while (!color(k))
            k++;
    }

    private boolean color(int k) {
        boolean[] removed = new boolean[node_size];
        for (int i = 0; i < node_size; i++)
            removed[i] = false;
        ArrayList<Integer> removeOrder = new ArrayList<>();
        while (true) {
            boolean finish = true;
            for (var x: nodes) {
                if (removed[x.intValue()])
                    continue;
                int cnt = 0;
                for (var y: edges.get(x)) {
                    if (!removed[y])
                        cnt++;
                }
                if (cnt < k) {
                    removeOrder.add(x);
                    removed[x] = true;
                    finish = false;
                }
            }
            if (finish)
                break;
        }

        if (removeOrder.size() != nodes.size())
            return false;
        
        int[] alloc = new int[node_size];
        for (int i = 0; i < node_size; i++)
            alloc[i] = -1;
        for (int xx = removeOrder.size() - 1; xx >= 0; xx--) {
            int x = removeOrder.get(xx);
            for (int i = 0; i < k; i++) {
                boolean used = false;
                for (var y: edges.get(x)) {
                    if (alloc[y] == i) {
                        used = true;
                        break;
                    }
                }
                if (!used) {
                    alloc[x] = i;
                    Log.info("alloc %s to %d", allocatableRegs[i], x);
                    break;
                }
            }
        }
        for (var x: nodes) {
            result.put(x, alloc[x]);
        }
        return true;
    }

    public void outit() {
        if (Log.isLoggable(Level.INFO)) {
            for (var x: nodes) {
                System.out.printf("%d: ", x);
                for (var y: edges.get(x)) {
                    System.out.printf("%d ", y);
                }
                System.out.printf("\n");
            }
        }
    }

    public Reg getColor(Temp temp) {
        // System.out.printf("get color %s\n", temp);
        return temp.index < 0 ?  (Reg)temp: allocatableRegs[result.get(temp.index)];
    }

    void init(SubroutineInfo ifo) {
        edges.clear();
        nodes.clear();
        result.clear();
        node_size = 0;
        info = ifo;
        Log.info("clear!");
    }

    private HashMap<Integer, HashSet<Integer>> edges;
    private HashSet<Integer> nodes;
    private HashMap<Integer, Integer> result;
    int node_size;
    Reg[] allocatableRegs;
    SubroutineInfo info;
}