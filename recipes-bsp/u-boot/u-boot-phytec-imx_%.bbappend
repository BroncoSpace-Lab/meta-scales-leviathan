FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI += "file://0001-uboot_and_usd_fix.patch"
SRC_URI += "file://0002-imx8qxp-set-default-kernel-console-to-ttyLP2.patch"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .= "imx8qxp-scales-leviathan"
COMPATIBLE_MACHINE .= ")$"
