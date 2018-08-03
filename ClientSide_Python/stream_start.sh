#/usr/bin/cvlc -A alsa,none --alsa-audio-device default v4l2:///dev/video0 --v4l2-width 480 --v4l2-height 340 --v4l2-chroma MJPG --v4l2-hflip 1 --v4l2-vflip 1 --sout '#transcode{fps=10}:standard{access=http{mime=multipart-mixed-replace},mux=ts,dst=:8554/}' -I dummy &
sudo raspivid -t 0 -w 300 -h 300 -ih -fps 20 -o - | nc -u 192.168.1.106 2223
#ffmpeg -threads 4 -framerate 20 -i /dev/video0 -c:v libx264 -vf scale=360:-1 -preset ultrafast -crf 35 -f h264 -pix_fmt yuv420p - | nc 192.168.1.106 2223
#ffmpeg -threads 4 -framerate 20 -i /dev/video0 -c:v libx264 -vf scale=360:-1 -preset ultrafast -crf 23 -f h264 -pix_fmt yuv420p - | nc 192.168.199.11 2223
#cat  /dev/video0 | nc 192.168.1.106 2223
