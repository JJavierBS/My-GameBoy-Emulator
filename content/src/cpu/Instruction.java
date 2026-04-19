package cpu;

@FunctionalInterface
public interface Instruction {
	int execute(Cpu cpu);
}
