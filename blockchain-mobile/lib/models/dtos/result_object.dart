import 'package:blockchain_mobile/1_controllers/services/others/toast_service.dart';

class ResultObject<T> {
  bool isLoading;
  bool isSuccess;
  bool isError;
  String message;
  String error;
  T? result;

  ResultObject({
    this.isLoading = false,
    this.isSuccess = false,
    this.isError = false,
    this.message = '',
    this.error = '',
    this.result,
  });
  reset({
    isLoading = false,
    isSuccess = false,
    isError = false,
    message = '',
    error = '',
    T? result,
  }) =>
      {
        this.isLoading = isLoading,
        this.isSuccess = isSuccess,
        this.isError = isError,
        this.message = message,
        this.error = error,
        this.result = result
      };
  handleResult(context, {String? success, String? errorMsg}) {
    if (isSuccess == true) {
      ToastService.toastSuccess(context, success ?? message);
    }
    if (isError == true) {
      ToastService.toastError(context, errorMsg ?? error);
    }
  }

  @override
  String toString() {
    return 'ResultObject{isLoading: $isLoading, isSuccess: $isSuccess, isError: $isError, message: $message, error: $error, result: $result}';
  }
}
