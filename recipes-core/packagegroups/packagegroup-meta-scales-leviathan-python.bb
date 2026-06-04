SUMMARY = "meta-scales-leviathan: full-ish Python 3 userland"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit packagegroup
ALLOW_EMPTY:${PN} = "1"
PACKAGE_ARCH = "allarch"

RDEPENDS:${PN} = "\
    python3 \
    python3-modules \
    python3-pip \
    python3-setuptools \
    python3-venv \
    ca-certificates \
    "

# Add hardware helpers you care about:
RDEPENDS:${PN} += "i2c-tools python3-smbus"

