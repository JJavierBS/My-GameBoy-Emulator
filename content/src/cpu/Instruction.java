package cpu;

//Es una interfaz funcional, solo indica que hay una función que se ejecuta
//La función es definida en InstructionSet con funciones lambda

@FunctionalInterface
public interface Instruction {
	int execute(Cpu cpu);
}
