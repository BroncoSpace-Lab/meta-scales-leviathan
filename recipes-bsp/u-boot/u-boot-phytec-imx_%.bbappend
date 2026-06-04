FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI += "file://0001-uboot_and_usd_fix.patch"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .= "imx8qxp-scales-leviathan"
COMPATIBLE_MACHINE .= ")$"
