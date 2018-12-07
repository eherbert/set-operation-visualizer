package app

object StringUtils {
  implicit class StringImprovements(val s: String) {

    // (A-Z) only
    def increment(): String = {
      var sArr = s.trim.toArray.map(_.toUpper)

      if (sArr.filterNot(_ == 'Z').length == 0) {
        sArr = Array.fill(sArr.length + 1)('A')
      } else {
        var rover = sArr.length - 1
        while (rover > (-1)) {
          if (sArr(rover) == 'Z') {
            sArr(rover) = 'A'
            rover -= 1
          } else {
            sArr(rover) = (sArr(rover).toInt + 1).toChar
            rover = -1
          }
        }
      }
      
      sArr.mkString("")
    }
  }
}