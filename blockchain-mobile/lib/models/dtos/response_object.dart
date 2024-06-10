class ResponseObj<T> {
  final String status;
  final String message;
  final T data;
  ResponseObj(this.status, this.message, this.data);
  factory ResponseObj.fromJson(Map<String, dynamic> json, [T? data]) {
    if (data == null) {
      return ResponseObj(json['status'], json['message'], json['data']);
    }
    return ResponseObj(json['status'], json['message'], data);
  }
}
