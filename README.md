# My GameBoy Emulator

[Español](#español) | [English](#english)

<a name="english"></a>
## English
[Read in Spanish](#español)

## Introduction

Welcome to **My-GameBoy-Emulator**! This is a custom-built GameBoy emulator written entirely in Java. It accurately emulates the core hardware of the original Nintendo GameBoy, allowing you to play classic titles right on your computer.

## Features

- **Accurate Cartridge Support**: Implements Memory Bank Controllers including **MBC1**, **MBC3**, and **MBC5**.
- **Audio Processing Unit (APU)**: Authentic 8-bit sound emulation.
- **Save States**: Supports battery-backed RAM via standard `.sav` files.
- **Customizable Configuration**: Dedicated configuration window for key mapping.
- **Fullscreen Mode**: Toggle fullscreen for better immersion.

## How to Run

Running the emulator is simple! You just need to have Java installed on your computer.

### Prerequisites
- **Java Runtime Environment (JRE) 11 or higher** installed on your system.

### Running the Emulator
1. Download the latest release `.jar` file or compile the project from source.
2. Launch it: `java -jar My-GameBoy-Emulator.jar`
3. Load a valid GameBoy ROM file (`.gb`).

*Note: This repository includes some ROMs used for testing the emulator's functionality. However, for legal reasons, official commercial game ROMs are not included.*

## Controls

- **D-Pad**: Arrow Keys
- **A Button**: `Z`, **B Button**: `X`
- **Start**: `Enter`, **Select**: `Shift`
- **Toggle Fullscreen**: `F11`

## Technical Details

- **CPU**: Custom implementation of the Sharp LR35902.
- **PPU**: Renders background, window, and sprites using Java Swing.
- **APU**: Synthesizes audio using Java Sound API.
- **MMU**: Manages the 16-bit address space.

---

<a name="español"></a>
## Español
[Ver en inglés](#english)

## Introducción

¡Bienvenido a **My-GameBoy-Emulator**! Este es un emulador de GameBoy desarrollado totalmente en Java. Emula con precisión el hardware original de la Nintendo GameBoy, permitiéndote jugar títulos clásicos en tu computadora.

## Características

- **Soporte preciso de cartuchos**: Implementa controladores de memoria bancaria como **MBC1**, **MBC3** y **MBC5**.
- **Unidad de procesamiento de audio (APU)**: Emulación auténtica de sonido de 8 bits.
- **Save States**: Soporta RAM respaldada por batería mediante archivos `.sav`.
- **Configuración personalizable**: Ventana dedicada para mapeo de teclas.
- **Modo pantalla completa**: Cambia a pantalla completa para una mejor inmersión.

## Cómo ejecutar

¡Ejecutar el emulador es sencillo! Solo necesitas tener Java instalado.

### Requisitos previos
- **Java Runtime Environment (JRE) 11 o superior**.

### Cómo ejecutar el emulador
1. Descarga el archivo `.jar` o compila el proyecto desde el código fuente.
2. Ejecútalo: `java -jar My-GameBoy-Emulator.jar`
3. Carga un archivo ROM de GameBoy válido (`.gb`).

*Nota: Este repositorio incluye algunas ROMs utilizadas para comprobar las funcionalidades del emulador. Sin embargo, por motivos legales, no se incluyen ROMs de juegos oficiales.*

## Controles

- **D-Pad**: Teclas de flecha
- **Botón A**: `Z`, **Botón B**: `X`
- **Start**: `Enter`, **Select**: `Shift`
- **Pantalla completa**: `F11`

## Detalles Técnicos

- **CPU**: Implementación personalizada del Sharp LR35902.
- **PPU**: Renderiza fondo, ventana y sprites usando Java Swing.
- **APU**: Sintetiza audio usando Java Sound API.
- **MMU**: Gestiona el espacio de direcciones de 16 bits.
