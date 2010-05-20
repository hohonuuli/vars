(import 
  '(java.net DatagramPacket DatagramSocket)
  '(java.util GregorianCalendar Calendar))
  
(defn fmt-time 
  "Format a java Calendar as a timecode"
  [cal] 
  (let [frames (* (/ (. cal get (Calendar/MILLISECOND)) 1000) 29.97)]
    (format "%tT:%02d" cal (Math/round frames))))
  
(defn run-generator 
  "Function for simulation a udp VCR"
  [port] 
  (println (str "Localtime timecode generator attached to port " port))
  (let [socket (new DatagramSocket port)
        buffer (make-array (Byte/TYPE) 4096)]
    (while true 
      (let [incoming (new DatagramPacket buffer 4096) 
            reply (fmt-time (new GregorianCalendar))]
         (. socket receive incoming)
         (let [outgoing (new DatagramPacket (. reply getBytes) (. reply length) (. incoming getAddress) (. incoming getPort))]
           (. socket send outgoing))))))
  
 (run-generator (Integer/parseInt (first *command-line-args*)))