LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = " \
    file://10-eth0.network \
    file://10-eth1.network \
"

S = "${WORKDIR}"

do_install() {
    install -d ${D}${sysconfdir}/systemd/network

    # Install eth0 config
    install -m 0644 ${WORKDIR}/10-eth0.network \
        ${D}${sysconfdir}/systemd/network/10-eth0.network

    # Install eth1 config
    install -m 0644 ${WORKDIR}/10-eth1.network \
        ${D}${sysconfdir}/systemd/network/10-eth1.network

    # Remove BSP defaults so they don’t override
    rm -f ${D}${nonarch_libdir}/systemd/network/10-eth0.network
    rm -f ${D}${nonarch_libdir}/systemd/network/10-eth1.network
}

FILES:${PN} += " \
    ${sysconfdir}/systemd/network/10-eth0.network \
    ${sysconfdir}/systemd/network/10-eth1.network \
"
