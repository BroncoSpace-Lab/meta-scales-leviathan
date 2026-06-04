FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += "file://0001-leviathan_dts.patch"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .= "imx8qxp-scales-leviathan"
COMPATIBLE_MACHINE .= ")$"
