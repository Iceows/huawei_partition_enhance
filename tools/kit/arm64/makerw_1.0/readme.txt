
# Welcome to the one and only, the original MakeRW/ro2rw v1.0 by lebigmac
# =======================================================================
# Also known as: RO-to-RW, ro-2-rw-Transformer, read-only-2-read/write converter, Read-Only Unlocker, READ-ONLY REMOVER, Block-Unsharer Pro, Shared_Blocks Remover Pro, RO2RW enhancer, EXT4-RW, EROFSRW, EROFS-RW, ERWFS, EROFS NUKER, EROFS-2-EXT4 CONVERTER, F2FSRW, F2FS-RW, F2FS TERMINATOR, F2FS-2-EXT4 CONVERTER, FORCE-READ-WRITE ENABLER, FORCE-RW, THE-REAL-RW, REAL-RW, Partition Resizer Pro, EXT4 Partition creator, Make_RW, Make-RW, ROTORW, Make_EXT4 & many more aliases...

# Please feel free to visit the brand new official MakeRW/ro2rw by lebigmac Homepage @
# ====================================================================================
#                              http://lebigmac.2ix.ch
# ====================================================================================

# DISCLAIMER:
# ===========
# By viewing and using this proprietary software you hereby agree to the following terms:
# The end-user shall NOT steal, imitate, hack, crack, decompile, hex-edit, rebrand, rename, delete, copy, cut & paste, rewrite, modify or abuse this proprietary software (or any parts thereof) without the original author's prior knowledge and explicit written consent! Thank you!
# This proprietary software was only provided for free for the end-user's own personal, educational, non-commercial and legal use!
# If you (the end-user) want to use this proprietary software (or any parts thereof) for commercial purposes, please rent a commercial usage license at the official MakeRW/ro2rw website (see link above). Thank you very much for your support!

# DONATIONWARE:
# =============
# This proprietary software is donationware which means it can only exist if the end-user decides to donate to the original developer lebigmac for all his efforts that went into creating this unique and original software from scratch for the past 2 years. To donate please visit the official link above and click the [ DONATE ] button. Thank you very much! Your precious donation is very much appreciated and will help me to pay my electricity bills so I can continue working on this project & add new features & keep it ad-free & create many more amazing programs such as this one!

# DESCRIPTION:
# ============
# Transform .img file from ro (read-only) to rw (read/write-able)
# Compatible with: EXT4 (shared_blocks), EROFS, F2FS (coming soon! Scheduled release date: 1st quartal 2023)

# Original author: lebigmac
# Credits: lebigmac, BrePro1, Yuki1001, thka2016, Kolibass
# Creation date: February 2021
# Last updated: January 2023

# INSTALLATION INSTRUCTIONS:
# ==========================
# Extract the .zip and copy makerw_1.0 folder into /data/local/tmp/
# Make the makerw binary executable and then unpack your super.img like this (as root):

#   adb shell
#   su
#   cd /data/local/tmp/makerw_1.0
#   bin/lpunpack `realpath /dev/block/by-name/super` img

# Now you are finally ready to convert your read-only sub-partitions to read/write-able ones with the one and only MakeRW/ro2rw by lebigmac!
#   bin/makerw in=img/system.img
#   bin/makerw in=img/vendor.img size=50
#   bin/makerw in=img/product.img
#   bin/makerw in=img/odm.img

# Have fun converting your read-only partitions into fully read/write-able partitions!

