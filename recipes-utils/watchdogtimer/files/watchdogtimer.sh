#!/bin/sh

GPIO=20
GPIO_CHIP=2

# Ping loop — 3.3v Square Wave, 1Hz, 50% Duty Cycle 
while true; do
    gpioset --chip gpiochip$GPIO_CHIP --hold-period 500ms --toggle=0 $GPIO=1
    echo "Pet High"

    gpioset --chip gpiochip$GPIO_CHIP --hold-period 500ms --toggle=0 $GPIO=0
    echo "Pet Low"

done

