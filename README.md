# My GameBoy Emulator

## Introduction

Welcome to **My-GameBoy-Emulator**! This is a custom-built GameBoy emulator written entirely in Java. It accurately emulates the core hardware of the original Nintendo GameBoy, allowing you to play classic titles right on your computer. Whether you want to relive your childhood memories playing classic games or you're a developer interested in emulation architecture, this project has something for you.

## Features

- **Accurate Cartridge Support**: Implements Memory Bank Controllers including **MBC1**, **MBC3**, and **MBC5**, ensuring high compatibility with popular GameBoy titles like *Pokémon Red*, *Tetris*, and more.
- **Audio Processing Unit (APU)**: Enjoy the classic 8-bit sound! The emulator features an authentic APU with square wave generation and a High-Pass Filter for accurate audio reproduction.
- **Save States**: Never lose your progress. The emulator supports battery-backed RAM via standard `.sav` files, automatically loading and saving your game data.
- **Customizable Configuration**: A dedicated configuration window allows you to easily map keyboard controls and select your preferred audio output device. It works out-of-the-box on Linux/ALSA via Java Sound.
- **Fullscreen Mode**: Immerse yourself in the game by toggling fullscreen mode.

## How to Run

Running the emulator is simple! You just need to have Java installed on your computer.

### Prerequisites
- **Java Runtime Environment (JRE) 11 or higher** installed on your system.

### Running the Emulator
1. Download the latest release `.jar` file from the repository, or compile the project from source.
2. Double-click the `.jar` file to launch the emulator GUI.
3. Alternatively, you can run it from the command line:
   ```bash
   java -jar My-GameBoy-Emulator.jar
   ```
4. Once the emulator opens, use the interface to load a valid GameBoy ROM file (`.gb`).
5. Your game will start immediately!

*Note: You must legally own the GameBoy games you play on this emulator. ROMs are not provided with this project.*

## Controls

The emulator supports customizable keyboard controls. You can change these at any time via the **Configuration** menu.

### Default Mapping:
- **D-Pad**: Arrow Keys (Up, Down, Left, Right)
- **A Button**: `Z`
- **B Button**: `X`
- **Start**: `Enter`
- **Select**: `Shift`
- **Toggle Fullscreen**: `F11` (or via the View menu)

## Technical Details

For developers and emulation enthusiasts, this emulator provides a clean, object-oriented implementation of the GameBoy architecture in Java.

### Architecture Overview
- **CPU**: Custom implementation of the Sharp LR35902 (similar to Z80/8080). Handles instruction fetching, decoding, and execution.
- **PPU (Pixel Processing Unit)**: Renders background, window, and sprites (OAM) to a Java Swing `BufferedImage`.
- **APU**: Synthesizes audio using Java Sound API. Features square wave channels and accurately processes the GameBoy's audio registers and high-pass filtering.
- **Memory Management Unit (MMU)**: Maps the 16-bit address space to internal RAM, Video RAM, Cartridge ROM/RAM, and Memory Mapped I/O registers.
- **GUI**: Built using standard Java Swing components for a lightweight, cross-platform interface.

### Building from Source
If you want to compile the project yourself:
1. Clone the repository: 
   ```bash
   git clone https://github.com/yourusername/My-GameBoy-Emulator.git
   ```
2. Open the project in your favorite IDE (IntelliJ IDEA, Eclipse, etc.) or use your build tool to compile the Java source files.
3. Run the `Main` class to start the emulator.

## License

MIT License - See [LICENSE.txt](LICENSE.txt) for details.
