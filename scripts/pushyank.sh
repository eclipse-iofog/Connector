#!/usr/bin/env bash

RETURN_STRING="cd /iofog-connector-packaging; fpm -s dir -t deb -n \"iofog-connector${ID}\" -v $VERSION
      -a all --deb-no-default-config-files --after-install debian.sh --after-remove
      remove.sh --before-upgrade upgrade.sh --after-upgrade debian.sh etc usr;"

declare -a UBUNTU_VERS=("precise" "trusty" "utopic" "vivid" "wily" "xenial" "bionic") #Support ubuntu versions
declare -a DEBIAN_VERS=("wheezy" "jessie" "stretch" "buster") #Also appplies to Raspbian, See related for loop
declare -a FEDORA_VERS=("22" "23" "24") #Supported Fedora Versions
declare -a REDHAT_VERS=("6" "7") #Supported Redhat versions


for version in ${UBUNTU_VERS[@]}
do
    RETURN_STRING="${RETURN_STRING} package_cloud yank iofog/iofog-connector/ubuntu/${version} iofog-connector${ID}_${VERSION}_all.deb;"
    RETURN_STRING="${RETURN_STRING} package_cloud push iofog/iofog-connector/ubuntu/${version} iofog-connector${ID}_${VERSION}_all.deb;"
done

for version in "${DEBIAN_VERS[@]}"
do
    RETURN_STRING="${RETURN_STRING} package_cloud yank iofog/iofog-connector/debian/${version} iofog-connector${ID}_${VERSION}_all.deb;"
    RETURN_STRING="${RETURN_STRING} package_cloud push iofog/iofog-connector/debian/${version} iofog-connector${ID}_${VERSION}_all.deb;"
    RETURN_STRING="${RETURN_STRING} package_cloud yank iofog/iofog-connector/raspbian/${version} iofog-connector${ID}_${VERSION}_all.deb;"
    RETURN_STRING="${RETURN_STRING} package_cloud push iofog/iofog-connector/raspbian/${version} iofog-connector${ID}_${VERSION}_all.deb;"
done

RETURN_STRING="$RETURN_STRING cd /iofog-connector-packaging-rpm;
fpm -s dir -t rpm -n \"iofog-connector${ID}\" -v $VERSION -a all --rpm-os 'linux' --after-install
rpm.sh --after-remove remove.sh --before-upgrade upgrade.sh --after-upgrade
rpm.sh etc usr;"

for version in ${FEDORA_VERS[@]}
do
    RETURN_STRING="${RETURN_STRING} package_cloud yank iofog/iofog-connector/fedora/${version} iofog-connector${ID}-${VERSION}-1.noarch.rpm;"
    RETURN_STRING="${RETURN_STRING} package_cloud push iofog/iofog-connector/fedora/${version} iofog-connector${ID}-${VERSION}-1.noarch.rpm;"
done

for version in ${REDHAT_VERS[@]}
do
    RETURN_STRING="${RETURN_STRING} package_cloud yank iofog/iofog-connector/el/${version} iofog-connector${ID}-${VERSION}-1.noarch.rpm;"
    RETURN_STRING="${RETURN_STRING} package_cloud push iofog/iofog-connector/el/${version} iofog-connector${ID}-${VERSION}-1.noarch.rpm;"
done

echo $RETURN_STRING
