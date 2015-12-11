## AVFoundation
## Notes for Video file playback interface

https://developer.apple.com/av-foundation/
http://www.raywenderlich.com/13418/how-to-play-record-edit-videos-in-ios
http://www.raywenderlich.com/13418/how-to-play-record-edit-videos-in-ios
http://www.slideshare.net/invalidname/capturing-stills-sounds-and-scenes-with-av-foundation
http://maniacdev.com/2013/03/source-code-examples-covering-avfoundation-video-basics-and-how-to-create-a-video-editor
http://www.slideshare.net/invalidname/capturing-stills-sounds-and-scenes-with-av-foundation

http://maniacdev.com/2013/03/source-code-examples-covering-avfoundation-video-basics-and-how-to-create-a-video-editor

http://www.raywenderlich.com/30200/avfoundation-tutorial-adding-overlays-and-animations-to-videos

http://abdulazeem.wordpress.com/tag/avfoundation/

http://iosguy.com/tag/avfoundation/

http://stackoverflow.com/questions/10123045/cocoa-whats-the-equivalent-of-a-uiviewcontroller-subclass-in-a-single-window

## VLC

http://n0tablog.wordpress.com/2009/02/09/controlling-vlc-via-rc-remote-control-interface-using-a-unix-domain-socket-and-no-programming/

https://wiki.videolan.org/Interfaces
https://developer.apple.com/library/ios/DOCUMENTATION/AudioVideo/Conceptual/AVFoundationPG/Articles/02_Playback.html

https://sites.google.com/site/sachinkagarwal/home/code-snippets/remote-controlling-vlc-player

https://code.google.com/p/android-vlc-remote/source/browse/src/org/peterbaldwin/vlcremote/net/MediaServer.java

### Remote Control Interface

From `/Applications/VLC.app/Contents/MacOS/VLC -H`

```

Remote control interface
     --rc-show-pos, --no-rc-show-pos
                                Show stream position (default disabled)
         Show the current position in seconds within the stream from time to time. (default disabled)
     --rc-fake-tty, --no-rc-fake-tty
                                Fake TTY (default disabled)
         Force the rc module to use stdin as if it was a TTY. (default disabled)
     --rc-unix <string>         UNIX socket command input
         Accept commands over a Unix socket rather than stdin.
     --rc-host <string>         TCP command input
         Accept commands over a socket rather than stdin. You can set the address and port the interface will bind to.
```
### Snapshot

```
Snapshot:
    --snapshot-path <string>   Video snapshot directory (or filename)
        Directory where the video snapshots will be stored.
    --snapshot-prefix <string> Video snapshot file prefix
        Video snapshot file prefix
    --snapshot-format {png,jpg,tiff}
                               Video snapshot format
        Image format which will be used to store the video snapshots
    --snapshot-preview, --no-snapshot-preview
                           Display video snapshot preview (default enabled)
    Display the snapshot preview in the screen's top-left corner. (default enabled)
    --snapshot-sequential, --no-snapshot-sequential
                               Use sequential numbers instead of timestamps (default disabled)
        Use sequential numbers instead of timestamps for snapshot numbering (default disabled)
    --snapshot-width <integer [-2147483648 .. 2147483647]>
                               Video snapshot width
        You can enforce the width of the video snapshot. By default it will keep the original width (-1). Using 0
        will scale the width to keep the aspect ratio.
    --snapshot-height <integer [-2147483648 .. 2147483647]>
                               Video snapshot height
        You can enforce the height of the video snapshot. By default it will keep the original height (-1). Using 0
        will scale the height to keep the aspect ratio.
```

### Playback Control

```
Playback control:
    --input-repeat <integer [-2147483648 .. 2147483647]>
                               Input repetitions
        Number of time the same input will be repeated
    --start-time <float>       Start time
        The stream will start at this position (in seconds).
    --stop-time <float>        Stop time
        The stream will stop at this position (in seconds).
    --run-time <float>         Run time
        The stream will run this duration (in seconds).
    --input-fast-seek, --no-input-fast-seek
                               Fast seek (default disabled)
        Favor speed over precision while seeking (default disabled)
    --rate <float>             Playback speed
        This defines the playback speed (nominal speed is 1.0).
    --input-list <string>      Input list
        You can give a comma-separated list of inputs that will be concatenated together after the normal one.
    --input-slave <string>     Input slave (experimental)
        This allows you to play from several inputs at the same time. This feature is experimental, not all formats
        are supported. Use a '#' separated list of inputs.
    --bookmarks <string>       Bookmarks list for a stream
        You can manually give a list of bookmarks for a stream in the form "{name=bookmark-name,time=optional-time-off
        set,bytes=optional-byte-offset},{...}"
```

### Network Settings
```
Network settings:
    --mtu <integer [-2147483648 .. 2147483647]>
                               MTU of the network interface
        This is the maximum application-layer packet size that can be transmitted over the network (in bytes).
    --ipv4-timeout <integer [-2147483648 .. 2147483647]>
                               TCP connection timeout
        Default TCP connection timeout (in milliseconds).
    --http-host <string>       HTTP server address
        By default, the server will listen on any local IP address. Specify an IP address (e.g. ::1 or 127.0.0.1) or
        a host name (e.g. localhost) to restrict them to a specific network interface.
    --http-port <integer [1 .. 65535]>
                               HTTP server port
        The HTTP server will listen on this TCP port. The standard HTTP port number is 80. However allocation of port
        numbers below 1025 is usually restricted by the operating system.
    --https-port <integer [1 .. 65535]>
                               HTTPS server port
        The HTTPS server will listen on this TCP port. The standard HTTPS port number is 443. However allocation of
        port numbers below 1025 is usually restricted by the operating system.
    --rtsp-host <string>       RTSP server address
        This defines the address the RTSP server will listen on, along with the base path of the RTSP VOD media.
        Syntax is address/path. By default, the server will listen on any local IP address. Specify an IP address
        (e.g. ::1 or 127.0.0.1) or a host name (e.g. localhost) to restrict them to a specific network interface.
    --rtsp-port <integer [1 .. 65535]>
                               RTSP server port
        The RTSP server will listen on this TCP port. The standard RTSP port number is 554. However allocation of
        port numbers below 1025 is usually restricted by the operating system.
    --http-cert <string>       HTTP/TLS server certificate
        This X.509 certicate file (PEM format) is used for server-side TLS.
    --http-key <string>        HTTP/TLS server private key
        This private key file (PEM format) is used for server-side TLS.
    --http-ca <string>         HTTP/TLS Certificate Authority
        This X.509 certificate file (PEM format) can optionally be used to authenticate remote clients in TLS
        sessions.
    --http-crl <string>        HTTP/TLS Certificate Revocation List
        This file contains an optional CRL to prevent remote clients from using revoked certificates in TLS sessions.
```

### Examples

#### HTTP interface

Start: `/Applications/VLC.app/Contents/MacOS/VLC --extraintf luahttp --http-password vars`

Open a browser at http://localhost:8080. Login using password "vars" or whatever we set in VLC preferences; leave the username blank. Then click "view source" to see a list of http commands

#### RC interface

Start: `/Applications/VLC.app/Contents/MacOS/VLC -I macosx --rc-host localhost:<port>`
Example: `Applications/VLC.app/Contents/MacOS/VLC --extraintf rc --rc-host=localhost:8080`
