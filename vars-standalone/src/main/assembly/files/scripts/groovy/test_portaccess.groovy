#!/usr/bin/env groovy

def portRange = 4050..4060

final socket = new ServerSocket(4055)
def ok = true
def masterThread = Thread.start {
    while (ok) {
        def c = socket.accept()
        def masterOutput = new OutputStreamWriter(c.outputStream)
        masterOutput.write("VARS")
        masterOutput.flush()
        c.close();
    }
}
Thread.sleep(1000)

for (i in portRange) {
  try {
    portCheck(i)
  }
  catch (Exception e) {
    println("Port $i is not open: $e")
  }

}


def portCheck(port) {
  def socket = new Socket("localhost", port);
  final input = new BufferedReader(new InputStreamReader(socket.inputStream))
  final inputThread = Thread.start {
    def msg = null
    while ((msg = input.readLine()) != null) {
        println(msg)
        if (msg == "VARS") {
            println("VARS was found on port $port")
        }
    }
  }
  def output = socket.outputStream
  output.write("VARS ping\n".bytes)
  output.flush()
  Thread.sleep(1000) {
      println("Interrupt")
  }
}