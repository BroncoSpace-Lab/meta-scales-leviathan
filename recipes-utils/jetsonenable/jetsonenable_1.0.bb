SUMMARY = "Jetson Power Toggler Program"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://jetsonenable.sh \
           file://jetsonenable.service"

S = "${WORKDIR}"

inherit systemd

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/jetsonenable.sh ${D}${bindir}/jetsonenable.sh

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/jetsonenable.service ${D}${systemd_system_unitdir}/jetsonenable.service
}

SYSTEMD_SERVICE:${PN} = "jetsonenable.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

