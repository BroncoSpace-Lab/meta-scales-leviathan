#!/bin/sh
set -eu

GPIO_CHIP=${GPIO_CHIP:-2}
GPIO=${GPIO:-18}
CHIP="gpiochip$GPIO_CHIP"

PIDFILE="/tmp/gpioset_${GPIO_CHIP}_${GPIO}.pid"
STATE="unknown"

is_running() {
  [ -f "$PIDFILE" ] && kill -0 "$(cat "$PIDFILE" 2>/dev/null)" 2>/dev/null
}

stop_holder() {
  if is_running; then
    pid="$(cat "$PIDFILE")"
    kill "$pid" 2>/dev/null || true
    wait "$pid" 2>/dev/null || true
  fi
  rm -f "$PIDFILE"
}

hold_value() {
  val="$1" # 0 or 1
  stop_holder
  gpioset --chip "$CHIP" "$GPIO=$val" &
  echo $! > "$PIDFILE"
  STATE="$val"
}

status() {
  if is_running; then
    case "$STATE" in
      1) echo "STATUS: $CHIP:$GPIO ON (held)";;
      0) echo "STATUS: $CHIP:$GPIO OFF (held)";;
      *) echo "STATUS: held (value unknown)";;
    esac
  else
    echo "STATUS: free (not held)"
  fi
}

cleanup() { stop_holder; echo "Bye!"; exit 0; }
trap cleanup INT TERM

hold_value 1
echo "Set ON by default."

while :; do
  echo
  echo "GPIO menu for $CHIP line $GPIO"
  echo "1) ON (hold high)"
  echo "2) OFF (hold low)"
  echo "3) STATUS"
  echo "4) RELEASE"
  echo "5) QUIT"
  printf "> "
  read ans || exit 0
  case "$ans" in
    1) hold_value 1; echo "Set ON.";;
    2) hold_value 0; echo "Set OFF.";;
    3) status;;
    4) stop_holder; echo "Released.";;
    5) cleanup;;
    *) echo "Unknown choice.";;
  esac
done
