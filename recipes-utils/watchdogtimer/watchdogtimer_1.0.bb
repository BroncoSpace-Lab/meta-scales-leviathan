SUMMARY = "GPIO watchdog timer pinger"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://watchdogtimer.sh \
           file://watchdogtimer.service"

S = "${WORKDIR}"

inherit systemd

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/watchdogtimer.sh ${D}${bindir}/watchdogtimer.sh

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/watchdogtimer.service ${D}${systemd_system_unitdir}/watchdogtimer.service
}

SYSTEMD_SERVICE:${PN} = "watchdogtimer.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

