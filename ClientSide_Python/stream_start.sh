cvlc -A alsa,none --alsa-audio-device default v4l2:///dev/video0 --v4l2-width 480 --v4l2-height 340 --v4l2-chroma MJPG --v4l2-hflip 1 --v4l2-vflip 1 --sout '#transcode{fps=10}:standard{access=http{mime=multipart-mixed-replace},mux=mpjpeg,dst=:8554/}' -I dummy &
