FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI += "file://0001-micro_usd_fdt_ttyLP2_edit.patch"

COMPATIBLE_MACHINE  = "^("
COMPATIBLE_MACHINE .= "imx8qxp-scales-leviathan"
COMPATIBLE_MACHINE .= ")$"
