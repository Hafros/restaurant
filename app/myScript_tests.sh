#!/bin/bash

#ICONS

ICON_INPUT="../builder/icon.png"

# sips -s format png -r 90 -Z 162 -c 192 192 -r 270 "$ICON_INPUT" --out src/main/res/drawable/ic_custom_launcher.png
convert "$ICON_INPUT" -resize "192x192!" -quality 100 "src/main/res/drawable/ic_custom_launcher.png"
convert -size 192x192 xc:none -draw "roundrectangle 30,30,162,162,96,96" png:- | convert src/main/res/drawable/ic_custom_launcher.png -matte - -compose DstIn -composite src/main/res/drawable/ic_custom_launcher_round.png
convert -size 192x192 xc:none -draw "roundrectangle 1,1,190,190,16,16" png:- | convert src/main/res/drawable/ic_custom_launcher.png -matte - -compose DstIn -composite src/main/res/drawable/ic_custom_launcher.png

# sips -s format png -r 90 -Z 162 -c 192 192 -r 270 "$ICON_INPUT" --out src/main/res/mipmap-xxxhdpi/ic_launcher.png
convert "$ICON_INPUT" -resize "192x192!" -quality 100 "src/main/res/mipmap-xxxhdpi/ic_launcher.png"
convert -size 192x192 xc:none -draw "roundrectangle 30,30,162,162,96,96" png:- | convert src/main/res/mipmap-xxxhdpi/ic_launcher.png -matte - -compose DstIn -composite src/main/res/mipmap-xxxhdpi/ic_launcher_round.png
convert -size 192x192 xc:none -draw "roundrectangle 1,1,190,190,16,16" png:- | convert src/main/res/mipmap-xxxhdpi/ic_launcher.png -matte - -compose DstIn -composite src/main/res/mipmap-xxxhdpi/ic_launcher.png

# sips -s format png -r 90 -Z 122 -c 144 144 -r 270  "$ICON_INPUT" --out src/main/res/mipmap-xxhdpi/ic_launcher.png
convert "$ICON_INPUT" -resize "144x144!" -quality 100 "src/main/res/mipmap-xxhdpi/ic_launcher.png"
convert -size 144x144 xc:none -draw "roundrectangle 20,20,122,122,72,72" png:- | convert src/main/res/mipmap-xxhdpi/ic_launcher.png -matte - -compose DstIn -composite src/main/res/mipmap-xxhdpi/ic_launcher_round.png
convert -size 144x144 xc:none -draw "roundrectangle 1,1,142,142,12,12" png:- | convert src/main/res/mipmap-xxhdpi/ic_launcher.png -matte - -compose DstIn -composite src/main/res/mipmap-xxhdpi/ic_launcher.png

# sips -s format png -r 90 -Z 96 -c 96 96 -r 270 "$ICON_INPUT" --out src/main/res/mipmap-xhdpi/ic_launcher.png
convert "$ICON_INPUT" -resize "96x96!" -quality 100 "src/main/res/mipmap-xhdpi/ic_launcher.png"
convert -size 96x96 xc:none -draw "roundrectangle 1,1,94,94,48,48" png:- | convert src/main/res/mipmap-xhdpi/ic_launcher.png -matte - -compose DstIn -composite src/main/res/mipmap-xhdpi/ic_launcher_round.png
convert -size 96x96 xc:none -draw "roundrectangle 1,1,94,94,8,8" png:- | convert src/main/res/mipmap-xhdpi/ic_launcher.png -matte - -compose DstIn -composite src/main/res/mipmap-xhdpi/ic_launcher.png

# sips -s format png -r 90 -Z 48 -c 48 48 -r 270 "$ICON_INPUT" --out src/main/res/mipmap-mdpi/ic_launcher.png
convert "$ICON_INPUT" -resize "48x48!" -quality 100 "src/main/res/mipmap-mdpi/ic_launcher.png"
convert -size 48x48 xc:none -draw "roundrectangle 1,1,46,46,24,24" -fill white src/main/res/mipmap-mdpi/ic_launcher.png -compose SrcIn -composite src/main/res/mipmap-mdpi/ic_launcher_round.png
convert -size 48x48 xc:none -draw "roundrectangle 1,1,46,46,4,4" -fill white src/main/res/mipmap-mdpi/ic_launcher.png -compose SrcIn -composite src/main/res/mipmap-mdpi/ic_launcher.png

# sips -s format png -r 90 -Z 72 -c 72 72 -r 270 "$ICON_INPUT" --out src/main/res/mipmap-hdpi/ic_launcher.png
convert "$ICON_INPUT" -resize "72x72!" -quality 100 "src/main/res/mipmap-hdpi/ic_launcher.png"
convert -size 72x72 xc:none -draw "roundrectangle 1,1,70,70,36,36" -fill white src/main/res/mipmap-hdpi/ic_launcher.png -compose SrcIn -composite src/main/res/mipmap-hdpi/ic_launcher_round.png
convert -size 72x72 xc:none -draw "roundrectangle 1,1,70,70,6,6" -fill white src/main/res/mipmap-hdpi/ic_launcher.png -compose SrcIn -composite src/main/res/mipmap-hdpi/ic_launcher.png

rm -r apk
mkdir -p apk



../gradlew clean assembleRelease

cp -R build/outputs/apk/release/. apk/

exit 0
