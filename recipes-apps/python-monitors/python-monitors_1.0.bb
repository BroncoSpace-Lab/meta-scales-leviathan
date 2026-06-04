SUMMARY = "SCALES EPS PIV and Temp services"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = " \
    file://eps_temp_telemetry.py \
    file://eps_piv_telemetry.py \
    file://eps_temp_telemetry.service \
    file://eps_piv_telemetry.service \
"

S = "${WORKDIR}"

inherit systemd

# Optional but explicit:
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "eps_temp_telemetry.service eps_piv_telemetry.service"
SYSTEMD_AUTO_ENABLE = "enable"

RDEPENDS:${PN} = "python3-core python3-modules python3-smbus i2c-tools"
# RDEPENDS:${PN} += "python3-smbus2"
# RDEPENDS:${PN} += "screen"     # if your units launch inside 'screen'

do_install() {
    # Scripts
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/eps_temp_telemetry.py ${D}${bindir}/eps_temp_telemetry
    install -m 0755 ${WORKDIR}/eps_piv_telemetry.py  ${D}${bindir}/eps_piv_telemetry

    # Systemd units (IMPORTANT: keep .service suffix and correct dir)
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/eps_temp_telemetry.service ${D}${systemd_system_unitdir}/
    install -m 0644 ${WORKDIR}/eps_piv_telemetry.service  ${D}${systemd_system_unitdir}/
}

FILES:${PN} += " \
    ${bindir}/eps_temp_telemetry \
    ${bindir}/eps_piv_telemetry \
    ${systemd_system_unitdir}/eps_temp_telemetry.service \
    ${systemd_system_unitdir}/eps_piv_telemetry.service \
"

