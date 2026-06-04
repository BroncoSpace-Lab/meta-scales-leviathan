#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
INA260AIPWR I2C monitor
Reads voltage (V), current (A), and power (W)
from up to three INA260 sensors and prints values on one line.
"""

import time

# Import I2C library
try:
    from smbus2 import SMBus
except ImportError:
    from smbus import SMBus

# ---------------- Configuration ----------------
I2C_BUS = 0                # I2C bus number (/dev/i2c-0, /dev/i2c-1, etc.)
INA260_OBC_ADDR   = 0x41   # Example addresses (adjust to your wiring)
INA260_PERIF_ADDR = 0x45
INA260_JET_ADDR   = 0x40

# INA260 register addresses (see datasheet)
REG_CURRENT = 0x01
REG_BUS_VOLTAGE = 0x02
REG_POWER = 0x03

# ---------------- Helper Functions ----------------
def read_u16(bus, addr, reg):
    """Read 16-bit register (big-endian)"""
    hi = bus.read_byte_data(addr, reg)
    lo = bus.read_byte_data(addr, reg + 1)
    return (hi << 8) | lo

def read_voltage_v(bus, addr):
    """Read bus voltage in volts."""
    raw = read_u16(bus, addr, REG_BUS_VOLTAGE)
    return raw * 1.25e-3  # 1.25 mV/bit per datasheet

def read_current_a(bus, addr):
    """Read current in amperes."""
    raw = read_u16(bus, addr, REG_CURRENT)
    # Convert two's complement 16-bit value
    if raw & 0x8000:
        raw -= 1 << 16
    return raw * 1.25e-3  # 1.25 mA/bit per datasheet

def read_power_w(bus, addr):
    """Read power in watts."""
    raw = read_u16(bus, addr, REG_POWER)
    return raw * 10e-3  # 10 mW/bit per datasheet

# ---------------- Main Loop ----------------
def main():
    bus = SMBus(I2C_BUS)
    try:
        while True:
            try:
                v_obc = read_voltage_v(bus, INA260_OBC_ADDR)
                i_obc = read_current_a(bus, INA260_OBC_ADDR)
                p_obc = read_power_w(bus, INA260_OBC_ADDR)

                v_perif = read_voltage_v(bus, INA260_PERIF_ADDR)
                i_perif = read_current_a(bus, INA260_PERIF_ADDR)
                p_perif = read_power_w(bus, INA260_PERIF_ADDR)

                v_jet = read_voltage_v(bus, INA260_JET_ADDR)
                i_jet = read_current_a(bus, INA260_JET_ADDR)
                p_jet = read_power_w(bus, INA260_JET_ADDR)

                # Single-line formatted output
                print(
                    f"OBC: {v_obc:6.2f} V {i_obc:6.2f} A {p_obc:6.2f} W | "
                    f"PERIF: {v_perif:6.2f} V {i_perif:6.2f} A {p_perif:6.2f} W | "
                    f"JETSON: {v_jet:6.2f} V {i_jet:6.2f} A {p_jet:6.2f} W",
                    end="\r", flush=True
                )

            except OSError as e:
                print(f"I2C communication error: {e}")
                time.sleep(1)

            time.sleep(0.5)

    except KeyboardInterrupt:
        print("\nStopping INA260 monitoring.")
    finally:
        bus.close()

if __name__ == "__main__":
    main()

