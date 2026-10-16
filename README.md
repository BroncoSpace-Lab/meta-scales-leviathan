# meta-scales-leviathan

Yocto layer for the **Scales Flight Computer (Leviathan)**: a custom carrier board for the
**PHYTEC phyCORE-i.MX8X** System-on-Module (NXP i.MX8QXP). It extends the PHYTEC i.MX8X BSP
(`BSP-Yocto-NXP-i.MX8X-PD24.1.y`) with a Leviathan machine definition, device tree, U-Boot changes,
static networking, and the F´ (F Prime) `ImxDeployment` flight software.

| | |
|---|---|
| **Layer name** | `meta-scales-leviathan` |
| **Machine** | `imx8qxp-scales-leviathan` |
| **Yocto release** | `scarthgap` (`LAYERSERIES_COMPAT`) |
| **Layer priority** | `999` |
| **Target image** | `phytec-headless-image` |
| **Distro** | `ampliphy-vendor` |
| **Repository** | <https://github.com/BroncoSpace-Lab/meta-scales-leviathan> |

---

## Table of Contents

1. [Directory Layout](#directory-layout)
2. [Layer Contents](#layer-contents)
3. [Dependencies](#dependencies)
4. [Building the Image in the i.MX8X BSP](#building-the-image-in-the-imx8x-bsp)
5. [Flashing and Booting](#flashing-and-booting)
6. [Network and Access Reference](#network-and-access-reference)
7. [Known Issues / TODO](#known-issues--todo)
8. [License and Maintainer](#license-and-maintainer)

---

## Directory Layout

```
meta-scales-leviathan/
├── conf/
│   ├── layer.conf                               # Layer registration (scarthgap, priority 999)
│   └── machine/
│       └── imx8qxp-scales-leviathan.conf        # Leviathan machine definition
├── recipes-bsp/
│   └── u-boot/
│       ├── u-boot-phytec-imx_%.bbappend         # Applies the Leviathan U-Boot patch
│       └── files/
│           └── 0001-micro_usd_fdt_ttyLP2_edit.patch
├── recipes-core/
│   ├── base-files/
│   │   ├── base-files_%.bbappend                # Overrides base-files with the files/ dir
│   │   └── files/issue.net                      # Scales Flight Computer login banner
│   ├── images/
│   │   └── phytec-headless-image.bbappend       # Adds Leviathan packages to the image
│   └── imx-static-ip/
│       ├── imx-static-ip.bb                     # Static IP config for eth0/eth1
│       └── files/10-eth0.network, 10-eth1.network
├── recipes-kernel/
│   └── linux/
│       ├── linux-phytec-imx_%.bbappend          # Applies the Leviathan device tree patch
│       └── files/
│           └── 0001-leviathan_dts.patch
├── recipes-utils/
│   └── imx-deployment/
│       ├── imx-deployment_1.0.bb                # Installs F´ ImxDeployment + sequences
│       └── files/
│           ├── ImxDeployment                    # Prebuilt aarch64 F´ deployment binary
│           ├── imx-deployment.service           # systemd unit (auto-start)
│           └── *.seq / *.bin                    # F´ command sequences (source / compiled)
├── COPYING.MIT
└── README.md
```

---

## Layer Contents

### Machine configuration: `conf/machine/imx8qxp-scales-leviathan.conf`

Defines the `imx8qxp-scales-leviathan` machine, based on PHYTEC's phyCORE-i.MX8X
(`conf/machine/include/phyimx8x.inc`) and NXP's `imx-base.inc`.

| Setting | Value |
|---|---|
| SoC / overrides | i.MX8QXP C0 (`mx8qxp:mx8qxpc0:`) |
| Board type | `phycore-imx8x-1` (PCM-942 / PCM-065) |
| Machine features | `emmc pci can wifi bluetooth` |
| Kernel device tree | `freescale/imx8qxp-scales-leviathan.dtb` plus the PHYTEC PCM-942 overlays (`.dtbo`) |
| Bootloader | `u-boot-phytec-imx` packaged as `imx-boot` |
| U-Boot configs | `sd` (default): `phycore-imx8x_defconfig,sdcard`; `fspi`: `phycore-imx8x_defconfig` |
| imx-mkimage target | `flash` (SD) or `flash_flexspi` (FSPI) |
| ATF platform | `imx8qx` |
| Kernel cmdline | `console=ttyLP2,115200 root=/dev/mmcblk1p2 fsck.repair=yes rootwait rw` |

### Linux kernel: `recipes-kernel/linux/`

`linux-phytec-imx_%.bbappend` applies **`0001-leviathan_dts.patch`**, which:

- Adds `arch/arm64/boot/dts/freescale/imx8qxp-scales-leviathan.dts` (built on
  `imx8qxp-phycore-som-emmc.dtsi`).
- Registers `imx8qxp-scales-leviathan.dtb` in the freescale DTS `Makefile`.

Main hardware described in the Leviathan device tree:

| Node | Purpose |
|---|---|
| `chosen` | Console on `lpuart2` (`ttyLP2`, 115200) with earlycon |
| Fixed regulators | 1.8 V, 3.3 V, and mPCIe 3.3 V rails |
| `i2c1` | TPS6598x USB-PD controller, RTC (disabled), EEPROM, SGTL5000 audio codec, GPIO expander |
| `lpuart0` | Enabled UART |
| `usdhc2` | microSD card slot |
| `fec2` + `mdio` | Second Ethernet MAC and PHY |
| `usbphy1` | USB PHY |
| Display controller / LVDS | Disabled (headless board) |

### U-Boot: `recipes-bsp/u-boot/`

`u-boot-phytec-imx_%.bbappend` applies **`0001-micro_usd_fdt_ttyLP2_edit.patch`**:

| File | Change |
|---|---|
| `arch/arm/dts/imx8qxp-phycore-som.dtsi` | microSD (`usdhc2`): `no-uhs`, `no-sd-highspeed`, `max-frequency = 25 MHz` |
| `include/configs/phycore_imx8x.h` | `console=ttyLP2` (was `ttyLP0`) |
| | `fdt_file=imx8qxp-scales-leviathan.dtb` (was `imx8qxp-phytec-pcm-942.dtb`) |
| | `ipaddr=10.3.2.12`, `serverip=10.3.2.13` (host PC) for netboot |

Both the kernel and U-Boot bbappends limit `COMPATIBLE_MACHINE` to `imx8qxp-scales-leviathan`.

### Image customization: `recipes-core/images/phytec-headless-image.bbappend`

Adds the Leviathan packages to PHYTEC's `phytec-headless-image` (from `meta-ampliphy`):

```
IMAGE_INSTALL:append = " imx-static-ip"
IMAGE_INSTALL:append = " imx-deployment"
```

### Static networking: `recipes-core/imx-static-ip/`

Installs systemd-networkd configs to `/etc/systemd/network/` and removes the BSP's default
`10-eth*.network` files so they can't override these settings.

| Interface | Address | DHCP | IPv6 RA / link-local |
|---|---|---|---|
| `eth0` | `10.3.2.10/24` | off | off |
| `eth1` | `10.3.2.11/24` | off | off |

### F´ flight software: `recipes-utils/imx-deployment/`

Installs the prebuilt **`ImxDeployment`** F´ (NASA/JPL F Prime) binary (ELF 64-bit, aarch64) and its
command sequences to `/root`, and adds a systemd service that starts automatically.

**`imx-deployment.service`**

```ini
ExecStart=/root/ImxDeployment -a 0.0.0.0 -p 50000
WorkingDirectory=/root
Restart=on-failure
RestartSec=5
WantedBy=multi-user.target
```

**Command sequences** (each `.seq` is the readable source; the `.bin` beside it is the compiled
sequence):

| Sequence | What it does |
|---|---|
| `save-png` | Takes a PNG with the Jetson Lucid camera, then downlinks `image.png` to the i.MX |
| `snap-n-save` | Same as `save-png` |
| `run-ml` | Sets the ML model to `resnet_inference`, sets the inference path to `./Images`, runs multi-inference |
| `test-resnet` | Same as `run-ml`, but runs on `../test-imagery` |
| `demo` | Snapshot → downlink → ResNet multi-inference on `./Images` → clears `./Images` |
| `batch-send-img` | Tars `./Images` on the Jetson, downlinks it, then forwards `Images.tgz` from the i.MX |
| `Zip-n-send-img` | Same as `batch-send-img`, but tars `/Images` |

### Login banner: `recipes-core/base-files/`

`base-files_%.bbappend` adds this layer's `files/` directory to the search path, so `issue.net`
(the "SCALES FLIGHT COMPUTER — Powered by F´ Flight Software" banner) replaces the default.

---

## Dependencies

The layer itself only declares `LAYERDEPENDS = "core"`. However, the machine and recipes depend on
the PHYTEC i.MX8X BSP (PD24.1.y) layer stack, which is already in the BSP's `build/conf/bblayers.conf`:

| Layer | Needed for |
|---|---|
| `poky/meta`, `poky/meta-poky` | OpenEmbedded core |
| `meta-openembedded/*` (oe, networking, python, multimedia, filesystems, perl, gnome) | General userspace |
| `meta-arm/meta-arm`, `meta-arm/meta-arm-toolchain` | Arm toolchain / TF-A |
| `meta-freescale`, `meta-freescale-3rdparty`, `meta-freescale-distro` | `imx-base.inc`, imx-boot, NXP firmware |
| `meta-imx/meta-imx-bsp`, `meta-imx-sdk`, `meta-imx-ml` | NXP i.MX BSP |
| `meta-phytec` | `phyimx8x.inc`, `linux-phytec-imx`, `u-boot-phytec-imx` |
| `meta-ampliphy` | `ampliphy-vendor` distro, `phytec-headless-image` |
| `meta-qt6`, `meta-qt6-phytec`, `meta-browser`, `meta-clang`, `meta-security`, `meta-virtualization`, `meta-rauc`, `meta-nxp-demo-experience` | Rest of the BSP manifest |

---

## Building the Image in the i.MX8X BSP

All paths below are relative to the BSP root:

```
~/BSP-Yocto-NXP-i.MX8X-PD24.1.y/yocto/
├── build/
└── sources/
    ├── meta-phytec/
    ├── meta-ampliphy/
    ├── meta-scales-leviathan/   <-- this layer
    └── poky/ ...
```

### 1. Get the layer

Clone it into the BSP's `sources/` folder if it isn't there yet:

```bash
cd ~/BSP-Yocto-NXP-i.MX8X-PD24.1.y/yocto/sources
git clone https://github.com/BroncoSpace-Lab/meta-scales-leviathan.git
```

### 2. Set up the build environment

```bash
cd ~/BSP-Yocto-NXP-i.MX8X-PD24.1.y/yocto
source sources/poky/oe-init-build-env build
```

This puts you in `yocto/build/`, with `bitbake` on your `PATH`.

### 3. Install the SCALES build configuration

The [`scales-firmware`](https://github.com/BroncoSpace-Lab/scales-firmware) repository has a
ready-made `conf/` folder (`local.conf` + `bblayers.conf`) that adds this layer and selects the
Leviathan machine. Copy it over the BSP's build configuration, replacing the existing `conf/`:

```bash
cp -r ~/scales-firmware/conf ~/BSP-Yocto-NXP-i.MX8X-PD24.1.y/yocto/build/
```

The main settings it provides:

```bash
# conf/local.conf
MACHINE ?= "imx8qxp-scales-leviathan"
DISTRO  ?= "ampliphy-vendor"
ACCEPT_FSL_EULA = "1"

# conf/bblayers.conf
BBLAYERS += "${TOPDIR}/../sources/meta-scales-leviathan"
```

Check that the layer and machine are active:

```bash
bitbake-layers show-layers | grep scales
bitbake -e | grep '^MACHINE='
```

<details>
<summary>Alternative: set it up by hand on a stock BSP <code>conf/</code></summary>

```bash
bitbake-layers add-layer ../sources/meta-scales-leviathan
```

Then in `conf/local.conf`:

```bash
MACHINE = "imx8qxp-scales-leviathan"
DISTRO  = "ampliphy-vendor"
ACCEPT_FSL_EULA = "1"
```

</details>

### 4. Build

```bash
bitbake phytec-headless-image
```

The image is written to:

```
build/deploy-ampliphy-vendor/images/imx8qxp-scales-leviathan/
```

### Optional: rebuild individual parts

```bash
bitbake -c cleansstate linux-phytec-imx  && bitbake linux-phytec-imx     # kernel / DTB
bitbake -c cleansstate u-boot-phytec-imx && bitbake imx-boot             # bootloader
bitbake -c cleansstate imx-deployment    && bitbake imx-deployment       # F´ deployment
```

---

## Flashing and Booting

1. Write the image to a microSD card (replace `/dev/sdX` with your card; check with `lsblk` first):

   ```bash
   cd build/deploy-ampliphy-vendor/images/imx8qxp-scales-leviathan/
   sudo bmaptool copy phytec-headless-image-imx8qxp-scales-leviathan.rootfs.wic.xz /dev/sdX
   ```

2. Put the card in the Leviathan board and set it to boot from SD.
3. Open the serial console (`ttyLP2`) at **115200 8N1**, for example `picocom -b 115200 /dev/ttyUSB0`.
4. Log in as `root`. The local config has `debug-tweaks` enabled, so there's no password.
5. Check that the flight software is running:

   ```bash
   systemctl status imx-deployment
   ```

---

## Network and Access Reference

| Item | Value |
|---|---|
| `eth0` (Linux) | `10.3.2.10/24` |
| `eth1` (Linux) | `10.3.2.11/24` |
| U-Boot `ipaddr` | `10.3.2.12` |
| U-Boot `serverip` (host PC) | `10.3.2.13` |
| F´ ImxDeployment | listens on `0.0.0.0:50000` |
| Serial console | `ttyLP2`, 115200 baud |
| Root filesystem | `/dev/mmcblk1p2` |

---

## License and Maintainer

- **License:** MIT (see `COPYING.MIT`)
- **Maintainer:** Luca Lanzi (BroncoSpace Lab)
