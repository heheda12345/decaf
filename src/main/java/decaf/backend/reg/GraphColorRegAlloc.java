package decaf.backend.reg;

import decaf.backend.asm.AsmEmitter;
import decaf.backend.asm.HoleInstr;
import decaf.backend.asm.SubroutineEmitter;
import decaf.backend.asm.SubroutineInfo;
import decaf.backend.dataflow.BasicBlock;
import decaf.backend.dataflow.CFG;
import decaf.backend.dataflow.Loc;
import decaf.lowlevel.instr.PseudoInstr;
import decaf.lowlevel.instr.Reg;
import decaf.lowlevel.instr.Temp;
import decaf.lowlevel.log.Log;
import decaf.printing.PrettyCFG;

import java.util.*;
import java.util.logging.*;
/**
 * Brute force greedy register allocation algorithm.
 * <p>
 * To make our life easier, don't consider any special registers that may be used during call.
 */
public final class GraphColorRegAlloc extends RegAlloc {

    public GraphColorRegAlloc(AsmEmitter emitter) {
        super(emitter);
        for (var reg : emitter.allocatableRegs) {
            reg.used = false;
        }
        g = new ColorGraph(emitter.allocatableRegs);
    }

    private ColorGraph g;

    @Override
    public void accept(CFG<PseudoInstr> graph, SubroutineInfo info) {
        Log.ifLoggable(Level.INFO, printer -> new PrettyCFG<>(printer).pretty(graph));
        g.init(info);
        for (int i = 0; i < info.numArg; i++) {
            g.addNode(i);
        }
        for (var bb: graph) {
            for (var temp: bb.def)
            g.addNode(temp.index);
            for (var temp: bb.liveIn)
            g.addNode(temp.index);
            for (var temp1: bb.liveIn) {
                for (var temp2: bb.liveIn)
                    g.addEdge(temp1.index, temp2.index);
            }
            for (var loc: bb.locs) {
                for (var dst: loc.instr.dsts) {
                    for (var out: loc.liveOut) {
                        g.addEdge(out.index, dst.index);
                        g.addEdge(dst.index, out.index);
                    }
                }
            } 
        }
        g.color();

        bindings.clear();
        for (var reg : emitter.allocatableRegs) {
            reg.occupied = false;
        }
        var subEmitter = emitter.emitSubroutine(info);
        for (int i = 0; i < info.numArg; i++) {
            Temp arg = new Temp(i);
            Reg reg = g.getColor(arg);
            subEmitter.emitLoadFromStack(reg, arg);
            bind(arg, reg);
        }
        for (var bb : graph) {
            bb.label.ifPresent(subEmitter::emitLabel);
            localAlloc(bb, subEmitter);
        }
        subEmitter.emitEnd();
    }

    private Map<Temp, Reg> bindings = new TreeMap<>();

    private void bind(Temp temp, Reg reg) {
        reg.used = true;
        bindings.put(temp, reg);
        reg.occupied = true;
        reg.temp = temp;
    }

    private void localAlloc(BasicBlock<PseudoInstr> bb, SubroutineEmitter subEmitter) {
        var callerNeedSave = new ArrayList<Reg>();
        for (var loc : bb.allSeq()) {
            // Handle special instructions on caller save/restore.
            if (loc.instr instanceof HoleInstr) {
                if (loc.instr.equals(HoleInstr.CallerSave)) {
                    for (var temp: loc.liveOut) {
                        var reg = g.getColor(temp);
                        boolean callerSave = false;
                        for (var r: emitter.callerSaveRegs)
                            if (reg.index == r.index) {
                                callerSave = true;
                                break;
                            }
                        if (callerSave) {
                            callerNeedSave.add(reg);
                            subEmitter.emitStoreToStack(reg);
                        }
                    }
                    continue;
                }

                if (loc.instr.equals(HoleInstr.CallerRestore)) {
                    for (var reg : callerNeedSave) {
                        subEmitter.emitLoadFromStack(reg, reg.temp);
                    }
                    callerNeedSave.clear();
                    continue;
                }
            }

            // For normal instructions: allocate registers for every read/written temp. Skip the already specified
            // special registers.
            allocForLoc(loc, subEmitter);
        }

        // Before we leave a basic block, we must copy values of all live variables from registers (if exist)
        // to stack, as all these registers will be reset (as unoccupied) when entering another basic block.
        // for (var temp : bb.liveOut) {
        //     if (bindings.containsKey(temp)) {
        //         subEmitter.emitStoreToStack(bindings.get(temp));
        //     }
        // }

        // Handle the last instruction, if it is a branch/return block.
        if (!bb.isEmpty() && !bb.kind.equals(BasicBlock.Kind.CONTINUOUS)) {
            allocForLoc(bb.locs.get(bb.locs.size() - 1), subEmitter);
        }
    }

    private void allocForLoc(Loc<PseudoInstr> loc, SubroutineEmitter subEmitter) {
        // System.out.printf("handle %s\n", loc.instr);
        var instr = loc.instr;
        var srcRegs = new Reg[instr.srcs.length];
        var dstRegs = new Reg[instr.dsts.length];

        for (var i = 0; i < instr.srcs.length; i++) {
            srcRegs[i] = g.getColor(instr.srcs[i]);
            // System.out.printf("src %s: %s\n", instr.srcs[i], srcRegs[i]);
        }
        
        for (var i = 0; i < instr.dsts.length; i++) {
            dstRegs[i] = g.getColor(instr.dsts[i]);
            bind(instr.dsts[i], dstRegs[i]);
            // System.out.printf("dst %s: %s\n", instr.dsts[i], dstRegs[i]);
        }

        subEmitter.emitNative(instr.toNative(dstRegs, srcRegs));
    }
}
