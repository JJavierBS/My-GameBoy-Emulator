# My GameBoy Emulator

Un emulador de Game Boy escrito en Java que reproduce el comportamiento del hardware original de Nintendo Game Boy (DMG).

## Estado del Proyecto

🚧 **En desarrollo activo**

El emulador está actualmente en fase de desarrollo y **no funciona completamente** debido a bugs en el sistema de timing/timer. Aunque puede cargar y mostrar las pantallas iniciales de ciertas ROMs, presenta problemas de sincronización que impiden la ejecución correcta de juegos.

### ✅ Lo que funciona
- Carga de ROMs (.gb)
- Ejecución básica de instrucciones de CPU
- Renderizado inicial de gráficos
- Pantallas de inicio de algunas ROMs
- Interfaz gráfica básica con GPU Debugger

### ❌ Problemas conocidos
- **Bug crítico en el timer**: Problemas de sincronización que afectan el timing de la CPU
- Fallo en el test `01-read_timing.gb` (único test de cpu_instrs que no pasa)
- Corrupción de memoria en juegos como Tetris
- Timing no preciso a nivel de ciclo (necesita migración a modelo cycle-accurate)

## Estructura del Proyecto

```
src/
├── audio/          # Procesamiento de audio (no implementado)
├── cpu/            # Procesador central y conjunto de instrucciones
│   ├── Cpu.java
│   ├── InstructionSet.java
│   ├── Timer.java
│   └── ...
├── emulator/       # Núcleo del emulador
│   ├── Emulator.java
│   ├── Main.java
│   └── RomLoader.java
├── gpu/            # Unidad de procesamiento gráfico
│   ├── Gpu.java
│   ├── GpuDisplay.java
│   └── GpuDebugger.java
├── memory/         # Gestión de memoria
│   ├── Mmu.java
│   ├── Memory.java
│   └── Cartridge.java
└── utils/          # Utilidades
```

## ROMs de Prueba

El proyecto incluye varias ROMs de test en `romTest/`:
- Tests de CPU (cpu_instrs): La mayoría pasan excepto `01-read_timing.gb`
- ROMs comerciales como Tetris (con problemas de corrupción)
- Tests compilados de mooneye-gb

## Compilación y Ejecución

### Requisitos
- Java 11 o superior
- IDE compatible con Java (Eclipse, IntelliJ, etc.)

### Ejecutar
1. Clona el repositorio
2. Abre el proyecto en tu IDE favorito
3. Ejecuta la clase `Main.java`
4. El emulador cargará automáticamente la ROM especificada en `Emulator.java`

## Contribuciones y Ayuda

### 🤝 ¡Se aceptan contribuciones!

Este proyecto está abierto a contribuciones de la comunidad. Cualquier nivel de experiencia es bienvenido:
- Principiantes: documentación, tests, pequeñas mejoras
- Desarrolladores: corrección de bugs, nuevas características
- Expertos en emulación: optimización y timing preciso

**¡Tu ayuda será muy bienvenida!**

### 📝 Consejos y sugerencias

Si tienes consejos sobre:
- Cómo implementar un timer más preciso
- Migración a modelo cycle-accurate
- Solución de bugs de timing
- Mejores prácticas en emulación

No dudes en abrir un **Issue** o enviar un **Pull Request**.

### 🐛 Reportar bugs

Para reportar bugs, por favor incluye:
- ROM que causa el problema
- Comportamiento esperado vs observado
- Screenshots si es aplicable
- Logs de depuración si están disponibles

## Recursos Útiles

- [Game Boy CPU Manual](http://marc.rawer.de/Gameboy/Docs/GBCPUman.pdf)
- [Pan Docs - Game Boy Documentation](https://gbdev.io/pandocs/)
- [Game Boy CPU Instruction Set](https://gbdev.io/pandocs/CPU_Instruction_Set.html)
- [Game Boy Opcodes Reference](https://pastraiser.com/cpu/gameboy/gameboy_opcodes.html)
- [Game Boy Development Community](https://gbdev.io/)
- [Blargg's Test ROMs](https://github.com/retrio/gb-test-roms)

## Licencia

MIT License - Ver [LICENSE.txt](LICENSE.txt) para más detalles.

## Reconocimientos

- Comunidad de desarrolladores de Game Boy
- Autores de las ROMs de test utilizadas

---

**Nota**: Este es un proyecto educativo en desarrollo. No está destinado para uso comercial y se basa en documentación pública del hardware Game Boy.