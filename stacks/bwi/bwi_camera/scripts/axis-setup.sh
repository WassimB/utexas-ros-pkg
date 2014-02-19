function valid_ip() {
    local  ip=$1
    local  stat=1

    if [[ $ip =~ ^[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}$ ]]; then
        OIFS=$IFS
        IFS='.'
        ip=($ip)
        IFS=$OIFS
        [[ ${ip[0]} -le 255 && ${ip[1]} -le 255 \
            && ${ip[2]} -le 255 && ${ip[3]} -le 255 ]]
        stat=$?
    fi
    return $stat
}
clear
read -p "Select the name you want to use for the camera: " camname
echo
read -p "Enter the hostname or IP address of your axis camera: " hostname
if false && valid_ip $hostname; then # disabling hostnames for now, they fail when switching between networks
  ip=$hostname
  hostname=$(avahi-resolve-address $ip | awk '{print $2}')
fi
echo
read -p "Enter the username for your axis camera: " username
echo
read -p "Enter the password for your axis camera: " -s password
echo
echo
echo "Select a map for the camera to use:"
cd $(rospack find bwi_map_server)/maps
list=$(ls -l *.yaml | grep -v '\-points\.' | awk '{print $9}' | sed 's/\.yaml//')
echo $list
read -p ">> " mapname
echo
example=$(rospack find bwi_map_server)/maps/$mapname-points.yaml
echo "Add optional data points to $example. These points will appear on the displayed map for simplifying correspondence selections."
read -s -n 1 -p "Open this file in vim for editing? (y/n)" result
echo
if [ $result == "y" ]; then
  vim $example
  echo "Finished editing example points."
else
  echo "Skipped example points."
fi
echo
read -s -p "Run the transform producer and create correspondences between the camera frame and the map by clicking points in each sequentially. When four correspondences have been chosen, the producer will output a valid transform file with each response. The file will be written to $(rospack find bwi_camera)/launch/$camname-tf.launch. You may stop the transform producer at any point after the transform file has been created. Press [Enter] to continue."
cat $(rospack find bwi_camera)/launch/axis-template.launch | sed s/##CAMERA_NAME##/$camname/ | sed s/##HOSTNAME##/$hostname/ | sed s/##USERNAME##/$username/ | sed s/##PASSWORD##/$password/ > $(rospack find bwi_camera)/launch/$camname.launch
$(rospack find bwi_camera)/scripts/cameras.py add "$camname" "$(rospack find bwi_camera)/cameras.yaml"
roslaunch bwi_camera axis-setup.launch camname:=$camname username:=$username hostname:=$hostname password:=$password mapname:=$mapname