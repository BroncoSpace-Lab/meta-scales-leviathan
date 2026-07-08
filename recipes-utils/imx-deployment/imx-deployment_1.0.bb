LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = " \
    file://ImxDeployment \
    file://imx-deployment.service \
    file://batch-send-img.bin \
    file://demo.bin \
    file://run-ml.bin \
    file://save-png.bin \
    file://snap-n-save.bin \
    file://test-resnet.bin \
    file://Zip-n-send-img.bin \
"

S = "${WORKDIR}"

inherit systemd

do_install() {
    install -d ${D}/root

    install -m 0755 ${WORKDIR}/ImxDeployment ${D}/root/ImxDeployment

    install -m 0644 ${WORKDIR}/batch-send-img.bin ${D}/root/batch-send-img.bin
    install -m 0644 ${WORKDIR}/demo.bin ${D}/root/demo.bin
    install -m 0644 ${WORKDIR}/run-ml.bin ${D}/root/run-ml.bin
    install -m 0644 ${WORKDIR}/save-png.bin ${D}/root/save-png.bin
    install -m 0644 ${WORKDIR}/snap-n-save.bin ${D}/root/snap-n-save.bin
    install -m 0644 ${WORKDIR}/test-resnet.bin ${D}/root/test-resnet.bin
    install -m 0644 ${WORKDIR}/Zip-n-send-img.bin ${D}/root/Zip-n-send-img.bin

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/imx-deployment.service ${D}${systemd_system_unitdir}/imx-deployment.service
}

SYSTEMD_SERVICE:${PN} = "imx-deployment.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

FILES:${PN} += " \
    /root/ImxDeployment \
    /root/*.bin \
    ${systemd_system_unitdir}/imx-deployment.service \
"
