package decaf.backend.opt;

import decaf.backend.asm.SubroutineInfo;
import decaf.backend.dataflow.CFGBuilder;
import decaf.backend.dataflow.LivenessAnalyzer;
import decaf.driver.Config;
import decaf.driver.Phase;
import decaf.lowlevel.instr.PseudoInstr;
import decaf.lowlevel.instr.Temp;
import decaf.lowlevel.log.Log;
import decaf.lowlevel.tac.Simulator;
import decaf.lowlevel.tac.TacInstr;
import decaf.lowlevel.tac.TacProg;
import decaf.printing.PrettyCFG;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.*;

/**
 * TAC optimization phase: optimize a TAC program.
 * <p>
 * The original decaf compiler has NO optimization, thus, we implement the transformation as identity function.
 */
public class Optimizer extends Phase<TacProg, TacProg> {
    public Optimizer(Config config) {
        super("optimizer", config);
    }

    @Override
    public TacProg transform(TacProg prog) {
        System.out.println("start transform!");
        while (true) {
            boolean working = false;
            var analyzer = new LivenessAnalyzer<>();
            for (var func: prog.funcs) {
                Log.fine("emit func for %s\n", func.entry.prettyString());
    
                var builder = new CFGBuilder<>();
                var seq = new ArrayList<PseudoInstr>();
                for (var x: func.getInstrSeq()) {
                    seq.add(x);
                }
                var cfg = builder.buildFrom(seq);
                analyzer.accept(cfg);
                Log.ifLoggable(Level.FINE, printer -> new PrettyCFG<>(printer).pretty(cfg));
                for (var bb: cfg) {
                    for (var loc: bb) {
                        if (loc.instr instanceof TacInstr.Assign ||
                            loc.instr instanceof TacInstr.LoadVTbl ||
                            loc.instr instanceof TacInstr.LoadImm4 ||
                            loc.instr instanceof TacInstr.LoadStrConst ||
                            loc.instr instanceof TacInstr.Unary ||
                            loc.instr instanceof TacInstr.Binary ||
                            (loc.instr instanceof TacInstr.Memory && ((TacInstr.Memory)loc.instr).op == TacInstr.Memory.Op.LOAD)) {
                                boolean isDead = true;
                                for (int i = 0; i < loc.instr.dsts.length; i++) {
                                    if (loc.liveOut.contains(loc.instr.dsts[0])) {
                                        isDead = false;
                                        continue;
                                    }
                                }
                                if (isDead) {
                                    func.getInstrSeq().remove(loc.instr);
                                    Log.info("remove %s", loc.instr.toString());
                                    working = true;
                                }
                            }
                        else if (loc.instr instanceof TacInstr.IndirectCall ||
                                 loc.instr instanceof TacInstr.DirectCall) {
                                    boolean isDead = true;
                                    for (int i = 0; i < loc.instr.dsts.length; i++) {
                                        if (loc.liveOut.contains(loc.instr.dsts[0])) {
                                            isDead = false;
                                            continue;
                                        }
                                    }
                                    if (isDead) {
                                        ((TacInstr)loc.instr).removeDest();
                                    }
                                 } 
                    }
                }
            }
            if (!working)
                break;
        }
        return prog;
    }

    @Override
    public void onSucceed(TacProg program) {
        if (config.target.equals(Config.Target.PA4)) {
            // First dump the tac program to file,
            var path = config.dstPath.resolve(config.getSourceBaseName() + ".tac");
            try {
                var printer = new PrintWriter(path.toFile());
                program.printTo(printer);
                printer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            // and then execute it using our simulator.
            var simulator = new Simulator(System.in, config.output);
            simulator.execute(program);
        }
    }
}
