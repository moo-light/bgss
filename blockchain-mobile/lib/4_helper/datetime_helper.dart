//[2024,4,9,14,0,20,288134700]
String convertToISOString(List<dynamic> dateTimeList) {
  // Create a DateTime object from the provided list
  DateTime dateTime = DateTime(
    dateTimeList[0], // year
    dateTimeList[1], // month
    dateTimeList[2], // day
    dateTimeList[3], // hour
    dateTimeList[4], // minute
    dateTimeList[5], // second
    dateTimeList[6] ~/ 1000, // millisecond (convert to microseconds)
  );

  // Convert the DateTime to ISO string format
  String isoString = dateTime.toIso8601String();

  return isoString;
}
