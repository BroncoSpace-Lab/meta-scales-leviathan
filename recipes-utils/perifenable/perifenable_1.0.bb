SUMMARY = "Perif. Power Toggler Program"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://perifenable.sh \
           file://perifenable.service"

S = "${WORKDIR}"

inherit systemd

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/perifenable.sh ${D}${bindir}/perifenable.sh

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/perifenable.service ${D}${systemd_system_unitdir}/perifenable.service
}

SYSTEMD_SERVICE:${PN} = "perifenable.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

