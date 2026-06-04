#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import time

# Import I2C library
try:
    from smbus2 import SMBus  # preferred if available
except ImportError:
    from smbus import SMBus   # fallback if only smbus is present

# ---------------- Configuration ----------------
I2C_BUS = 0                 # /dev/i2c-0
I2C_TEMP_OBC_ADDR   = 0x19  # MCP9808 on OBC PSU
I2C_TEMP_PERIF_ADDR = 0x1A  # MCP9808 on PERIF PSU
I2C_TEMP_JET_ADDR   = 0x1B  # MCP9808 on JETSON PSU

# MCP9808 register addresses
REG_AMBIENT_TEMP = 0x05

# ---------------- Helper Functions ----------------
def read_temperature_c(bus, addr):
    """
    Read and convert temperature from MCP9808 to Celsius.
    Correct 16-bit big-endian format; 0.0625 °C per LSB.
    """
    try:
        hi, lo = bus.read_i2c_block_data(addr, REG_AMBIENT_TEMP, 2)
    except AttributeError:
        hi = bus.read_byte_data(addr, REG_AMBIENT_TEMP)
        lo = bus.read_byte_data(addr, REG_AMBIENT_TEMP + 1)

    raw = (hi << 8) | lo
    val = raw & 0x0FFF
    if raw & 0x1000:  # negative temperature
        val -= 1 << 13
    return val * 0.0625

# ---------------- Main Loop ----------------
def main():
    bus = SMBus(I2C_BUS)
    try:
        while True:
            try:
                t_obc   = read_temperature_c(bus, I2C_TEMP_OBC_ADDR)
                t_perif = read_temperature_c(bus, I2C_TEMP_PERIF_ADDR)
                t_jet   = read_temperature_c(bus, I2C_TEMP_JET_ADDR)

                # Single-line print (overwrite each update). Add spaces to clear leftovers.
                line = f"OBC EPS: {t_obc:6.2f} °C | PERIF EPS: {t_perif:6.2f} °C | JETSON EPS: {t_jet:6.2f} °C"
                print(line + "   ", end="\r", flush=True)

            except OSError as e:
                print(f"I2C communication error: {e}")

            time.sleep(0.5)

    except KeyboardInterrupt:
        print("\nStopping temperature monitoring.")
    finally:
        bus.close()

if __name__ == "__main__":
    main()

