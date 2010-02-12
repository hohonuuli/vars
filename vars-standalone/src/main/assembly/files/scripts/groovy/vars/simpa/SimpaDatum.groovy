package vars.simpa
class SimpaDatum {
    int tileIndex
    Date date
    double x, y, z, roll, pitch, heading, width, height

    def SimpaDatum() { }

    def SimpaDatum(tileIndex, date, x, y, z, roll, pitch, heading, width, height) {
		this.tileIndex = tileIndex
		this.date = date
        this.x = x
        this.y = y
        this.z = z
        this.roll = roll
        this.pitch = pitch
        this.heading = heading
        this.width = width
        this.height = height
    }


}